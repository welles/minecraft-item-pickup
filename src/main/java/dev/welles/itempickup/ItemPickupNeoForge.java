package dev.welles.itempickup;

//? if neoforge {
/*import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(ItemPickup.MOD_ID)
public class ItemPickupNeoForge {
    public ItemPickupNeoForge(IEventBus modBus) {
        ItemPickup.init(FMLPaths.CONFIGDIR.get(), (player, payload) -> player.connection.send(payload));
        modBus.addListener(ItemPickupNeoForge::registerPayloads);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) ItemPickup.onDisconnect(player);
        });
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> ItemPickup.onServerTick(event.getServer()));
    }

    // Optional channels: players without the mod can still join and pick up items like in vanilla.
    // Handlers run on the main thread; the client handler only runs on clients.
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").optional()
            .playToServer(ModePayload.TYPE, ModePayload.CODEC, (payload, context) -> {
                if (context.player() instanceof ServerPlayer player) ItemPickup.setMode(player, payload.mode());
            })
            .playToServer(PickupPayload.TYPE, PickupPayload.CODEC, (payload, context) -> {
                if (context.player() instanceof ServerPlayer player) ItemPickup.pickUp(player, payload.entityId());
            })
            .playToClient(RangePayload.TYPE, RangePayload.CODEC, (payload, context) -> ItemPickupClient.setRange(payload.range()));
    }
}
*///?}
