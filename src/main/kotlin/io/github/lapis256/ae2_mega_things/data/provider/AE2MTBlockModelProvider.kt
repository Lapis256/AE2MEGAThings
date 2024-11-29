package io.github.lapis256.ae2_mega_things.data.provider

import appeng.core.AppEng
import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.model.generators.BlockModelProvider
import net.minecraftforge.common.data.ExistingFileHelper


class AE2MTBlockModelProvider(output: PackOutput, helper: ExistingFileHelper) : BlockModelProvider(output, AE2MEGAThings.MOD_ID, helper) {
    private val parent: ResourceLocation = AppEng.makeId("block/drive/drive_cell")

    override fun registerModels() {
        driveTexture("mega_item")
        driveTexture("fluid")
        driveTexture("mega_fluid")
        driveTexture("chemical")
        driveTexture("mega_chemical")
    }

    private fun driveTexture(name: String) = withExistingParent("block/drive/cells/$name", parent).texture("cell", "block/drive/cells/$name")
}
