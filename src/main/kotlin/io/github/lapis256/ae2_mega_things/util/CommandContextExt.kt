package io.github.lapis256.ae2_mega_things.util

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import io.github.lapis256.ae2_mega_things.command.DISKCellItemArgument
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.commands.arguments.UuidArgument


@Throws(CommandSyntaxException::class)
fun CommandContext<CommandSourceStack>.getUuid(name: String) = UuidArgument.getUuid(this, name)

@Throws(CommandSyntaxException::class)
fun CommandContext<CommandSourceStack>.getDisk(name: String) = DISKCellItemArgument.getDisk(this, name)

@Throws(CommandSyntaxException::class)
fun CommandContext<CommandSourceStack>.getId(name: String) = ResourceLocationArgument.getId(this, name)
