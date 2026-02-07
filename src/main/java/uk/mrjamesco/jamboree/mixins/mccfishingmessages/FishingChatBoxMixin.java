package uk.mrjamesco.jamboree.mixins.mccfishingmessages;

import com.deflanko.MCCFishingMessages.FishingChatBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Deque;

@Mixin(FishingChatBox.class)
public interface FishingChatBoxMixin {
    @Accessor
    Deque<FishingChatBoxChatMessageMixin> getMessages();
}
