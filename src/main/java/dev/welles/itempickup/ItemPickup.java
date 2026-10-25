package dev.welles.itempickup;

import dev.welles.itempickup.mixin.ItemEntityAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

public final class ItemPickup {
    public static final String MOD_ID = "itempickup";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** Extra distance beyond the block interaction range, so lag does not make pickups fail. */
    private static final double RANGE_TOLERANCE = 1.0;
    /** Pickup delay of items that can never be picked up, see {@code ItemEntity#setNeverPickUp}. */
    private static final int NEVER_PICK_UP = 32767;

    /** Players on this server in manual mode. Players without the client mod are never in here. */
    private static final Set<UUID> MANUAL_PLAYERS = new HashSet<>();
    /** The player that is picking up an item manually right now. */
    private static Player pickingUp;

    private ItemPickup() {}

    public static void init(Path configDir) {
        PickupConfig.init(configDir);
        LOGGER.info("Item Pickup loaded");
    }

    //? if >=1.21.11 {
    /*public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
    *///?} else {
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
    //?}

    public static void setMode(ServerPlayer player, PickupMode mode) {
        if (mode == PickupMode.MANUAL) MANUAL_PLAYERS.add(player.getUUID());
        else MANUAL_PLAYERS.remove(player.getUUID());
    }

    public static void onDisconnect(ServerPlayer player) {
        MANUAL_PLAYERS.remove(player.getUUID());
    }

    /** Whether the player picks up items by touching them, like in vanilla. */
    public static boolean picksUpOnTouch(Player player) {
        return player == pickingUp || !MANUAL_PLAYERS.contains(player.getUUID());
    }

    /**
     * Picks up the item the player looked at when pressing the pick up key. Vanilla's pickup runs as if the player
     * touched the item, so full inventories, item owners and pickup events of other mods behave like in vanilla. Only
     * the pickup delay of freshly dropped items is skipped, the player asked for the item explicitly.
     */
    public static void pickUp(ServerPlayer player, int entityId) {
        if (!player.isAlive() || player.isSpectator()) return;
        if (!(player.level().getEntity(entityId) instanceof ItemEntity item) || !item.isAlive()) return;

        double range = player.blockInteractionRange() + RANGE_TOLERANCE;
        if (item.getBoundingBox().distanceToSqr(player.getEyePosition()) > range * range) return;

        int delay = ((ItemEntityAccessor) item).itempickup$getPickupDelay();
        if (delay == NEVER_PICK_UP) return;

        item.setNoPickUpDelay();
        pickingUp = player;
        try {
            item.playerTouch(player);
        } finally {
            pickingUp = null;
            // e.g. the inventory was full
            if (item.isAlive()) item.setPickUpDelay(delay);
        }
    }
}
