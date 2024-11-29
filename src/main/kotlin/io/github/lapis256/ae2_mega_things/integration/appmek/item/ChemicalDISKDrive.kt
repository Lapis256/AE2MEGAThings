package io.github.lapis256.ae2_mega_things.integration.appmek.item

import appeng.api.stacks.AEKey
import io.github.lapis256.ae2_mega_things.item.AbstractDISKDrive
import me.ramidzkh.mekae2.ae2.MekanismKey
import me.ramidzkh.mekae2.ae2.MekanismKeyType
import mekanism.api.chemical.attribute.ChemicalAttributeValidator
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike


class ChemicalDISKDrive(component: ItemLike, housing: ItemLike, kilobytes: Int, idleDrain: Double) :
    AbstractDISKDrive(MekanismKeyType.TYPE, component, housing, kilobytes, idleDrain) {

    override fun isBlackListed(cellItem: ItemStack, requestedAddition: AEKey): Boolean {
        if (requestedAddition is MekanismKey) {
            return !ChemicalAttributeValidator.DEFAULT.process(requestedAddition.stack)
        }
        return true
    }
}
