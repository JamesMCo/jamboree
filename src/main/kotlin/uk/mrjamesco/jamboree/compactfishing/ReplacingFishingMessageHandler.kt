package uk.mrjamesco.jamboree.compactfishing

import net.minecraft.network.chat.Component
import uk.mrjamesco.jamboree.Config

/**
 * Sends initial caught messages immediately, and replaces
 * that message in place with new versions when icon messages
 * and XP messages are received.
 */
object ReplacingFishingMessageHandler : CommonReplacingFishingMessageHandler() {
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
            append(Component.literal(" +"))
            append(xpMessage!!)
        }
    } ?: Component.empty()
}
