package dev.welles.itempickup;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Settings stored in {@code config/itempickup.properties}.
 * The file is re-read automatically whenever it changes on disk.
 */
public final class PickupConfig {
    /** Vanilla picks up items touching the player's bounding box grown by 1 block sideways and 0.5 up and down. */
    public static final double DEFAULT_RANGE = 1.0;
    public static final double MAX_RANGE = 16.0;

    public PickupMode defaultMode = PickupMode.VANILLA;
    public double range = DEFAULT_RANGE;
    public boolean outline = true;
    public boolean showName = true;

    private static final String HEADER = """
        Item Pickup configuration
        Changes are picked up automatically, no restart required.
        range is decided by the server (in multiplayer the server's value applies), all other settings only affect the client.
        The keys are changed in Options > Controls > Key Binds > Gameplay.

        defaultMode  Pickup mode when the game starts. vanilla = items are picked up by walking over them
                                                       manual  = look at an item and press the pick up key (right-click by default)
                     The toggle key switches the mode while playing. Changing this value switches to the new mode immediately.
        range        How far items can be picked up manually, in blocks around the player's body (sideways; up and down half
                     of it). 1.0 = the area in which vanilla picks up items automatically. 0 to 16.
        outline      true = outline the item that the pick up key would pick up in manual mode
        showName     true = show the name of that item below the crosshair""";

    private static Path file;
    private static FileTime loadedAt;
    private static PickupConfig current = new PickupConfig();

    public static synchronized void init(Path configDir) {
        file = configDir.resolve(ItemPickup.MOD_ID + ".properties");
        reload();
    }

    /** Returns the current settings, re-reading the file if it was modified. */
    public static synchronized PickupConfig get() {
        if (file != null) {
            try {
                FileTime modified = Files.exists(file) ? Files.getLastModifiedTime(file) : null;
                if (modified == null || !modified.equals(loadedAt)) reload();
            } catch (IOException e) {
                ItemPickup.LOGGER.warn("Could not check config file {}", file, e);
            }
        }
        return current;
    }

    private static void reload() {
        PickupConfig config = new PickupConfig();
        Properties props = new Properties();
        boolean complete = false;

        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                props.load(reader);
                complete = config.read(props);
            } catch (IOException | RuntimeException e) {
                ItemPickup.LOGGER.error("Could not read config file {}, using defaults", file, e);
                current = config;
                return;
            }
        }

        if (!complete) {
            try {
                Files.createDirectories(file.getParent());
                try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                    config.write(writer);
                }
            } catch (IOException e) {
                ItemPickup.LOGGER.error("Could not write config file {}", file, e);
            }
        }

        try {
            loadedAt = Files.exists(file) ? Files.getLastModifiedTime(file) : null;
        } catch (IOException e) {
            loadedAt = null;
        }
        current = config;
    }

    /** Applies all valid values present in {@code props}; returns whether every key was present. */
    private boolean read(Properties props) {
        String value = props.getProperty("defaultMode");
        if (value != null) {
            PickupMode mode = PickupMode.byId(value);
            if (mode != null) defaultMode = mode;
            else ItemPickup.LOGGER.warn("Unknown defaultMode '{}' in {}, using {}", value.trim(), file, defaultMode.id());
        }
        value = props.getProperty("range");
        if (value != null) {
            double parsed = parseRange(value);
            if (!Double.isNaN(parsed)) range = parsed;
            else ItemPickup.LOGGER.warn("Invalid range '{}' in {}, using {}", value.trim(), file, range);
        }
        value = props.getProperty("outline");
        if (value != null) outline = Boolean.parseBoolean(value.trim());
        value = props.getProperty("showName");
        if (value != null) showName = Boolean.parseBoolean(value.trim());
        return props.keySet().containsAll(toProperties().keySet());
    }

    /** Parses a range clamped to 0 to {@link #MAX_RANGE}, returns NaN if it is not a number. */
    private static double parseRange(String value) {
        try {
            double range = Double.parseDouble(value.trim());
            return Double.isNaN(range) ? Double.NaN : Math.clamp(range, 0.0, MAX_RANGE);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private void write(Writer writer) throws IOException {
        for (String line : HEADER.split("\n")) writer.write(("# " + line).stripTrailing() + "\n");
        writer.write("\n");
        for (var entry : toProperties().entrySet()) {
            writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
        }
    }

    private Map<String, String> toProperties() {
        Map<String, String> props = new LinkedHashMap<>();
        props.put("defaultMode", defaultMode.id());
        props.put("range", String.valueOf(range));
        props.put("outline", String.valueOf(outline));
        props.put("showName", String.valueOf(showName));
        return props;
    }
}
