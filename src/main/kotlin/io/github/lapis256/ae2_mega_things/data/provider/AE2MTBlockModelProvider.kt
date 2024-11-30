package io.github.lapis256.ae2_mega_things.data.provider

import appeng.datagen.providers.models.AE2BlockStateProvider
import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.init.AE2MTItems
import io.github.lapis256.ae2_mega_things.util.isMEGA
import net.minecraft.core.Direction
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.ExistingFileHelper


/*
 * This class is based on the following LGPL-3.0 code:
 * https://github.com/62832/MEGACells/blob/4.2.0/src/data/java/gripe/_90/megacells/datagen/MEGAModelProvider.java
 *
 * For the full text of the LGPL-3.0 License, please refer to the above URL or the LICENSE file of the original project.
 */
class AE2MTBlockModelProvider(output: PackOutput, helper: ExistingFileHelper) : AE2BlockStateProvider(output, AE2MEGAThings.MOD_ID, helper) {
    override fun registerStatesAndModels() {
        AE2MTItems.DISK_DRIVES.forEach(::drive)
    }

    private fun drive(drive: AE2MTItems.DISKDriveDefinition) {
        val tier = drive.tier
        val keyType = drive.keyType
        drive(
            "${tier.namePrefix()}_${keyType}_cell",
            (if (tier.isMEGA) "mega_" else "") + keyType,
            (tier.index() - (if (tier.isMEGA) 6 else 0)) * 2
        )
    }

    private fun drive(cell: String, texture: String, offset: Int) {
        val texturePrefix = "block/drive/cells/"
        models().getBuilder(texturePrefix + cell)
            .ao(false)
            .texture("cell", texturePrefix + texture)
            .texture("particle", texturePrefix + texture)
            .element()
            .to(6f, 2f, 2f)
            .face(Direction.NORTH)
            .uvs(0f, offset.toFloat(), 6f, (offset + 2).toFloat())
            .end()
            .face(Direction.UP)
            .uvs(6f, offset.toFloat(), 0f, (offset + 2).toFloat())
            .end()
            .face(Direction.DOWN)
            .uvs(6f, offset.toFloat(), 0f, (offset + 2).toFloat())
            .end()
            .faces { _, builder ->
                builder.texture("#cell").cullface(Direction.NORTH).end()
            }
            .end()
    }
}
