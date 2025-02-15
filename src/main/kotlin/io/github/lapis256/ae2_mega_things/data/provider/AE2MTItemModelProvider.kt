package io.github.lapis256.ae2_mega_things.data.provider

import appeng.core.AppEng
import appeng.core.definitions.ItemDefinition
import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.init.AE2MTItems
import io.github.lapis256.ae2_mega_things.item.AbstractDISKDrive
import io.github.lapis256.ae2_mega_things.item.DISKHousing
import net.minecraft.data.PackOutput
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.data.ExistingFileHelper


class AE2MTItemModelProvider(output: PackOutput, helper: ExistingFileHelper) : ItemModelProvider(output, AE2MEGAThings.MOD_ID, helper) {
    private val parent = mcLoc("item/generated")

    override fun registerModels() {
        AE2MTItems.ITEMS.forEach {
            when (it.asItem()) {
                is AbstractDISKDrive -> driveTexture(it)
                is DISKHousing -> housingTexture(it)
            }
        }
    }

    private fun driveTexture(item: ItemDefinition<*>) =
        singleTexture(item.id().path, parent, "layer0", modLoc("item/${item.id().path}")).texture("layer1", AppEng.makeId("item/storage_cell_led"))

    private fun housingTexture(item: ItemDefinition<*>) = singleTexture(item.id().path, parent, "layer0", modLoc("item/${item.id().path}"))
}
