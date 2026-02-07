package uk.mrjamesco.jamboree.mixins.mccfishingmessages;

import net.minecraft.client.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "com.deflanko.MCCFishingMessages.FishingChatBox$ChatMessage")
public interface FishingChatBoxChatMessageMixin {
    @Accessor("chathudline")
    GuiMessage getChatHudLine();

    @Accessor("chathudline")
    void setChatHudLine(GuiMessage message);
}
