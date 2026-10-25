package dev.welles.itempickup;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

//? if >=26.3 {
/*import net.minecraft.world.item.component.SwingAnimation;
*///?}

/** Client side of manual pickup: the mode, the keys and the item the player is looking at. */
public final class ItemPickupClient {
    /** Items are small, so they can also be targeted a bit next to their bounding box. */
    private static final double TARGET_MARGIN = 0.2;
    /** Ticks between pickups while the key is held, like the use key repeats. */
    private static final int REPEAT_DELAY = 4;

    //? if >=1.21.9 {
    /*private static final KeyMapping.Category CATEGORY = KeyMapping.Category.GAMEPLAY;
    *///?} else {
    private static final String CATEGORY = "key.categories.gameplay";
    //?}
    //? if >=26.3 {
    /*public static final KeyMapping TOGGLE_MODE = new KeyMapping("key.itempickup.toggle_mode", InputConstants.Type.KEYBOARD, InputConstants.KEY_K, CATEGORY);
    *///?} else {
    public static final KeyMapping TOGGLE_MODE = new KeyMapping("key.itempickup.toggle_mode", InputConstants.Type.KEYSYM, InputConstants.KEY_K, CATEGORY);
    //?}
    public static final KeyMapping PICK_UP = new KeyMapping("key.itempickup.pick_up", InputConstants.Type.MOUSE, InputConstants.MOUSE_BUTTON_RIGHT, CATEGORY);

    private static Network network;
    private static PickupMode mode;
    private static PickupMode configuredMode;

    /** The connection and mode the server was last told about. */
    private static ClientPacketListener syncedConnection;
    private static PickupMode syncedMode;

    private static ItemEntity target;
    private static int heldTicks;
    /** Set after a pickup until the key is released, so a held use key does not use the item in hand afterwards. */
    private static boolean useBlocked;

    private ItemPickupClient() {}

    /** Loader specific networking. */
    public interface Network {
        /** Whether the server has the mod. Only called while connected. */
        boolean serverSupported();

        void send(CustomPacketPayload payload);
    }

    public static void init(Network network) {
        ItemPickupClient.network = network;
    }

    public static void tick(Minecraft minecraft) {
        PickupConfig config = PickupConfig.get();
        // the configured mode applies on start and whenever it is changed in the file
        if (config.defaultMode != configuredMode) mode = configuredMode = config.defaultMode;
        // no frames are picked without a world
        if (minecraft.level == null) target = null;

        while (TOGGLE_MODE.consumeClick()) toggleMode(minecraft);
        syncMode(minecraft);

        boolean clicked = false;
        while (PICK_UP.consumeClick()) clicked = true;
        if (PICK_UP.isDown()) {
            heldTicks++;
        } else {
            heldTicks = 0;
            useBlocked = false;
        }
        if (clicked || heldTicks > 0 && heldTicks % REPEAT_DELAY == 0) pickUp(minecraft);
    }

    private static void toggleMode(Minecraft minecraft) {
        mode = mode == PickupMode.MANUAL ? PickupMode.VANILLA : PickupMode.MANUAL;
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        Component message;
        if (mode == PickupMode.VANILLA) message = Component.translatable("itempickup.mode.vanilla");
        else if (serverSupported(minecraft)) message = Component.translatable("itempickup.mode.manual", PICK_UP.getTranslatedKeyMessage());
        else message = Component.translatable("itempickup.mode.unavailable");
        //? if >=26.1 {
        /*player.sendOverlayMessage(message);
        *///?} else {
        player.displayClientMessage(message, true);
        //?}
    }

    /** Tells the server the mode after joining and whenever it changes. */
    private static void syncMode(Minecraft minecraft) {
        ClientPacketListener connection = minecraft.getConnection();
        if (connection == null || minecraft.player == null) {
            syncedConnection = null;
            return;
        }
        if ((connection != syncedConnection || mode != syncedMode) && network.serverSupported()) {
            network.send(new ModePayload(mode));
            syncedConnection = connection;
            syncedMode = mode;
        }
    }

    private static boolean serverSupported(Minecraft minecraft) {
        return minecraft.getConnection() != null && network.serverSupported();
    }

    private static void pickUp(Minecraft minecraft) {
        if (target == null || minecraft.player == null) return;
        network.send(new PickupPayload(target.getId()));
        //? if >=26.3 {
        /*minecraft.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
        *///?} else {
        minecraft.player.swing(InteractionHand.MAIN_HAND);
        //?}
        useBlocked = true;
    }

    /** Called every frame after vanilla picked the block or entity under the crosshair. */
    public static void updateTarget(Minecraft minecraft, float partialTick) {
        target = mode == PickupMode.MANUAL && serverSupported(minecraft) ? findTarget(minecraft, partialTick) : null;
    }

    /** The nearest item along the line of sight within block interaction range. */
    private static ItemEntity findTarget(Minecraft minecraft, float partialTick) {
        Entity camera = minecraft.getCameraEntity();
        LocalPlayer player = minecraft.player;
        if (camera == null || player == null || minecraft.level == null || player.isSpectator()) return null;

        Vec3 eye = camera.getEyePosition(partialTick);
        Vec3 end = eye.add(camera.getViewVector(partialTick).scale(player.blockInteractionRange()));
        // Solid blocks hide items behind them, grass and flowers do not
        double maxDistance = eye.distanceToSqr(minecraft.level.clip(
            new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, camera)
        ).getLocation());
        // So does an entity under the crosshair, e.g. a villager standing on the item
        if (minecraft.hitResult instanceof EntityHitResult entityHit) {
            maxDistance = Math.min(maxDistance, eye.distanceToSqr(entityHit.getLocation()));
        }

        ItemEntity nearest = null;
        for (ItemEntity item : minecraft.level.getEntitiesOfClass(ItemEntity.class, new AABB(eye, end).inflate(TARGET_MARGIN), Entity::isAlive)) {
            AABB box = item.getBoundingBox().inflate(TARGET_MARGIN);
            Optional<Vec3> hit = box.contains(eye) ? Optional.of(eye) : box.clip(eye, end);
            if (hit.isEmpty()) continue;
            double distance = eye.distanceToSqr(hit.get());
            if (distance < maxDistance) {
                maxDistance = distance;
                nearest = item;
            }
        }
        return nearest;
    }

    /**
     * Whether a press of the use key picks up an item instead of using the item in hand. Only when both actions are on
     * the same key (the default), so clicking an item on the ground does not also place a block.
     */
    public static boolean blocksUse(Minecraft minecraft) {
        return (target != null || useBlocked) && PICK_UP.same(minecraft.options.keyUse);
    }
}
