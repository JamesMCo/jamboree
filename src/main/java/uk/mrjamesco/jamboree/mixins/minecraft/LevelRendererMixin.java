package uk.mrjamesco.jamboree.mixins.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.mrjamesco.jamboree.Config;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderBlockOutline", at = @At("HEAD"), cancellable = true)
    public void cancelRenderBlockOutlineIfHidingBlockOutlines(CallbackInfo ci) {
        if (Config.AdjustRendering.INSTANCE.getHideBlockOutlines()) {
            MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
            if (gameMode != null && gameMode.getPlayerMode().isCreative()) {
                ci.cancel();
            }
        }
    }
}
