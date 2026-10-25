package dev.welles.itempickup;

//? if neoforge {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Objects;

@Mod(value = ItemPickup.MOD_ID, dist = Dist.CLIENT)
public class ItemPickupNeoForgeClient {
    public ItemPickupNeoForgeClient(IEventBus modBus) {
        ItemPickupClient.init(new ItemPickupClient.Network() {
            @Override
            public boolean serverSupported() {
                return connection().hasChannel(PickupPayload.TYPE);
            }

            @Override
            public void send(CustomPacketPayload payload) {
                connection().send(payload);
            }

            private static ClientPacketListener connection() {
                return Objects.requireNonNull(Minecraft.getInstance().getConnection());
            }
        });

        modBus.addListener((RegisterKeyMappingsEvent event) -> {
            event.register(ItemPickupClient.TOGGLE_MODE);
            event.register(ItemPickupClient.PICK_UP);
        });
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> ItemPickupClient.tick(Minecraft.getInstance()));
    }
}
*///?}
