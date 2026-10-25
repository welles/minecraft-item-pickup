package dev.welles.itempickup.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Backport of 1.21.9: a key press reaches every key mapping bound to that key, not just one of them. Lets the pick up
 * key share right-click with the use key. Only applied on Fabric before 1.21.9, NeoForge already does this.
 */
@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Shadow @Final private static Map<String, KeyMapping> ALL;
    @Shadow @Final private static Map<InputConstants.Key, KeyMapping> MAP;
    @Shadow private InputConstants.Key key;
    @Shadow private int clickCount;

    @Shadow
    public abstract void setDown(boolean down);

    @Inject(method = "click", at = @At("TAIL"))
    private static void itempickup$clickAll(InputConstants.Key key, CallbackInfo ci) {
        for (KeyMapping mapping : ALL.values()) {
            if (itempickup$isShadowed(mapping, key)) ((KeyMappingMixin) (Object) mapping).clickCount++;
        }
    }

    @Inject(method = "set", at = @At("TAIL"))
    private static void itempickup$setAll(InputConstants.Key key, boolean down, CallbackInfo ci) {
        for (KeyMapping mapping : ALL.values()) {
            if (itempickup$isShadowed(mapping, key)) ((KeyMappingMixin) (Object) mapping).setDown(down);
        }
    }

    /** Whether the mapping is bound to the key, but vanilla only notified the other mapping registered for it. */
    private static boolean itempickup$isShadowed(KeyMapping mapping, InputConstants.Key key) {
        return ((KeyMappingMixin) (Object) mapping).key.equals(key) && MAP.get(key) != mapping;
    }
}
