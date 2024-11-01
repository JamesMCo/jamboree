package uk.mrjamesco.jamboree

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.sound.SoundEvents
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

object ChatDing {
    fun registerListeners() {
        logger.info("Registering ChatDing listeners")
        ClientReceiveMessageEvents.CHAT.register { message, _, _, _, _ -> testMessage(message.string, "CHAT") }
        ClientReceiveMessageEvents.GAME.register { message, _ -> testMessage(message.string, "GAME") }
    }

    fun testMessage(message: String, messageType: String) {
        if ("jammy" in message.lowercase()) {
            logger.info("Found \"jammy\" in $messageType message")
            MinecraftClient.getInstance().player?.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), 1.0f, 1.0f)
        }
    }
}
