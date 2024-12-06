package io.github.lapis256.ae2_mega_things

import appeng.api.client.StorageCellModels
import appeng.api.storage.StorageCells
import appeng.core.definitions.ItemDefinition
import io.github.lapis256.ae2_mega_things.command.DISKCellItemArgument
import io.github.lapis256.ae2_mega_things.command.MEGAThingsCommand
import io.github.lapis256.ae2_mega_things.init.AE2MTCreativeTab
import io.github.lapis256.ae2_mega_things.init.AE2MTItems
import io.github.lapis256.ae2_mega_things.integration.appmek.AppMek
import io.github.lapis256.ae2_mega_things.item.AbstractDISKDrive
import io.github.lapis256.ae2_mega_things.storage.AE2MTDISKCellHandler
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS


@Mod(AE2MEGAThings.MOD_ID)
object AE2MEGAThings {
    const val MOD_ID = "ae2_mega_things"
    const val MOD_NAME = "AE2 MEGA Things"

    fun rl(path: String) = ResourceLocation(MOD_ID, path)

    @JvmField
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)

    init {
        DISKCellItemArgument.register()

        MOD_BUS.addListener(::commonSetup)
        MOD_BUS.addGenericListener(Item::class.java, ::registerItem)
        FORGE_BUS.addListener(EventPriority.HIGHEST, ::registerCommand)


        AppMek.initIntegration()
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        StorageCells.addCellHandler(AE2MTDISKCellHandler)

        event.enqueueWork {
            AE2MTItems.initUpgrades()

            AE2MTItems.MEGA_ITEM_DISKS.forEach { registerDriveModel(it, "mega_item") }
            AE2MTItems.FLUID_DISKS.forEach { registerDriveModel(it, "fluid") }
            AE2MTItems.MEGA_FLUID_DISKS.forEach { registerDriveModel(it, "mega_fluid") }
        }
    }

    fun registerItem(event: RegistryEvent.Register<Item>) {
        AE2MTItems.ITEMS.forEach {
            event.registry.register(it.asItem().apply { setRegistryName(it.id()) })
        }
    }

    fun registerCommand(event: RegisterCommandsEvent) {
        MEGAThingsCommand.init(event.dispatcher)
    }

    fun registerDriveModel(item: ItemDefinition<AbstractDISKDrive>, name: String) {
        StorageCellModels.registerModel(item, rl("block/drive/cells/$name"))
    }
}
