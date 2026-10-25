package dev.welles.itempickup;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Tells a client in manual mode the server's pickup range, so it only targets items that can be picked up. */
public record RangePayload(double range) implements CustomPacketPayload {
    public static final Type<RangePayload> TYPE = new Type<>(ItemPickup.id("range"));
    public static final StreamCodec<FriendlyByteBuf, RangePayload> CODEC = StreamCodec.ofMember(RangePayload::write, RangePayload::new);

    private RangePayload(FriendlyByteBuf buf) {
        this(buf.readDouble());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeDouble(range);
    }

    @Override
    public Type<RangePayload> type() {
        return TYPE;
    }
}
