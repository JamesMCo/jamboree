package uk.mrjamesco.jamboree.compactfishing

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import uk.mrjamesco.jamboree.Config
import uk.mrjamesco.jamboree.compactfishing.CompactFishing.altIconOrder
import uk.mrjamesco.jamboree.compactfishing.CompactFishing.isCaughtMessage
import uk.mrjamesco.jamboree.compactfishing.CompactFishing.isIconMessage
import uk.mrjamesco.jamboree.compactfishing.CompactFishing.isXPMessage

/**
 * Sends initial caught messages immediately, then collects icons and xp messages
 * and sends as a second message when receiving an XP message.
 */
object DelayedTwoLinesFishingMessageHandler : FishingMessageHandler {
    private val iconBuffer: MutableList<Pair<MutableComponent, Int>> = mutableListOf()
    private var xpMessage: MutableComponent? = null
    private lateinit var arrowPrefix: MutableComponent
    private var sendingMessage: Boolean = false

    override fun handleCaughtMessage(message: Component): Boolean {
        // Seeing a new caught message means we are handling a new set of messages
        // Therefore, we need to clear the icons and xp message
        iconBuffer.clear()
        xpMessage = null

        // Showing on two lines, so we need to let the initial message through
        return true
    }

    override fun handleIconMessage(message: Component): Boolean {
        if (sendingMessage) {
            // Currently sending a final message, so need to let the message through
            return true
        }

        if (Config.CompactFishing.showIcons) {
            message
                .siblings.first() // e.g. "[] Triggered: [] Supply Preserve"
                .siblings.last()  // e.g. "Triggered: [] Supply Preserve"
                .siblings         // e.g. "[]", " ", "Supply Preserve"
                .let {
                    iconBuffer.addLast(Pair(
                        it.first().copy().apply {
                            // Use existing style (e.g. font), but replace hover event with that of overall message
                            if (message.siblings.first().style.hoverEvent !== null) {
                                this.style = style.withHoverEvent(message.siblings.first().style.hoverEvent)
                            }
                        },
                        altIconOrder(it.last().string)
                    ))
                }
        }

        // Collecting messages, so we need to block the message
        return false
    }

    override fun handleXPMessage(message: Component): Boolean {
        if (sendingMessage) {
            // Currently sending a final message, so need to let the message through
            return true
        }

        // Ensure that we have a copy of the server-sent spacing and arrow icon to prefix our message
        if (!::arrowPrefix.isInitialized) {
            arrowPrefix = message.copy().apply{
                val arrow = message.siblings.first()

                // Keep only the initial spacing and the extracted arrow icon
                siblings.clear()
                append(arrow)
            }
        }

        if (Config.CompactFishing.showXP) {
            message
                .siblings.last()  // "You earned: n Island XP"
                .siblings.last()  // "n Island XP"
                .let {
                    xpMessage = it.copy()
                }
        }

        // Make sure there's something that needs to be shown before sending a message
        if (iconBuffer.isNotEmpty() || xpMessage != null) {
            sendingMessage = true
            Minecraft.getInstance().player?.displayClientMessage(buildIconXPMessage(), false)
            sendingMessage = false
        }

        return false
    }

    fun buildIconXPMessage(): Component = when (::arrowPrefix.isInitialized) {
        true -> arrowPrefix.copy().apply {
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
        }

        false -> Component.empty()
    }

    override fun maybeAlterMCCFishingMessages(message: Component): Boolean? =
        when {
            message.isCaughtMessage() -> true
            message.isIconMessage() -> false
            message.isXPMessage() -> false

            sendingMessage -> true

            else -> null
        }
}
