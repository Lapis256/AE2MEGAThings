package io.github.lapis256.ae2_mega_things.util

import net.minecraft.resources.ResourceLocation


fun ResourceLocation.withSuffix(suffix: String) = ResourceLocation(namespace, path + suffix)
