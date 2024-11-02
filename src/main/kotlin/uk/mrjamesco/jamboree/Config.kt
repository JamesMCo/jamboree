package uk.mrjamesco.jamboree

import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.dsl.YetAnotherConfigLib
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.tickBox
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import uk.mrjamesco.jamboree.Jamboree.Companion.logger

class Config {
    @SerialEntry
    var chatDingEnabled: Boolean = true

    object ChatDing {
        val enabled: Boolean
            get() = handler.instance().chatDingEnabled
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
                        description(OptionDescription.of(Text.literal("When enabled, Chat Ding plays a sound when one of the trigger phrases is sent in chat.")))
                        binding(handler.instance()::chatDingEnabled, true)
                        controller(tickBox())
                    }
                }
            }
        }.generateScreen(parentScreen)
    }
}
