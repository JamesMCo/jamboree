package uk.mrjamesco.jamboree

import dev.isxander.yacl3.api.ListOption
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.dsl.YetAnotherConfigLib
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.enumDropdown
import dev.isxander.yacl3.dsl.slider
import dev.isxander.yacl3.dsl.stringField
import dev.isxander.yacl3.dsl.tickBox
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screens.Screen
import net.minecraft.sounds.SoundEvent
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

class Config {
    @SerialEntry
    var chatDingEnabled: Boolean = true

    @SerialEntry
    var chatDingSound: uk.mrjamesco.jamboree.ChatDing.NotificationSound = uk.mrjamesco.jamboree.ChatDing.NotificationSound.Chime

    @SerialEntry
    var chatDingPitch: Float = 1.0f

    @SerialEntry
    var chatDingVolume: Int = 100

    @SerialEntry
    var chatDingFilters: List<String> = emptyList()
        set(value) {
            val lowercase: List<String> = value.map(String::lowercase)

            // A long string containing a short string will always be triggered by the short string,
            // so no need to also filter by the long string
            // e.g. The filters ABC and ABCDE, ABCDE is redundant when ABC already triggers

            val filtered: MutableSet<String> = mutableSetOf()
            lowercase.sortedBy { it.length }.forEach { candidate ->
                if (candidate.isEmpty()) return@forEach
                if (filtered.all { it !in candidate }) {
                    filtered.add(candidate)
                }
            }

            field = lowercase.filter { it in filtered }
        }

    @SerialEntry
    var compactFishingEnabled: Boolean = false

    @SerialEntry
    var compactFishingShowIcons: Boolean = true

    @SerialEntry
    var compactFishingUseAltIconOrder: Boolean = false

    @SerialEntry
    var compactFishingShowXP: Boolean = true

    @SerialEntry
    var compactFishingCollect: Boolean = true

    object ChatDing {
        val enabled: Boolean
            get() = handler.instance().chatDingEnabled

        val sound: SoundEvent
            get() = handler.instance().chatDingSound.sound

        val pitch: Float
            get() = handler.instance().chatDingPitch.coerceIn(0.0f..2.0f)

        val volume: Float
            get() = handler.instance().chatDingVolume.coerceIn(0..100) / 100.0f

        val filters: List<String>
            get() = handler.instance().chatDingFilters
    }

    object CompactFishing {
        val enabled: Boolean
            get() = handler.instance().compactFishingEnabled

        val showIcons: Boolean
            get() = handler.instance().compactFishingShowIcons

        val useAltIconOrder: Boolean
            get() = handler.instance().compactFishingUseAltIconOrder

        val showXP: Boolean
            get() = handler.instance().compactFishingShowXP

        val collect: Boolean
            get() = handler.instance().compactFishingCollect
    }

    companion object {
        val handler: ConfigClassHandler<Config> by lazy {
            ConfigClassHandler.createBuilder(Config::class.java)
                .id(ResourceLocation.fromNamespaceAndPath("jamboree", "config"))
                .serializer { config ->
                    GsonConfigSerializerBuilder.create(config)
                        .setPath(FabricLoader.getInstance().configDir.resolve("jamboree.json"))
                        .build()
                }
                .build()
        }

        fun init() {
            logger.info("Loading config")
            handler.load()
        }

        fun getScreen(parentScreen: Screen): Screen = YetAnotherConfigLib("jamboree") {
            title(Component.translatable("config.jamboree"))
            save(handler::save)

            categories.register("jamboree") {
                name(Component.translatable("config.jamboree"))

                groups.register("chatding") {
                    name(Component.translatable("config.jamboree.chatding"))

                    options.register<Boolean>("enabled") {
                        name(Component.translatable("config.jamboree.chatding.enabled.name"))
                        description(OptionDescription.of(Component.translatable("config.jamboree.chatding.enabled.description")))
                        binding(handler.instance()::chatDingEnabled, true)
                        controller(tickBox())
                    }
                    options.register<uk.mrjamesco.jamboree.ChatDing.NotificationSound>("sound") {
                        name(Component.translatable("config.jamboree.chatding.sound.name"))
                        description(OptionDescription.of(Component.translatable("config.jamboree.chatding.sound.description")))
                        binding(handler.instance()::chatDingSound, uk.mrjamesco.jamboree.ChatDing.NotificationSound.Chime)
                        controller(enumDropdown<uk.mrjamesco.jamboree.ChatDing.NotificationSound>())
                    }
                    options.register<Float>("pitch") {
                        name(Component.translatable("config.jamboree.chatding.pitch.name"))
                        description(OptionDescription.of(Component.translatable("config.jamboree.chatding.pitch.description")))
                        binding(handler.instance()::chatDingPitch, 1.0f)
                        controller(slider(0.0f..2.0f, 0.1f))
                    }
                    options.register<Int>("volume") {
                        name(Component.translatable("config.jamboree.chatding.volume.name"))
                        description(OptionDescription.of(Component.translatable("config.jamboree.chatding.volume.description")))
                        binding(handler.instance()::chatDingVolume, 100)
                        controller(slider(0..100, 1) { Component.literal("$it%") })
                    }
                }
                groups.register("chatdingfilters", ListOption.createBuilder<String>()
                    .name(Component.translatable("config.jamboree.chatding.filters.name"))
                    .description(OptionDescription.of(Component.translatable("config.jamboree.chatding.filters.description")))
                    .binding(emptyList(), { handler.instance().chatDingFilters }, { value -> handler.instance().chatDingFilters = value })
                    .controller(stringField())
                    .initial("")
                    .build()
                )

                groups.register("compactfishing") {
                    name(Component.translatable("config.jamboree.compactfishing"))

                    options.register<Boolean>("enabled") {
                        name(Component.translatable("config.jamboree.compactfishing.enabled.name"))
                        description(OptionDescription.of(Component.translatable("config.jamboree.compactfishing.enabled.description")))
                        binding(handler.instance()::compactFishingEnabled, false)
                        controller(tickBox())
                    }
                    options.register<Boolean>("showicons") {
                        name(Component.translatable("config.jamboree.compactfishing.showicons.name"))
                        description(OptionDescription.of(Component.translatable("config.jamboree.compactfishing.showicons.description")))
                        binding(handler.instance()::compactFishingShowIcons, true)
                        controller(tickBox())
                    }
                    options.register<Boolean>("useAltIconOrder") {
                        name(Component.translatable("config.jamboree.compactfishing.useAltIconOrder.name"))
                        description(OptionDescription.of(Component.translatable("config.jamboree.compactfishing.useAltIconOrder.description")))
                        binding(handler.instance()::compactFishingUseAltIconOrder, false)
                        controller(tickBox())
                    }
                    options.register<Boolean>("showxp") {
                        name(Component.translatable("config.jamboree.compactfishing.showxp.name"))
                        description(OptionDescription.of(Component.translatable("config.jamboree.compactfishing.showxp.description")))
                        binding(handler.instance()::compactFishingShowXP, true)
                        controller(tickBox())
                    }
                    options.register<Boolean>("collect") {
                        name(Component.translatable("config.jamboree.compactfishing.collect.name"))
                        description(OptionDescription.of(Component.translatable("config.jamboree.compactfishing.collect.description")))
                        binding(handler.instance()::compactFishingCollect, true)
                        controller(tickBox())
                    }
                }
            }
        }.generateScreen(parentScreen)
    }
}
