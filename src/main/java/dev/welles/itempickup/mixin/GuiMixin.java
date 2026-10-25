package dev.welles.itempickup.mixin;

import dev.welles.itempickup.ItemPickupClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.2 {
/*import net.minecraft.client.gui.Hud;
*///?} else {
import net.minecraft.client.gui.Gui;
//?}
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}

/**
 * Shows the name of the item that the pick up key would pick up in small text below the crosshair. Only drawn when the
 * crosshair is, e.g. not with a hidden HUD or in third person.
 */
//? if >=26.2 {
/*@Mixin(Hud.class)
*///?} else {
@Mixin(Gui.class)
//?}
public abstract class GuiMixin {
    /** Pixels between the center of the crosshair and the top of the name, below the attack indicator. */
    private static final int LABEL_OFFSET = 14;
    private static final float LABEL_SCALE = 0.75F;
    private static final int LABEL_COLOR = 0xFFFFFFFF;

    @Shadow @Final private Minecraft minecraft;

    //? if >=26.1 {
    /*@Inject(method = "extractCrosshair", at = @At("TAIL"))
    private void itempickup$targetName(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Component label = ItemPickupClient.targetLabel();
        if (label == null) return;
        Font font = minecraft.font;
        graphics.pose().pushMatrix();
        graphics.pose().translate(graphics.guiWidth() / 2.0F, graphics.guiHeight() / 2.0F + LABEL_OFFSET);
        graphics.pose().scale(LABEL_SCALE, LABEL_SCALE);
        graphics.text(font, label, -font.width(label) / 2, 0, LABEL_COLOR, true);
        graphics.pose().popMatrix();
    }
    *///?} else {
    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    private void itempickup$targetName(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Component label = ItemPickupClient.targetLabel();
        if (label == null) return;
        Font font = minecraft.font;
        //? if >=1.21.6 {
        /*graphics.pose().pushMatrix();
        graphics.pose().translate(graphics.guiWidth() / 2.0F, graphics.guiHeight() / 2.0F + LABEL_OFFSET);
        graphics.pose().scale(LABEL_SCALE, LABEL_SCALE);
        graphics.drawString(font, label, -font.width(label) / 2, 0, LABEL_COLOR, true);
        graphics.pose().popMatrix();
        *///?} else {
        graphics.pose().pushPose();
        graphics.pose().translate(graphics.guiWidth() / 2.0F, graphics.guiHeight() / 2.0F + LABEL_OFFSET, 0.0F);
        graphics.pose().scale(LABEL_SCALE, LABEL_SCALE, 1.0F);
        graphics.drawString(font, label, -font.width(label) / 2, 0, LABEL_COLOR, true);
        graphics.pose().popPose();
        //?}
    }
    //?}
}
