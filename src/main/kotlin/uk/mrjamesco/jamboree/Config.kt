package uk.mrjamesco.jamboree

import dev.isxander.yacl3.api.ListOption
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.dsl.YetAnotherConfigLib
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.enumDropdown
import dev.isxander.yacl3.dsl.stringField
import dev.isxander.yacl3.dsl.tickBox
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.Screen
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

class Config {
    @SerialEntry
    var chatDingEnabled: Boolean = true

    @SerialEntry
    var chatDingSound: uk.mrjamesco.jamboree.ChatDing.NotificationSound = uk.mrjamesco.jamboree.ChatDing.NotificationSound.Chime

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

    object ChatDing {
        val enabled: Boolean
            get() = handler.instance().chatDingEnabled

        val sound: SoundEvent
            get() = handler.instance().chatDingSound.sound

        val filters: List<String>
            get() = handler.instance().chatDingFilters
    }

    companion object {
        val handler: ConfigClassHandler<Config> by lazy {
            ConfigClassHandler.createBuilder(Config::class.java)
                .id(Identifier.of("jamboree", "config"))
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
            title(Text.literal("Jamboree"))
            save(handler::save)

            categories.register("jamboree") {
                name(Text.literal("Jamboree"))

                groups.register("chatding") {
                    name(Text.literal("Chat Ding"))

                    options.register<Boolean>("enabled") {
                        name(Text.literal("Enabled"))
                        description(OptionDescription.of(Text.literal("When enabled, Chat Ding plays a sound when one of the filter phrases is sent in chat.")))
                        binding(handler.instance()::chatDingEnabled, true)
                        controller(tickBox())
                    }
                    options.register<uk.mrjamesco.jamboree.ChatDing.NotificationSound>("sound") {
                        name(Text.literal("Notification Sound"))
                        description(OptionDescription.of(Text.literal("The sound that plays when one of the filter phrases is sent in chat.")))
                        binding(handler.instance()::chatDingSound, uk.mrjamesco.jamboree.ChatDing.NotificationSound.Chime)
                        controller(enumDropdown<uk.mrjamesco.jamboree.ChatDing.NotificationSound>())
                    }
                }
                groups.register("chatdingfilters", ListOption.createBuilder<String>()
                    .name(Text.literal("Chat Ding Filters"))
                    .description(OptionDescription.of(Text.literal(
                        "The list of phrases that should cause a sound to play when sent in chat.\n\n" +
                        "If a phrase in the list is included within a longer phrase, only the shorter phrase will be kept."
                    )))
                    .binding(emptyList(), { handler.instance().chatDingFilters }, { value -> handler.instance().chatDingFilters = value })
                    .controller(stringField())
                    .initial("")
                    .build()
                )
            }
        }.generateScreen(parentScreen)
    }
}
