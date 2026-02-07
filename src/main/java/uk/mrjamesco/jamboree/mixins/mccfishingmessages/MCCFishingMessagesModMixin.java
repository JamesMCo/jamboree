package uk.mrjamesco.jamboree.mixins.mccfishingmessages;

import com.deflanko.MCCFishingMessages.MCCFishingMessagesMod;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.mrjamesco.jamboree.compactfishing.CompactFishing;

@Mixin(MCCFishingMessagesMod.class)
public class MCCFishingMessagesModMixin {
    @Inject(method = "isPulledPhrase", at = @At("HEAD"), cancellable = true)
    private static void alterIsPulledPhraseIfCompactFishingHandlingMessage(Component message, CallbackInfoReturnable<Boolean> cir) {
        Boolean result = CompactFishing.INSTANCE.maybeAlterMCCFishingMessages(message);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }
}
