package io.izzel.arclight.mixin.core.block;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlock;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractPressurePlateBlock.class)
public abstract class AbstractPressurePlateBlockMixin {

    // @formatter:off
    @Accessor("PRESSURE_AABB") public static AxisAlignedBB getPressureAABB() { return null; }
    @Shadow protected abstract int computeRedstoneStrength(World worldIn, BlockPos pos);
    // @formatter:on

    @Redirect(method = "updateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractPressurePlateBlock;computeRedstoneStrength(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)I"))
    public int arclight$blockRedstone(AbstractPressurePlateBlock abstractPressurePlateBlock, World worldIn, BlockPos pos, World world, BlockPos blockPos, BlockState state, int oldRedstoneStrength) {
        int newStrength = this.computeRedstoneStrength(worldIn, pos);
        boolean flag = oldRedstoneStrength > 0;
        boolean flag1 = newStrength > 0;

        if (flag != flag1) {
            BlockRedstoneEvent event = new BlockRedstoneEvent(CraftBlock.at(worldIn, blockPos), oldRedstoneStrength, newStrength);
            Bukkit.getPluginManager().callEvent(event);
            newStrength = event.getNewCurrent();
        }
        return newStrength;
    }
}
