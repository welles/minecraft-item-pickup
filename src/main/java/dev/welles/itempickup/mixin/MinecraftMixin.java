package dev.welles.itempickup.mixin;

import dev.welles.itempickup.ItemPickupClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    /** Outlines the targeted item like a glowing entity. */
    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void itempickup$outlineTarget(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ItemPickupClient.isOutlined(entity)) cir.setReturnValue(true);
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void itempickup$pickUpInsteadOfUse(CallbackInfo ci) {
        if (ItemPickupClient.blocksUse((Minecraft) (Object) this)) ci.cancel();
    }

    //? if >=26.1 {
    /*@Inject(method = "pick", at = @At("TAIL"))
    private void itempickup$updateTarget(float partialTick, CallbackInfo ci) {
        ItemPickupClient.updateTarget((Minecraft) (Object) this, partialTick);
    }
    *///?}
}
