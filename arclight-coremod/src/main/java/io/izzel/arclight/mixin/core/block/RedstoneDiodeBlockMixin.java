package io.izzel.arclight.mixin.core.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_14_R1.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(RedstoneDiodeBlock.class)
public class RedstoneDiodeBlockMixin {

    @Inject(method = "tick", cancellable = true, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public void arclight$turnOff(BlockState state, World worldIn, BlockPos pos, Random random, CallbackInfo ci) {
        if (CraftEventFactory.callRedstoneChange(worldIn, pos, 15, 0).getNewCurrent() != 0) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", cancellable = true, at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public void arclight$turnOn(BlockState state, World worldIn, BlockPos pos, Random random, CallbackInfo ci) {
        if (CraftEventFactory.callRedstoneChange(worldIn, pos, 0, 15).getNewCurrent() != 15) {
            ci.cancel();
        }
    }
}
