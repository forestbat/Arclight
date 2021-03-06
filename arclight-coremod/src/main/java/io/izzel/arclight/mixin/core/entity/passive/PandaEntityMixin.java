package io.izzel.arclight.mixin.core.entity.passive;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import org.bukkit.craftbukkit.v1_14_R1.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(PandaEntity.class)
public abstract class PandaEntityMixin extends AnimalEntityMixin {

    @Shadow @Final private static Predicate<ItemEntity> field_213607_bQ;

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        boolean cancel = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() && field_213607_bQ.test(itemEntity);
        if (CraftEventFactory.callEntityPickupItemEvent((PandaEntity) (Object) this, itemEntity, 0, cancel).isCancelled()) {
            ItemStack itemstack = itemEntity.getItem();
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
            this.inventoryHandsDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
            this.onItemPickup(itemEntity, itemstack.getCount());
            itemEntity.remove();
        }

    }
}
