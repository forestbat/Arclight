package io.izzel.arclight.mixin.core.inventory.container;

import io.izzel.arclight.bridge.entity.player.PlayerEntityBridge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.LoomContainer;
import net.minecraft.util.IWorldPosCallable;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryLoom;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import io.izzel.arclight.bridge.inventory.container.LoomContainerBridge;

@Mixin(LoomContainer.class)
public class LoomContainerMixin extends ContainerMixin implements LoomContainerBridge {

    // @formatter:off
    @Shadow @Final private IInventory field_217040_j; // crafting
    @Shadow @Final private IInventory field_217041_k; // result
    @Shadow @Final private IWorldPosCallable worldPos;
    // @formatter:on

    private CraftInventoryView bukkitEntity;
    private Player player;

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/util/IWorldPosCallable;)V", at = @At("RETURN"))
    public void arclight$init(int p_i50074_1_, PlayerInventory playerInventory, IWorldPosCallable p_i50074_3_, CallbackInfo ci) {
        this.player = (Player) ((PlayerEntityBridge) playerInventory.player).bridge$getBukkitEntity();
    }

    @Inject(method = "canInteractWith", at = @At("HEAD"))
    public void arclight$unreachable(PlayerEntity playerIn, CallbackInfoReturnable<Boolean> cir) {
        if (!bridge$isCheckReachable()) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public InventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryLoom inventory = new CraftInventoryLoom(this.field_217040_j, this.field_217041_k);
        bukkitEntity = new CraftInventoryView(this.player, inventory, (Container) (Object) this);
        return bukkitEntity;
    }

    @Override
    public IWorldPosCallable bridge$getWorldPos() {
        return this.worldPos;
    }
}
