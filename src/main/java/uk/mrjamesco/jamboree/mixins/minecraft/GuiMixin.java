package uk.mrjamesco.jamboree.mixins.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.mrjamesco.jamboree.Config;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void cancelDisplayScoreboardSidebarIfHidingScoreboard(CallbackInfo ci) {
        if (Config.AdjustRendering.INSTANCE.getHideScoreboard()) {
            MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
            if (gameMode != null && gameMode.getPlayerMode().isCreative()) {
                ci.cancel();
            }
        }
    }
}
