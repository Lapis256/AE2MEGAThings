package io.github.lapis256.ae2_mega_things.client

import appeng.items.storage.BasicStorageCell
import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.init.AE2MTItems
import io.github.lapis256.ae2_mega_things.integration.appmek.client.AppMekClient
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS


@Mod.EventBusSubscriber(modid = AE2MEGAThings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object AE2MEGAThingsClient {
    init {
        MOD_BUS.addListener(AE2MEGAThingsClient::registerItemColors)

        AppMekClient.initIntegration()
    }

    private fun registerItemColors(event: ColorHandlerEvent.Item) {
        event.itemColors.register(BasicStorageCell::getColor, *AE2MTItems.DISK_DRIVES.toTypedArray())
    }
}
