package io.github.lapis256.ae2_mega_things.client

import appeng.api.client.StorageCellModels
import appeng.items.storage.BasicStorageCell
import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.init.AE2MTItems
import io.github.lapis256.ae2_mega_things.integration.appmek.client.AppMekClient
import net.minecraft.util.FastColor
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent


@Mod(value = AE2MEGAThings.MOD_ID, dist = [Dist.CLIENT])
class AE2MEGAThingsClient(modEventBus: IEventBus) {
    init {
        modEventBus.addListener(::registerItemColors)
        modEventBus.addListener(::clientSetup)

        AppMekClient.initIntegration(modEventBus)
    }

    private fun getFastColor(stack: ItemStack, tintIndex: Int) = FastColor.ARGB32.opaque(BasicStorageCell.getColor(stack, tintIndex))

    private fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
        event.register(::getFastColor, *AE2MTItems.DISK_DRIVES.toTypedArray())
    }

    private fun clientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            for (drive in AE2MTItems.DISK_DRIVES) {
                StorageCellModels.registerModel(drive, AE2MEGAThings.rl("block/drive/cells/${drive.tier.namePrefix()}_${drive.keyType}_cell"))
            }
        }
    }
}
