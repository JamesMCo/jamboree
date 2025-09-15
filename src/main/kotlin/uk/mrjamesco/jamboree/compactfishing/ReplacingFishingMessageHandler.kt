package uk.mrjamesco.jamboree.compactfishing

import net.minecraft.client.GuiMessage
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import uk.mrjamesco.jamboree.Config
import uk.mrjamesco.jamboree.compactfishing.CompactFishing.altIconOrder
import uk.mrjamesco.jamboree.compactfishing.CompactFishing.isCaughtMessage
import uk.mrjamesco.jamboree.mixins.ChatComponentMixin

/**
 * Sends initial caught messages immediately, and replaces
 * that message in place with new versions when icon messages
 * and XP messages are received.
 */
object ReplacingFishingMessageHandler : FishingMessageHandler {
    private var caughtMessage: MutableComponent? = null
    private val iconBuffer: MutableList<Pair<MutableComponent, Int>> = mutableListOf()
    private var xpMessage: MutableComponent? = null

    override fun handleCaughtMessage(message: Component): Boolean {
        caughtMessage = message.copy()

        // Seeing a new caught message means we are handling a new set of messages
        // Therefore, we need to clear the icons and xp message
        iconBuffer.clear()
        xpMessage = null

        // Updating message as new info received immediately,
        // so we need to let the initial message through
        return true
    }

    override fun handleIconMessage(message: Component): Boolean {
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

        // Updating message as new info received immediately,
        // so we need to update chat and block the message
        updateChat()
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

        updateChat()
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

    fun updateChat() {
        // Update chat by replacing an existing message

        // Only need to toggle sendingCompactMessage when sending a new message
        // (replacing an existing message doesn't trigger ClientReceiveMessageEvents.ALLOW_GAME)
        (Minecraft.getInstance().gui.chat as ChatComponentMixin).apply replaceExistingCatchMessage@{
            allMessages.forEachIndexed { i, message ->
                if (message.content.isCaughtMessage()) {
                    // Compact messages start with caught messages, so will match the same regex
                    allMessages[i] = GuiMessage(
                        message.addedTime,
                        buildCompactMessage(),
                        message.signature,
                        message.tag
                    )
                    refreshChat()
                    return@replaceExistingCatchMessage
                }
            }
        }
    }
}
