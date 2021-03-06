package io.izzel.arclight.mixin.core.inventory;

import io.izzel.arclight.bridge.entity.player.PlayerEntityBridge;
import io.izzel.arclight.bridge.inventory.IInventoryBridge;
import io.izzel.arclight.bridge.inventory.container.WorkbenchContainerBridge;
import io.izzel.arclight.bridge.util.IWorldPosCallableBridge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import io.izzel.arclight.bridge.inventory.CraftingInventoryBridge;

import java.util.ArrayList;
import java.util.List;

@Mixin(CraftingInventory.class)
public abstract class CraftingInventoryMixin implements CraftingInventoryBridge, IInventory {

    // @formatter:off
    @Shadow @Final private NonNullList<ItemStack> stackList;
    @Shadow @Final public Container field_70465_c;
    // @formatter:on

    public List<HumanEntity> transaction = new ArrayList<>();
    private IRecipe<?> currentRecipe;
    public IInventory resultInventory;
    private PlayerEntity owner;
    private InventoryHolder bukkitOwner;
    private int maxStack = MAX_STACK;

    public void arclight$constructor(Container eventHandlerIn, int width, int height) {
        throw new RuntimeException();
    }

    public void arclight$constructor(Container eventHandlerIn, int width, int height, PlayerEntity owner) {
        arclight$constructor(eventHandlerIn, width, height);
        this.owner = owner;
    }

    public InventoryType getInvType() {
        return this.stackList.size() == 4 ? InventoryType.CRAFTING : InventoryType.WORKBENCH;
    }

    @Override
    public void bridge$setResultInventory(IInventory resultInventory) {
        this.resultInventory = resultInventory;
    }

    @Override
    public void bridge$setOwner(PlayerEntity owner) {
        this.owner = owner;
    }

    @Override
    public List<ItemStack> bridge$getContents() {
        return this.stackList;
    }

    @Override
    public void bridge$onOpen(CraftHumanEntity who) {
        this.transaction.add(who);
    }

    @Override
    public void bridge$onClose(CraftHumanEntity who) {
        this.transaction.remove(who);
    }

    @Override
    public List<HumanEntity> bridge$getViewers() {
        return transaction;
    }

    @Override
    public InventoryHolder bridge$getOwner() {
        if (bukkitOwner == null) {
            bukkitOwner = owner == null ? null : ((PlayerEntityBridge) owner).bridge$getBukkitEntity();
        }
        return bukkitOwner;
    }

    @Override
    public void bridge$setOwner(InventoryHolder owner) {
        this.bukkitOwner = owner;
    }

    @Override
    public int getInventoryStackLimit() {
        if (maxStack == 0) maxStack = MAX_STACK;
        return this.maxStack;
    }

    @Override
    public void bridge$setMaxStackSize(int size) {
        this.maxStack = size;
        ((IInventoryBridge) this.resultInventory).bridge$setMaxStackSize(size);
    }

    @Override
    public Location bridge$getLocation() {
        return this.field_70465_c instanceof WorkbenchContainer
            ? ((IWorldPosCallableBridge) ((WorkbenchContainerBridge) field_70465_c).bridge$getContainerAccess()).bridge$getLocation()
            : ((PlayerEntityBridge) owner).bridge$getBukkitEntity().getLocation();
    }

    @Override
    public IRecipe<?> bridge$getCurrentRecipe() {
        return this.currentRecipe;
    }

    @Override
    public void bridge$setCurrentRecipe(IRecipe<?> recipe) {
        this.currentRecipe = recipe;
    }
}
