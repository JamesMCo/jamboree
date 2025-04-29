package uk.mrjamesco.jamboree.compactfishing

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import uk.mrjamesco.jamboree.Config
import uk.mrjamesco.jamboree.compactfishing.CompactFishing.altIconOrder

/**
 * Collects all messages related to a fishing catch,
 * and sends as one message when receiving an XP message.
 */
object DelayedOneLineFishingMessageHandler : FishingMessageHandler {
    private var caughtMessage: MutableComponent? = null
    private val iconBuffer: MutableList<Pair<MutableComponent, Int>> = mutableListOf()
    private var xpMessage: MutableComponent? = null
    private var sendingMessage: Boolean = false

    override fun handleCaughtMessage(message: Component): Boolean {
        if (sendingMessage) {
            // Currently sending a final message, so need to let the message through
            return true
        }

        caughtMessage = message.copy()

        // Seeing a new caught message means we are handling a new set of messages
        // Therefore, we need to clear the icons and xp message
        iconBuffer.clear()
        xpMessage = null

        // Collecting messages, so we need to block the message
        return false
    }

    override fun handleIconMessage(message: Component): Boolean {
        if (Config.CompactFishing.showIcons) {
            message
                .siblings.first() // e.g. "[] Triggered: [] Supply Preserve"
                .siblings.last()  // e.g. "Triggered: [] Supply Preserve"
                .siblings         // e.g. "[]", " ", "Supply Preserve"
                .let {
                    iconBuffer.addLast(Pair(
                        it.first().copy().apply { style = message.siblings.first().style },
                        altIconOrder(it.last().string)
                    ))
                }
        }

        // Collecting messages, so we need to block the message
        return false
    }

    override fun handleXPMessage(message: Component): Boolean {
        if (Config.CompactFishing.showXP) {
            message
                .siblings.last()  // "You earned: n Island XP"
                .siblings.last()  // "n Island XP"
                .let {
                    xpMessage = it.copy()
                }
        }

        sendingMessage = true
        Minecraft.getInstance().player?.displayClientMessage(buildCompactMessage(), false)
        sendingMessage = false

        return false
    }

    fun buildCompactMessage(): Component = caughtMessage?.copy()?.apply {
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
