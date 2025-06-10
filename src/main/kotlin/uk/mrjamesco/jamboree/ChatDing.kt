package uk.mrjamesco.jamboree

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import uk.mrjamesco.jamboree.Jamboree.Companion.logger
import uk.mrjamesco.jamboree.Util.requestAttentionIfNotActive

object ChatDing {
    enum class NotificationSound {
        Banjo {
            override val sound = SoundEvents.NOTE_BLOCK_BANJO.value()
        },
        BassDrum {
            override val sound = SoundEvents.NOTE_BLOCK_BASEDRUM.value()
        },
        Bass {
            override val sound = SoundEvents.NOTE_BLOCK_BASS.value()
        },
        Bell {
            override val sound = SoundEvents.NOTE_BLOCK_BELL.value()
        },
        Bit {
            override val sound = SoundEvents.NOTE_BLOCK_BIT.value()
        },
        Chime {
            override val sound = SoundEvents.NOTE_BLOCK_CHIME.value()
        },
        CowBell {
            override val sound = SoundEvents.NOTE_BLOCK_COW_BELL.value()
        },
        Didgeridoo {
            override val sound = SoundEvents.NOTE_BLOCK_DIDGERIDOO.value()
        },
        Flute {
            override val sound = SoundEvents.NOTE_BLOCK_FLUTE.value()
        },
        Guitar {
            override val sound = SoundEvents.NOTE_BLOCK_GUITAR.value()
        },
        Harp {
            override val sound = SoundEvents.NOTE_BLOCK_HARP.value()
        },
        Hat {
            override val sound = SoundEvents.NOTE_BLOCK_HAT.value()
        },
        Pling {
            override val sound = SoundEvents.NOTE_BLOCK_PLING.value()
        },
        Snare {
            override val sound = SoundEvents.NOTE_BLOCK_SNARE.value()
        },
        Vibraphone {
            override val sound = SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value()
        },
        Xylophone {
            override val sound = SoundEvents.NOTE_BLOCK_XYLOPHONE.value()
        };

        abstract val sound: SoundEvent
    }

    private var latestTrigger: Instant = Instant.DISTANT_PAST

    fun registerListeners() {
        logger.info("Registering ChatDing listeners")
        ClientReceiveMessageEvents.CHAT.register { message, _, _, _, _ -> if (Config.ChatDing.enabled) testMessage(message.string) }
        ClientReceiveMessageEvents.GAME.register { message, _ -> if (Config.ChatDing.enabled) testMessage(message.string) }
    }

    fun testMessage(message: String) {
        if (Clock.System.now() - latestTrigger < Config.ChatDing.cooldown) {
            return
        }

        message.lowercase().let { lowercase ->
            for (candidate: String in Config.ChatDing.filters) {
                if (candidate in lowercase) {
                    Minecraft.getInstance().player?.playSound(Config.ChatDing.sound, Config.ChatDing.volume, Config.ChatDing.pitch)
                    latestTrigger = Clock.System.now()
                    if (Config.ChatDing.flash) {
                        Minecraft.getInstance().window.requestAttentionIfNotActive()
                    }
                    return
                }
            }
        }
    }
}
