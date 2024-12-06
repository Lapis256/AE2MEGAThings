package io.github.lapis256.ae2_mega_things.data.provider

import appeng.core.definitions.AEBlocks
import appeng.core.definitions.AEItems
import appeng.core.definitions.ItemDefinition
import gripe._90.megacells.integration.appmek.AppMekItems
import gripe._90.megacells.item.MEGAItems
import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.init.AE2MTItems
import io.github.lapis256.ae2_mega_things.integration.AbstractMod
import io.github.lapis256.ae2_mega_things.integration.appmek.AppMek
import io.github.lapis256.ae2_mega_things.integration.appmek.init.AMIntegrationItems
import io.github.lapis256.ae2_mega_things.item.AbstractDISKDrive
import io.github.lapis256.ae2_mega_things.util.withSuffix
import me.ramidzkh.mekae2.AMItems
import net.minecraft.data.DataGenerator
import net.minecraft.data.recipes.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ItemLike
import net.minecraftforge.common.Tags
import net.minecraftforge.common.crafting.ConditionalRecipe
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition
import java.util.function.Consumer


class AE2MTRecipeProvider(gen: DataGenerator) : RecipeProvider(gen) {
    private val components = listOf(
        AEItems.CELL_COMPONENT_1K,
        AEItems.CELL_COMPONENT_4K,
        AEItems.CELL_COMPONENT_16K,
        AEItems.CELL_COMPONENT_64K,
        AEItems.CELL_COMPONENT_256K
    )

    private val megaComponents = listOf(
        MEGAItems.CELL_COMPONENT_1M,
        MEGAItems.CELL_COMPONENT_4M,
        MEGAItems.CELL_COMPONENT_16M,
        MEGAItems.CELL_COMPONENT_64M,
        MEGAItems.CELL_COMPONENT_256M
    )

    override fun buildCraftingRecipes(writer: Consumer<FinishedRecipe>) {
        housing(writer, AE2MTItems.MEGA_ITEM_DISK_HOUSING, MEGAItems.MEGA_ITEM_CELL_HOUSING)
        housing(writer, AE2MTItems.FLUID_DISK_HOUSING, AEItems.FLUID_CELL_HOUSING)
        housing(writer, AE2MTItems.MEGA_FLUID_DISK_HOUSING, MEGAItems.MEGA_FLUID_CELL_HOUSING)
        housing(AppMek, writer, AMIntegrationItems.CHEMICAL_DISK_HOUSING, AMItems.CHEMICAL_CELL_HOUSING.get())
        housing(AppMek, writer, AMIntegrationItems.MEGA_CHEMICAL_DISK_HOUSING, AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)

        drives(writer, AE2MTItems.MEGA_ITEM_DISKS, megaComponents, AE2MTItems.MEGA_ITEM_DISK_HOUSING, MEGAItems.MEGA_ITEM_CELL_HOUSING)
        drives(writer, AE2MTItems.FLUID_DISKS, components, AE2MTItems.FLUID_DISK_HOUSING, AEItems.FLUID_CELL_HOUSING)
        drives(writer, AE2MTItems.MEGA_FLUID_DISKS, megaComponents, AE2MTItems.MEGA_FLUID_DISK_HOUSING, MEGAItems.MEGA_FLUID_CELL_HOUSING)
        drives(AppMek, writer, AMIntegrationItems.CHEMICAL_DISKS, components, AMIntegrationItems.CHEMICAL_DISK_HOUSING, AMItems.CHEMICAL_CELL_HOUSING.get())
        drives(AppMek, writer, AMIntegrationItems.MEGA_CHEMICAL_DISKS, megaComponents, AMIntegrationItems.MEGA_CHEMICAL_DISK_HOUSING, AppMekItems.MEGA_CHEMICAL_CELL_HOUSING)
    }

    private fun drives(writer: Consumer<FinishedRecipe>, drives: Collection<ItemDefinition<AbstractDISKDrive>>, components: Collection<ItemLike>, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        for ((drive, component) in drives.zip(components)) {
            drive(writer, drive, component, housing, baseHousing)
        }
    }

    private fun drives(requiredMod: AbstractMod, writer: Consumer<FinishedRecipe>, drives: Collection<ItemDefinition<AbstractDISKDrive>>, components: Collection<ItemLike>, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        for ((drive, component) in drives.zip(components)) {
            drive(requiredMod, writer, drive, component, housing, baseHousing)
        }
    }

    private fun drive(writer: Consumer<FinishedRecipe>, drive: ItemDefinition<*>, component: ItemLike, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        val (shaped, shapeless) = drive(drive, component, housing, baseHousing)
        build(writer, shaped, drive.id())
        build(writer, shapeless, drive.id().withSuffix("_with_housing"))
    }

    private fun drive(requiredMod: AbstractMod, writer: Consumer<FinishedRecipe>, drive: ItemDefinition<*>, component: ItemLike, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        val (shaped, shapeless) = drive(drive, component, housing, baseHousing)
        build(requiredMod, writer, shaped, drive.id())
        build(requiredMod, writer, shapeless, drive.id().withSuffix("_with_housing"))
    }

    private fun drive(drive: ItemDefinition<*>, component: ItemLike, housing: ItemDefinition<*>, baseHousing: ItemLike): Pair<ShapedRecipeBuilder, ShapelessRecipeBuilder> {
        val shaped = ShapedRecipeBuilder.shaped(drive)
            .pattern("qrq")
            .pattern("rcr")
            .pattern("nmn")
            .define('q', AEBlocks.QUARTZ_VIBRANT_GLASS)
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('c', component)
            .define('m', baseHousing)
            .define('n', Tags.Items.INGOTS_NETHERITE)
            .unlockedBy("has_" + component.asItem().registryName?.path, has(component))
            .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))

        val shapeless = ShapelessRecipeBuilder.shapeless(drive)
            .requires(housing)
            .requires(component)
            .unlockedBy("has_" + component.asItem().registryName?.path, has(component))
            .unlockedBy("has_" + housing.id().path, has(housing))

        return shaped to shapeless
    }

    private fun housing(writer: Consumer<FinishedRecipe>, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        build(writer, housing(housing, baseHousing), housing.id())
    }

    private fun housing(requiredMod: AbstractMod, writer: Consumer<FinishedRecipe>, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        build(requiredMod, writer, housing(housing, baseHousing), housing.id())
    }

    private fun housing(housing: ItemDefinition<*>, baseHousing: ItemLike): ShapedRecipeBuilder {
        return ShapedRecipeBuilder.shaped(housing)
            .pattern("qrq")
            .pattern("r r")
            .pattern("nmn")
            .define('q', AEBlocks.QUARTZ_VIBRANT_GLASS)
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('m', baseHousing)
            .define('n', Tags.Items.INGOTS_NETHERITE)
            .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
    }

    private fun build(writer: Consumer<FinishedRecipe>, builder: RecipeBuilder, id: ResourceLocation) {
        builder.save(writer, AE2MEGAThings.rl(id.path))
    }

    private fun build(requiredMod: AbstractMod, writer: Consumer<FinishedRecipe>, builder: RecipeBuilder, id: ResourceLocation) {
        ConditionalRecipe.builder()
            .addCondition(ModLoadedCondition(requiredMod.modId))
            .addRecipe(builder::save)
            .build(writer, AE2MEGAThings.rl(id.path))
    }
}
