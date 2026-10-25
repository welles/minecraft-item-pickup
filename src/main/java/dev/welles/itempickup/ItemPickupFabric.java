package dev.welles.itempickup;

//? if fabric {
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public class ItemPickupFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ItemPickup.init(FabricLoader.getInstance().getConfigDir(), ServerPlayNetworking::send);

        //? if >=26.1 {
        /*PayloadTypeRegistry.serverboundPlay().register(ModePayload.TYPE, ModePayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(PickupPayload.TYPE, PickupPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(RangePayload.TYPE, RangePayload.CODEC);
        *///?} else {
        PayloadTypeRegistry.playC2S().register(ModePayload.TYPE, ModePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PickupPayload.TYPE, PickupPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RangePayload.TYPE, RangePayload.CODEC);
        //?}
        // Handlers run on the server thread
        ServerPlayNetworking.registerGlobalReceiver(ModePayload.TYPE, (payload, context) -> ItemPickup.setMode(context.player(), payload.mode()));
        ServerPlayNetworking.registerGlobalReceiver(PickupPayload.TYPE, (payload, context) -> ItemPickup.pickUp(context.player(), payload.entityId()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> ItemPickup.onDisconnect(handler.player));
        ServerTickEvents.END_SERVER_TICK.register(ItemPickup::onServerTick);
    }
}
//?}
