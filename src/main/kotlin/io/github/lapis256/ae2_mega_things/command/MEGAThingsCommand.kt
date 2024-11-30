package io.github.lapis256.ae2_mega_things.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.tree.LiteralCommandNode
import io.github.lapis256.ae2_mega_things.util.getDisk
import io.github.lapis256.ae2_mega_things.util.getUuid
import io.github.projectet.ae2things.AE2Things
import io.github.projectet.ae2things.item.AETItems
import io.github.projectet.ae2things.storage.IDISKCellItem
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.UuidArgument
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import java.util.*


/*
 * This class is based on the following MIT-licensed code:
 * https://github.com/Technici4n/AE2Things-Forge/blob/1.2.1/src/main/java/io/github/projectet/ae2things/command/Command.java
 *
 * Copyright (c) 2021 ProjectET
 *
 * For the full text of the MIT License, please refer to the above URL or the LICENSE file of the original project.
 */
object MEGAThingsCommand {
    fun init(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.root.addChild(recoverArg)
    }

    private val recoverArg: LiteralCommandNode<CommandSourceStack> = Commands.literal(AE2Things.MOD_ID).then(
        Commands.literal("recover").requires { source -> source.hasPermission(2) }
            .then(
                Commands.argument("uuid", UuidArgument.uuid())
                    .executes { ctx -> spawnDrive(ctx, ctx.getUuid("uuid")) }
                    .then(
                        Commands.argument("disk", DISKCellItemArgument)
                            .executes { ctx -> spawnDrive(ctx, ctx.getUuid("uuid"), ctx.getDisk("disk")) }
                    )
            )
    ).build()

    @Throws(CommandSyntaxException::class)
    private fun spawnDrive(context: CommandContext<CommandSourceStack>, uuid: UUID) =
        spawnDrive(context, uuid, AETItems.DISK_DRIVE_256K.get() as IDISKCellItem)

    @Throws(CommandSyntaxException::class)
    private fun spawnDrive(context: CommandContext<CommandSourceStack>, uuid: UUID, disk: IDISKCellItem): Int {
        val player = context.source.playerOrException

        val storageManager = AE2Things.currentStorageManager()
            ?.takeIf { it.hasUUID(uuid) }
            ?: run {
                context.source.sendFailure(Component.translatable("command.ae2things.recover_fail", uuid.toString()))
                return 1
            }

        player.addItem(ItemStack(disk).also {
            it.set(AE2Things.DATA_DISK_ID, uuid)
            it.set(AE2Things.DATA_DISK_ITEM_COUNT, storageManager.getOrCreateDisk(uuid).itemCount)
        })

        context.source.sendSuccess({ Component.translatable("command.ae2things.recover_success", player.displayName, uuid.toString()) }, true)
        return 0
    }
}
