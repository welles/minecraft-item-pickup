package dev.welles.itempickup.recordings;

import dev.welles.itempickup.ItemPickup;
import dev.welles.itempickup.ItemPickupClient;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Function;

/**
 * Plays scenes of the mod and saves every tick as a frame in {@code recordings/<scene>/} of the run directory, for
 * {@code record-gifs.sh} to turn into GIFs. Not a test: it checks nothing and only runs on request.
 */
public class ItemPickupRecordings implements FabricClientGameTest {
    private static final int WIDTH = 854;
    private static final int HEIGHT = 480;
    /** The flat world's surface, where the player and the items stand. */
    private static final int GROUND = -60;
    private static final double EYE_HEIGHT = 1.62;
    /** Dropped items hover and bob a little above the floor. */
    private static final double ITEM_HEIGHT = 0.2;
    /** How long the view takes to turn to the next item and rests on it before picking it up. */
    private static final int TURN_TICKS = 8;
    private static final int HOVER_TICKS = 20;

    @Override
    public void runTest(ClientGameTestContext context) {
        setConfig("defaultMode=manual\n");
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            TestServerContext server = singleplayer.getServer();
            server.runCommand("gamemode survival @a");
            server.runCommand("time set noon");
            // a calm floor: the noisy grass texture changes in every frame of a moving view and bloats the GIFs
            server.runCommand("fill -40 " + (GROUND - 1) + " -40 40 " + (GROUND - 1) + " 40 minecraft:light_gray_concrete");
            singleplayer.getClientWorld().waitForChunksRender();

            manualSweep(context, server);
            walkOver(context, server);
        }
    }

    /** Clears the inventory and the floor and puts the player back in the middle, facing south or north. */
    private static void reset(ClientGameTestContext context, TestServerContext server, float yaw) {
        server.runCommand("clear @a");
        server.runCommand("kill @e[type=item]");
        server.runCommand(String.format(Locale.ROOT, "tp @a 0.5 %d 0.5 %.1f 0", GROUND, yaw));
        context.waitTicks(5);
    }

    /** Drops an item stack at the given offset from the middle. */
    private static void drop(TestServerContext server, String item, int count, double x, double z) {
        server.runCommand(String.format(Locale.ROOT, "summon item %.2f %d %.2f {Item:{id:\"minecraft:%s\",count:%d},PickupDelay:0}",
            0.5 + x, GROUND, 0.5 + z, item, count));
    }

    /**
     * In manual mode, looks at one drop of a pile after the other, from left to right, and picks it up with the pick up
     * key after a moment, so the outline and the label of each can be seen.
     */
    private static void manualSweep(ClientGameTestContext context, TestServerContext server) {
        reset(context, server, 0);
        // south of the player, within the default range of 2 blocks: item, count, x, z
        Object[][] items = {
            {"diamond", 3, -1.4, 1.6}, {"golden_apple", 1, -0.7, 1.9}, {"iron_ingot", 8, 0.0, 1.5},
            {"emerald", 2, 0.7, 1.9}, {"redstone", 16, 1.4, 1.6},
        };
        for (Object[] item : items) drop(server, (String) item[0], (int) item[1], (double) item[2], (double) item[3]);
        context.waitTicks(30);

        Recorder recorder = new Recorder(context, "manual-sweep");
        float yaw = 70;
        float pitch = 40;
        look(context, yaw, pitch);
        recorder.frames(10);
        for (Object[] item : items) {
            double x = (double) item[2];
            double z = (double) item[3];
            // from the eyes to the middle of the item hovering just above the floor
            float targetYaw = (float) Math.toDegrees(Math.atan2(-x, z));
            float targetPitch = (float) Math.toDegrees(Math.atan2(EYE_HEIGHT - ITEM_HEIGHT, Math.hypot(x, z)));
            for (int i = 1; i <= TURN_TICKS; i++) {
                float t = (float) i / TURN_TICKS;
                // eases in and out
                t = t * t * (3 - 2 * t);
                look(context, yaw + (targetYaw - yaw) * t, pitch + (targetPitch - pitch) * t);
                recorder.frame();
            }
            yaw = targetYaw;
            pitch = targetPitch;
            recorder.frames(HOVER_TICKS);
            context.getInput().pressKey(ItemPickupClient.PICK_UP);
            recorder.frames(3);
        }
        recorder.frames(20);
    }

    /**
     * Seen from behind in third person: switches to manual mode with the toggle key, steps sideways over a row of drops,
     * which leaves them lying, switches back to vanilla mode and steps back over them, which picks them up.
     */
    private static void walkOver(ClientGameTestContext context, TestServerContext server) {
        reset(context, server, 0);
        // starts in vanilla mode, changing the config switches without a message
        setConfig("defaultMode=vanilla\n");
        // a row on the player's left (east, it faces south), where the camera behind it sees them all, out of reach of
        // the vanilla pickup at the start
        drop(server, "gold_ingot", 4, 1.8, 0);
        drop(server, "apple", 2, 2.6, 0);
        drop(server, "lapis_lazuli", 12, 3.4, 0);
        context.waitTicks(30);
        context.runOnClient(minecraft -> minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK));

        Recorder recorder = new Recorder(context, "walk-over");
        look(context, 0, 25);
        recorder.frames(10);
        context.getInput().pressKey(ItemPickupClient.TOGGLE_MODE);
        recorder.frames(30);
        walk(recorder, context, options -> options.keyLeft, 30);
        recorder.frames(10);
        context.getInput().pressKey(ItemPickupClient.TOGGLE_MODE);
        recorder.frames(30);
        walk(recorder, context, options -> options.keyRight, 30);
        recorder.frames(25);
        context.runOnClient(minecraft -> minecraft.options.setCameraType(CameraType.FIRST_PERSON));
    }

    private static void walk(Recorder recorder, ClientGameTestContext context, Function<Options, KeyMapping> key, int ticks) {
        context.getInput().holdKey(key);
        recorder.frames(ticks);
        context.getInput().releaseKey(key);
    }

    /** Turns the player, without the interpolation from the last rotation. */
    private static void look(ClientGameTestContext context, float yaw, float pitch) {
        context.runOnClient(minecraft -> {
            var player = minecraft.player;
            player.setYRot(yaw);
            player.setXRot(pitch);
            player.yRotO = yaw;
            player.xRotO = pitch;
        });
    }

    private static void setConfig(String content) {
        Path file = FabricLoader.getInstance().getConfigDir().resolve(ItemPickup.MOD_ID + ".properties");
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Saves numbered frames of one scene; taking a screenshot advances the game by one tick. */
    private static final class Recorder {
        private final ClientGameTestContext context;
        private final Path dir;
        private int frame;

        Recorder(ClientGameTestContext context, String scene) {
            this.context = context;
            this.dir = FabricLoader.getInstance().getGameDir().resolve("recordings").resolve(scene);
        }

        void frame() {
            // picking up items unlocks advancements and recipes, their toasts and chat messages would cover the scene
            context.runOnClient(minecraft -> {
                minecraft.getToastManager().clear();
                minecraft.gui.getChat().clearMessages(false);
            });
            context.takeScreenshot(TestScreenshotOptions.of(String.format(Locale.ROOT, "%04d", frame++))
                .disableCounterPrefix().withSize(WIDTH, HEIGHT).withDestinationDir(dir));
        }

        void frames(int count) {
            for (int i = 0; i < count; i++) frame();
        }
    }
}
