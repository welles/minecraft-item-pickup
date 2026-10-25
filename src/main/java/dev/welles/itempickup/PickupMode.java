package dev.welles.itempickup;

import java.util.Locale;

public enum PickupMode {
    /** Items are picked up by walking over them. */
    VANILLA,
    /** Items are picked up by looking at them and pressing the pick up key. */
    MANUAL;

    /** The name used in the config file and translation keys. */
    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    /** Parses a config value, returns {@code null} for unknown values. */
    public static PickupMode byId(String id) {
        for (PickupMode mode : values()) {
            if (mode.id().equalsIgnoreCase(id.trim())) return mode;
        }
        return null;
    }
}
