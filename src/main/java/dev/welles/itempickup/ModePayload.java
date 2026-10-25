package dev.welles.itempickup;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Tells the server the client's pickup mode. Sent after joining and whenever the mode changes. */
public record ModePayload(PickupMode mode) implements CustomPacketPayload {
    public static final Type<ModePayload> TYPE = new Type<>(ItemPickup.id("mode"));
    public static final StreamCodec<FriendlyByteBuf, ModePayload> CODEC = StreamCodec.ofMember(ModePayload::write, ModePayload::new);

    private ModePayload(FriendlyByteBuf buf) {
        this(buf.readBoolean() ? PickupMode.MANUAL : PickupMode.VANILLA);
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeBoolean(mode == PickupMode.MANUAL);
    }

    @Override
    public Type<ModePayload> type() {
        return TYPE;
    }
}
