package io.github.lapis256.ae2_mega_things.init

import appeng.core.definitions.ItemDefinition
import gripe._90.megacells.MEGACells
import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.Output
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredRegister


object AE2MTCreativeTab {
    val REGISTRY: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AE2MEGAThings.MOD_ID)

    private val items = mutableSetOf<ItemDefinition<*>>()

    init {
        REGISTRY.register("main") { _ ->
            CreativeModeTab.builder()
                .title(Component.literal("AE2 MEGA Things"))
                .icon { ItemStack(AE2MTItems.ITEM_DISK_1M) }
                .displayItems { _, output -> buildItems(output) }
                .withTabsBefore(MEGACells.makeId("tab"))
                .build()
        }
    }

    private fun buildItems(output: Output) {
        for (item in items) {
            output.accept(item)
        }
    }

    fun add(item: ItemDefinition<*>) {
        items.add(item)
    }
}
