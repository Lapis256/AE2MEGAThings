package io.github.lapis256.ae2_mega_things.data.provider

import appeng.core.definitions.AEBlocks
import appeng.core.definitions.AEItems
import appeng.core.definitions.ItemDefinition
import gripe._90.megacells.definition.MEGAItems
import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.init.AE2MTItems
import io.github.lapis256.ae2_mega_things.integration.AbstractMod
import io.github.lapis256.ae2_mega_things.integration.appmek.AppMek
import io.github.lapis256.ae2_mega_things.integration.appmek.init.AMIntegrationItems
import io.github.lapis256.ae2_mega_things.item.AbstractDISKDrive
import me.ramidzkh.mekae2.AMItems
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.conditions.ModLoadedCondition
import java.util.concurrent.CompletableFuture


class AE2MTRecipeProvider(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>) : RecipeProvider(output, lookupProvider) {
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

    override fun buildRecipes(output: RecipeOutput) {
        housing(output, AE2MTItems.MEGA_ITEM_DISK_HOUSING, MEGAItems.MEGA_ITEM_CELL_HOUSING)
        housing(output, AE2MTItems.FLUID_DISK_HOUSING, AEItems.FLUID_CELL_HOUSING)
        housing(output, AE2MTItems.MEGA_FLUID_DISK_HOUSING, MEGAItems.MEGA_FLUID_CELL_HOUSING)
        housing(AppMek, output, AMIntegrationItems.CHEMICAL_DISK_HOUSING, AMItems.CHEMICAL_CELL_HOUSING)
        housing(AppMek, output, AMIntegrationItems.MEGA_CHEMICAL_DISK_HOUSING, MEGAItems.MEGA_CHEMICAL_CELL_HOUSING)

        drives(output, AE2MTItems.MEGA_ITEM_DISKS, megaComponents, AE2MTItems.MEGA_ITEM_DISK_HOUSING, MEGAItems.MEGA_ITEM_CELL_HOUSING)
        drives(output, AE2MTItems.FLUID_DISKS, components, AE2MTItems.FLUID_DISK_HOUSING, AEItems.FLUID_CELL_HOUSING)
        drives(output, AE2MTItems.MEGA_FLUID_DISKS, megaComponents, AE2MTItems.MEGA_FLUID_DISK_HOUSING, MEGAItems.MEGA_FLUID_CELL_HOUSING)
        drives(AppMek, output, AMIntegrationItems.CHEMICAL_DISKS, components, AMIntegrationItems.CHEMICAL_DISK_HOUSING, AMItems.CHEMICAL_CELL_HOUSING)
        drives(AppMek, output, AMIntegrationItems.MEGA_CHEMICAL_DISKS, megaComponents, AMIntegrationItems.MEGA_CHEMICAL_DISK_HOUSING, MEGAItems.MEGA_CHEMICAL_CELL_HOUSING)
    }

    private fun drives(output: RecipeOutput, drives: Collection<ItemDefinition<AbstractDISKDrive>>, components: Collection<ItemDefinition<*>>, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        for ((drive, component) in drives.zip(components)) {
            drive(output, drive, component, housing, baseHousing)
        }
    }

    private fun drives(requiredMod: AbstractMod, output: RecipeOutput, drives: Collection<ItemDefinition<AbstractDISKDrive>>, components: Collection<ItemDefinition<*>>, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        for ((drive, component) in drives.zip(components)) {
            drive(requiredMod, output, drive, component, housing, baseHousing)
        }
    }

    private fun drive(output: RecipeOutput, drive: ItemDefinition<*>, component: ItemDefinition<*>, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        val (shaped, shapeless) = drive(drive, component, housing, baseHousing)
        build(output, shaped, drive.id())
        build(output, shapeless, drive.id().withSuffix("_with_housing"))
    }

    private fun drive(requiredMod: AbstractMod, output: RecipeOutput, drive: ItemDefinition<*>, component: ItemDefinition<*>, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        val (shaped, shapeless) = drive(drive, component, housing, baseHousing)
        build(requiredMod, output, shaped, drive.id())
        build(requiredMod, output, shapeless, drive.id().withSuffix("_with_housing"))
    }

    private fun drive(drive: ItemDefinition<*>, component: ItemDefinition<*>, housing: ItemDefinition<*>, baseHousing: ItemLike): Pair<ShapedRecipeBuilder, ShapelessRecipeBuilder> {
        val shaped = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, drive)
            .pattern("qrq")
            .pattern("rcr")
            .pattern("nmn")
            .define('q', AEBlocks.QUARTZ_VIBRANT_GLASS)
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('c', component)
            .define('m', baseHousing)
            .define('n', Tags.Items.INGOTS_NETHERITE)
            .unlockedBy("has_" + component.id().path, has(component))
            .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))

        val shapeless = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, drive)
            .requires(housing)
            .requires(component)
            .unlockedBy("has_" + component.id().path, has(component))
            .unlockedBy("has_" + housing.id().path, has(housing))

        return shaped to shapeless
    }

    private fun housing(output: RecipeOutput, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        build(output, housing(housing, baseHousing), housing.id())
    }

    private fun housing(requiredMod: AbstractMod, output: RecipeOutput, housing: ItemDefinition<*>, baseHousing: ItemLike) {
        build(requiredMod, output, housing(housing, baseHousing), housing.id())
    }

    private fun housing(housing: ItemDefinition<*>, baseHousing: ItemLike): ShapedRecipeBuilder {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, housing)
            .pattern("qrq")
            .pattern("r r")
            .pattern("nmn")
            .define('q', AEBlocks.QUARTZ_VIBRANT_GLASS)
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('m', baseHousing)
            .define('n', Tags.Items.INGOTS_NETHERITE)
            .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
    }

    private fun build(output: RecipeOutput, builder: RecipeBuilder, id: ResourceLocation) {
        builder.save(output, AE2MEGAThings.rl(id.path))
    }

    private fun build(requiredMod: AbstractMod, output: RecipeOutput, builder: RecipeBuilder, id: ResourceLocation) {
        build(output.withConditions(ModLoadedCondition(requiredMod.modId)), builder, id)
    }
}
