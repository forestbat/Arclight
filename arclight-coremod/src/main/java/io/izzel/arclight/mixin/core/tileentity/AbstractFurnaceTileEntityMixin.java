package io.izzel.arclight.mixin.core.tileentity;

import io.izzel.arclight.bridge.entity.player.ServerPlayerEntityBridge;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftMagicNumbers;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.izzel.arclight.bridge.tileentity.AbstractFurnaceTileEntityBridge;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractFurnaceTileEntity.class)
public abstract class AbstractFurnaceTileEntityMixin extends LockableTileEntityMixin implements AbstractFurnaceTileEntityBridge {

    // @formatter:off
    @Shadow protected NonNullList<ItemStack> items;
    @Shadow protected abstract int getBurnTime(ItemStack stack);
    @Shadow public int burnTime;
    @Shadow protected abstract boolean isBurning();
    @Shadow protected abstract boolean canSmelt(@Nullable IRecipe<?> recipeIn);
    @Shadow public abstract void setRecipeUsed(@Nullable IRecipe<?> recipe);
    @Shadow public abstract void func_213995_d(PlayerEntity p_213995_1_);
    // @formatter:on

    public List<HumanEntity> transaction = new ArrayList<>();
    private int maxStack = MAX_STACK;

    private transient FurnaceBurnEvent arclight$burnEvent;

    @Inject(method = "tick", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;getBurnTime(Lnet/minecraft/item/ItemStack;)I"))
    public void arclight$furnaceBurn(CallbackInfo ci) {
        ItemStack itemStack = this.items.get(1);
        CraftItemStack fuel = CraftItemStack.asCraftMirror(itemStack);

        arclight$burnEvent = new FurnaceBurnEvent(CraftBlock.at(this.world, this.pos), fuel, getBurnTime(itemStack));
        Bukkit.getPluginManager().callEvent(arclight$burnEvent);

        if (arclight$burnEvent.isCancelled()) {
            ci.cancel();
            arclight$burnEvent = null;
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/tileentity/AbstractFurnaceTileEntity;isBurning()Z"))
    public boolean arclight$setBurnTime(AbstractFurnaceTileEntity furnace) {
        this.burnTime = arclight$burnEvent.getBurnTime();
        try {
            return this.isBurning() && arclight$burnEvent.isBurning();
        } finally {
            arclight$burnEvent = null;
        }
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    private void func_214007_c(@Nullable IRecipe<?> p_214007_1_) {
        if (p_214007_1_ != null && this.canSmelt(p_214007_1_)) {
            ItemStack itemstack = this.items.get(0);
            ItemStack itemstack1 = p_214007_1_.getRecipeOutput();
            ItemStack itemstack2 = this.items.get(2);

            CraftItemStack source = CraftItemStack.asCraftMirror(itemstack);
            org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(itemstack1);

            FurnaceSmeltEvent event = new FurnaceSmeltEvent(CraftBlock.at(world, pos), source, result);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            result = event.getResult();
            itemstack1 = CraftItemStack.asNMSCopy(result);

            if (!itemstack1.isEmpty()) {
                if (itemstack2.isEmpty()) {
                    this.items.set(2, itemstack1.copy());
                } else if (CraftItemStack.asCraftMirror(itemstack2).isSimilar(result)) {
                    itemstack2.grow(itemstack1.getCount());
                } else {
                    return;
                }
            }

            if (!this.world.isRemote) {
                this.setRecipeUsed(p_214007_1_);
            }

            if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET) {
                this.items.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
        }
    }

    private static AbstractFurnaceTileEntity arclight$captureFurnace;
    private static ItemStack arclight$item;
    private static int arclight$captureAmount;

    public void d(PlayerEntity entity, ItemStack itemStack, int amount) {
        arclight$item = itemStack;
        arclight$captureAmount = amount;
        arclight$captureFurnace = (AbstractFurnaceTileEntity) (Object) this;
        this.func_213995_d(entity);
        arclight$item = null;
        arclight$captureAmount = 0;
        arclight$captureFurnace = null;
    }

    @Override
    public void bridge$dropExp(PlayerEntity entity, ItemStack itemStack, int amount) {
        d(entity, itemStack, amount);
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    private static void func_214003_a(PlayerEntity entity, int ex, float f) {
        if (f == 0.0F) {
            ex = 0;
        } else if (f < 1.0F) {
            int i = MathHelper.floor((float) ex * f);
            if (i < MathHelper.ceil((float) ex * f) && Math.random() < (double) ((float) ex * f - (float) i)) {
                ++i;
            }

            ex = i;
        }

        if (arclight$captureFurnace != null && arclight$captureAmount != 0) {
            FurnaceExtractEvent event = new FurnaceExtractEvent(((ServerPlayerEntityBridge) entity).bridge$getBukkitEntity(), CraftBlock.at(arclight$captureFurnace.getWorld(), arclight$captureFurnace.getPos()), CraftMagicNumbers.getMaterial(arclight$item.getItem()), arclight$captureAmount, ex);
            Bukkit.getPluginManager().callEvent(event);
            ex = event.getExpToDrop();
        }
        while (ex > 0) {
            int j = ExperienceOrbEntity.getXPSplit(ex);
            ex -= j;
            entity.world.addEntity(new ExperienceOrbEntity(entity.world, entity.posX, entity.posY + 0.5D, entity.posZ + 0.5D, j));
        }

    }

    @Override
    public List<ItemStack> bridge$getContents() {
        return this.items;
    }

    @Override
    public void bridge$onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    @Override
    public void bridge$onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    @Override
    public List<HumanEntity> bridge$getViewers() {
        return transaction;
    }

    @Override
    public void bridge$setOwner(InventoryHolder owner) {
    }

    @Override
    public int getInventoryStackLimit() {
        if (maxStack == 0) maxStack = MAX_STACK;
        return maxStack;
    }

    @Override
    public void bridge$setMaxStackSize(int size) {
        this.maxStack = size;
    }
}
