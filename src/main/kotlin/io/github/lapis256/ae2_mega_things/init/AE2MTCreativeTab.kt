package io.github.lapis256.ae2_mega_things.init

import gripe._90.megacells.definition.MEGACreativeTab
import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.Output
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.forge.registerObject


object AE2MTCreativeTab {
    val TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AE2MEGAThings.MOD_ID)

    private val items = mutableSetOf<Item>()

    private val MAIN by TABS.registerObject("main") {
        CreativeModeTab.builder()
            .title(Component.literal("AE2 MEGA Things"))
            .icon { ItemStack(AE2MTItems.ITEM_DISK_1M) }
            .displayItems { _, output -> buildItems(output) }
            .withTabsBefore(MEGACreativeTab.ID)
            .build()
    }

    private fun buildItems(output: Output) {
        for (item in items) {
            output.accept(item)
        }
    }

    fun add(item: Item) {
        items.add(item)
    }
}
