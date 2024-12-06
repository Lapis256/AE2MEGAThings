package io.github.lapis256.ae2_mega_things.init

import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack


object AE2MTCreativeTab : CreativeModeTab(AE2MEGAThings.MOD_ID) {
    override fun makeIcon() =ItemStack(AE2MTItems.ITEM_DISK_1M)
}
