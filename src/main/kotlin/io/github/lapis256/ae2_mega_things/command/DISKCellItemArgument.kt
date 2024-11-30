package io.github.lapis256.ae2_mega_things.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.lapis256.ae2_mega_things.init.AE2MTTexts
import io.github.lapis256.ae2_mega_things.util.Utils
import io.github.lapis256.ae2_mega_things.util.getId
import io.github.projectet.ae2things.storage.IDISKCellItem
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.registries.ForgeRegistries
import java.util.concurrent.CompletableFuture


object DISKCellItemArgument : ArgumentType<ResourceLocation> {
    private val ERROR_INVALID_VALUE = DynamicCommandExceptionType { AE2MTTexts.Chat.UnknownDISKDrive.text(it) }

    private val DISK_DRIVE_MAP: Map<ResourceLocation, IDISKCellItem> by lazy {
        ForgeRegistries.ITEMS.asSequence()
            .filter { it is IDISKCellItem }
            .map { it as IDISKCellItem }
            .mapNotNull { item -> Utils.getItemId(item)?.let { it to item } }
            .toMap()
    }

    private val DISK_DRIVE_NAMES by lazy { DISK_DRIVE_MAP.keys }

    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): ResourceLocation {
        return ResourceLocation.read(reader)
    }

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> =
        if (context.source is SharedSuggestionProvider) SharedSuggestionProvider.suggestResource(DISK_DRIVE_NAMES, builder) else Suggestions.empty()

    fun getDisk(context: CommandContext<CommandSourceStack>, name: String): IDISKCellItem {
        val id = context.getId(name)
        return DISK_DRIVE_MAP[id] ?: throw ERROR_INVALID_VALUE.create(id)
    }

    fun register() {
        ArgumentTypeInfos.registerByClass(this.javaClass, SingletonArgumentInfo.contextFree { this })
    }
}
