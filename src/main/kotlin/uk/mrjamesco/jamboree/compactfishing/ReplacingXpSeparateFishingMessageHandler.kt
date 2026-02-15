package uk.mrjamesco.jamboree.compactfishing

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import uk.mrjamesco.jamboree.Config

/**
 * Sends initial caught messages immediately, and replaces
 * that message in place with new versions when icon messages
 * and XP messages are received.
 */
object ReplacingXpSeparateFishingMessageHandler : CommonReplacingFishingMessageHandler() {
    private lateinit var arrowPrefix: MutableComponent

    override fun handleXPMessage(message: Component): Boolean {
        // Ensure that we have a copy of the server-sent spacing and arrow icon to prefix our message
        if (!::arrowPrefix.isInitialized) {
            arrowPrefix = message.copy().apply{
                val arrow = message.siblings.first()

                // Keep only the initial spacing and the extracted arrow icon
                siblings.clear()
                append(arrow)
            }
        }

        return super.handleXPMessage(message)
    }

    override fun buildCompactMessage(): Component = caughtMessage?.copy()?.apply {
        if (iconBuffer.isNotEmpty()) {
            append(Component.literal(" "))

            if (Config.CompactFishing.useAltIconOrder) {
                iconBuffer.sortedBy { it.second }
            } else {
                iconBuffer
            }.forEach { append(it.first) }
        }

        if (xpMessage != null) {
            append(Component.literal("\n"))
            append(arrowPrefix)
            append(Component.literal(" +"))
            append(xpMessage!!)
        }
    } ?: Component.empty()
}
