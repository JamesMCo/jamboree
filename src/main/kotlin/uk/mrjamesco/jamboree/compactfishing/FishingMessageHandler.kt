package uk.mrjamesco.jamboree.compactfishing

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

interface FishingMessageHandler {
    fun handleCaughtMessage(message: Component): Boolean
    fun handleIconMessage(message: Component): Boolean
    fun handleXPMessage(message: Component): Boolean
}

enum class FishingMessageHandlers : NameableEnum {
    DelayedOneLine {
        override fun get() = DelayedOneLineFishingMessageHandler
        override fun getDisplayName(): Component = Component.translatable("config.jamboree.compactfishing.mode.delayedoneline.name")
        override fun getDescription(): Component = Component.translatable("config.jamboree.compactfishing.mode.delayedoneline.description")
    },
    DelayedTwoLines {
        override fun get() = DelayedTwoLinesFishingMessageHandler
        override fun getDisplayName(): Component = Component.translatable("config.jamboree.compactfishing.mode.delayedtwolines.name")
        override fun getDescription(): Component = Component.translatable("config.jamboree.compactfishing.mode.delayedtwolines.description")
    },
    DelayedTwoLinesXpSeparate {
        override fun get() = DelayedTwoLinesXpSeparateFishingMessageHandler
        override fun getDisplayName(): Component = Component.translatable("config.jamboree.compactfishing.mode.delayedtwolinesxpseparate.name")
        override fun getDescription(): Component = Component.translatable("config.jamboree.compactfishing.mode.delayedtwolinesxpseparate.description")
    },
    Replacing {
        override fun get() = ReplacingFishingMessageHandler
        override fun getDisplayName(): Component = Component.translatable("config.jamboree.compactfishing.mode.replacing.name")
        override fun getDescription(): Component = Component.translatable("config.jamboree.compactfishing.mode.replacing.description").apply {
            append(Component.literal("\n"))
            append(Component.translatable("config.jamboree.compactfishing.mode.replacing.description.note.header").apply { style = Style.EMPTY.withColor(ChatFormatting.AQUA) })
            append(Component.literal("\n"))
            append(Component.translatable("config.jamboree.compactfishing.mode.replacing.description.note.body"))

            append(Component.literal("\n"))
            append(Component.translatable("config.jamboree.compactfishing.mode.replacing.description.warning.header").apply { style = Style.EMPTY.withColor(ChatFormatting.RED) })
            append(Component.literal("\n"))
            append(Component.translatable("config.jamboree.compactfishing.mode.replacing.description.warning.body"))
        }
    },
    ReplacingXpSeparate {
        override fun get() = ReplacingXpSeparateFishingMessageHandler
        override fun getDisplayName(): Component = Component.translatable("config.jamboree.compactfishing.mode.replacingxpseparate.name")
        override fun getDescription(): Component = Component.translatable("config.jamboree.compactfishing.mode.replacingxpseparate.description").apply {
            append(Component.literal("\n"))
            append(Component.translatable("config.jamboree.compactfishing.mode.replacing.description.note.header").apply { style = Style.EMPTY.withColor(ChatFormatting.AQUA) })
            append(Component.literal("\n"))
            append(Component.translatable("config.jamboree.compactfishing.mode.replacing.description.note.body"))

            append(Component.literal("\n"))
            append(Component.translatable("config.jamboree.compactfishing.mode.replacing.description.warning.header").apply { style = Style.EMPTY.withColor(ChatFormatting.RED) })
            append(Component.literal("\n"))
            append(Component.translatable("config.jamboree.compactfishing.mode.replacing.description.warning.body"))
        }
    };

    abstract fun get(): FishingMessageHandler
    abstract fun getDescription(): Component

    companion object {
        fun buildConfigDescription(): Component = Component.translatable("config.jamboree.compactfishing.mode.description").apply {
            append(Component.literal("\n"))
            entries.sortedBy { entry -> entry.displayName.string }.forEach { entry ->
                append(Component.literal("\n\n"))
                append(entry.displayName.copy().apply { style = Style.EMPTY.withBold(true) })
                append(Component.literal("\n"))
                append(entry.getDescription())
            }
        }
    }
}
