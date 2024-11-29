package io.github.lapis256.ae2_mega_things.item

import appeng.api.stacks.AEKeyType
import net.minecraft.world.level.ItemLike


class FluidDISKDrive(coreItem: ItemLike, housingItem: ItemLike, kilobytes: Int, idleDrain: Double) :
    AbstractDISKDrive(AEKeyType.fluids(), coreItem, housingItem, kilobytes, idleDrain)
