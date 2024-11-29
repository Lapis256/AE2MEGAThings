package io.github.lapis256.ae2_mega_things.mixin;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.lapis256.ae2_mega_things.util.Utils;
import io.github.projectet.ae2things.storage.DISKCellInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(value = DISKCellInventory.class, remap = false)
public class MixinDISKCellInventory {
    // Prevents inserting a non-empty cell into the DISK.
    @ModifyExpressionValue(method = "insert", at = @At(value = "INVOKE", target = "Lio/github/projectet/ae2things/storage/IDISKCellItem;isBlackListed(Lnet/minecraft/world/item/ItemStack;Lappeng/api/stacks/AEKey;)Z"))
    private boolean ae2_mega_things$insertPreventNonEmptyCell(boolean original, @Local(argsOnly = true) AEKey what) {
        if (what instanceof AEItemKey itemKey) {
            return original || Utils.isCellNestingPrevented(itemKey);
        }
        return original;
    }
}
