package io.github.lapis256.ae2_mega_things.item

import appeng.api.stacks.AEItemKey
import appeng.api.stacks.AEKey
import appeng.api.stacks.AEKeyType
import io.github.lapis256.ae2_mega_things.util.Utils
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike


class ItemDISKDrive(coreItem: ItemLike, housingItem: ItemLike, kilobytes: Int, idleDrain: Double) :
    AbstractDISKDrive(AEKeyType.items(), coreItem, housingItem, kilobytes, idleDrain) {

    override fun isBlackListed(cellItem: ItemStack, requestedAddition: AEKey): Boolean {
        val itemKey = requestedAddition as? AEItemKey ?: return false
        return Utils.isCellNestingPrevented(itemKey)
    }
}
