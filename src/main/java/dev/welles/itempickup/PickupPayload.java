package dev.welles.itempickup;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Asks the server to pick up the item entity the player is looking at. */
public record PickupPayload(int entityId) implements CustomPacketPayload {
    public static final Type<PickupPayload> TYPE = new Type<>(ItemPickup.id("pick_up"));
    public static final StreamCodec<FriendlyByteBuf, PickupPayload> CODEC = StreamCodec.ofMember(PickupPayload::write, PickupPayload::new);

    private PickupPayload(FriendlyByteBuf buf) {
        this(buf.readVarInt());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
    }

    @Override
    public Type<PickupPayload> type() {
        return TYPE;
    }
}
