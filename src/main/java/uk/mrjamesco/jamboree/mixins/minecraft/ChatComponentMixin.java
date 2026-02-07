package uk.mrjamesco.jamboree.mixins.minecraft;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatComponentMixin {
    @Accessor
    List<GuiMessage> getAllMessages();

    @Invoker("refreshTrimmedMessages")
    void refreshChat();
}
