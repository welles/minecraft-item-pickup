package dev.welles.itempickup.mixin;

import dev.welles.itempickup.ItemPickup;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    /** Players in manual mode do not pick up items by walking over them. */
    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void itempickup$noPickupOnTouch(Player player, CallbackInfo ci) {
        if (!ItemPickup.picksUpOnTouch(player)) ci.cancel();
    }
}
