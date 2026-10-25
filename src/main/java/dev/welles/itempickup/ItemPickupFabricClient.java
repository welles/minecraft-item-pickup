package dev.welles.itempickup;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

//? if >=26.1 {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
*///?} else {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?}

public class ItemPickupFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemPickupClient.init(new ItemPickupClient.Network() {
            @Override
            public boolean serverSupported() {
                return ClientPlayNetworking.canSend(PickupPayload.TYPE);
            }

            @Override
            public void send(CustomPacketPayload payload) {
                ClientPlayNetworking.send(payload);
            }
        });

        //? if >=26.1 {
        /*KeyMappingHelper.registerKeyMapping(ItemPickupClient.TOGGLE_MODE);
        KeyMappingHelper.registerKeyMapping(ItemPickupClient.PICK_UP);
        *///?} else {
        KeyBindingHelper.registerKeyBinding(ItemPickupClient.TOGGLE_MODE);
        KeyBindingHelper.registerKeyBinding(ItemPickupClient.PICK_UP);
        //?}
        ClientTickEvents.END_CLIENT_TICK.register(ItemPickupClient::tick);
    }
}
//?}
