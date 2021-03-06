package io.izzel.arclight.mixin.bukkit;

import net.minecraft.block.BlockState;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftBlockState.class)
public class CraftBlockStateMixin {

    @Shadow(remap = false) protected BlockState data;

    @Override
    public String toString() {
        return this.data.toString();
    }
}
