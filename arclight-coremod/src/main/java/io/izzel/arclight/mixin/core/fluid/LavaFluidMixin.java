package io.izzel.arclight.mixin.core.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.IFluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.bukkit.craftbukkit.v1_14_R1.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(LavaFluid.class)
public abstract class LavaFluidMixin {

    // @formatter:off
    @Shadow protected abstract boolean isSurroundingBlockFlammable(IWorldReader worldIn, BlockPos pos);
    @Shadow protected abstract boolean getCanBlockBurn(IWorldReader worldIn, BlockPos pos);
    // @formatter:on

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public void randomTick(World world, BlockPos pos, IFluidState state, Random random) {
        if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            int i = random.nextInt(3);
            if (i > 0) {
                BlockPos blockpos = pos;

                for (int j = 0; j < i; ++j) {
                    blockpos = blockpos.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                    if (!world.isBlockPresent(blockpos)) {
                        return;
                    }

                    BlockState blockstate = world.getBlockState(blockpos);
                    if (blockstate.isAir()) {
                        if (this.isSurroundingBlockFlammable(world, blockpos)) {
                            if (world.getBlockState(blockpos).getBlock() != Blocks.FIRE) {
                                if (CraftEventFactory.callBlockIgniteEvent(world, blockpos, pos).isCancelled()) {
                                    continue;
                                }
                            }
                            world.setBlockState(blockpos, ForgeEventFactory.fireFluidPlaceBlockEvent(world, blockpos, pos, Blocks.FIRE.getDefaultState()));
                            return;
                        }
                    } else if (blockstate.getMaterial().blocksMovement()) {
                        return;
                    }
                }
            } else {
                for (int k = 0; k < 3; ++k) {
                    BlockPos blockpos1 = pos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                    if (!world.isBlockPresent(blockpos1)) {
                        return;
                    }

                    if (world.isAirBlock(blockpos1.up()) && this.getCanBlockBurn(world, blockpos1)) {
                        BlockPos up = blockpos1.up();
                        if (world.getBlockState(up).getBlock() != Blocks.FIRE) {
                            if (CraftEventFactory.callBlockIgniteEvent(world, up, pos).isCancelled()) {
                                continue;
                            }
                        }
                        world.setBlockState(blockpos1.up(), ForgeEventFactory.fireFluidPlaceBlockEvent(world, blockpos1.up(), pos, Blocks.FIRE.getDefaultState()));
                    }
                }
            }

        }
    }
}
