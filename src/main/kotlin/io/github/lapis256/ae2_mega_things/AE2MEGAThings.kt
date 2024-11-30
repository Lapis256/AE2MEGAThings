package io.github.lapis256.ae2_mega_things

import appeng.api.storage.StorageCells
import io.github.lapis256.ae2_mega_things.command.DISKCellItemArgument
import io.github.lapis256.ae2_mega_things.command.MEGAThingsCommand
import io.github.lapis256.ae2_mega_things.init.AE2MTCreativeTab
import io.github.lapis256.ae2_mega_things.init.AE2MTItems
import io.github.lapis256.ae2_mega_things.integration.appmek.AppMek
import io.github.lapis256.ae2_mega_things.storage.AE2MTDISKCellHandler
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Mod(AE2MEGAThings.MOD_ID)
class AE2MEGAThings(modEventBus: IEventBus) {
    init {
        DISKCellItemArgument.REGISTRY.register(modEventBus)
        AE2MTCreativeTab.REGISTRY.register(modEventBus)
        AE2MTItems.REGISTRY.register(modEventBus)

        modEventBus.addListener(::commonSetup)
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ::registerCommand)

        AppMek.initIntegration(modEventBus)
    }

    companion object {
        const val MOD_ID = "ae2_mega_things"
        const val MOD_NAME = "AE2 MEGA Things"

        @JvmField
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)

        fun rl(path: String) = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        StorageCells.addCellHandler(AE2MTDISKCellHandler)

        event.enqueueWork {
            AE2MTItems.initUpgrades()
        }
    }

    fun registerCommand(event: RegisterCommandsEvent) {
        MEGAThingsCommand.init(event.dispatcher)
    }
}
