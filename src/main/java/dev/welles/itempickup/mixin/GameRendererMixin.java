package dev.welles.itempickup.mixin;

import dev.welles.itempickup.ItemPickupClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Before 26.1 the block or entity under the crosshair is picked here, see MinecraftMixin for later versions. */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final Minecraft minecraft;

    @Inject(method = "pick(F)V", at = @At("TAIL"))
    private void itempickup$updateTarget(float partialTick, CallbackInfo ci) {
        ItemPickupClient.updateTarget(minecraft, partialTick);
    }
}
