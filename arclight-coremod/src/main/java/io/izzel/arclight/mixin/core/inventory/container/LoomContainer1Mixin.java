package io.izzel.arclight.mixin.core.inventory.container;

import net.minecraft.inventory.container.LoomContainer;
import org.bukkit.Location;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import io.izzel.arclight.bridge.inventory.container.LoomContainerBridge;
import io.izzel.arclight.bridge.util.IWorldPosCallableBridge;
import io.izzel.arclight.mixin.core.inventory.InventoryMixin;

@Mixin(targets = "net/minecraft/inventory/container/LoomContainer$1")
public abstract class LoomContainer1Mixin extends InventoryMixin {

    @Shadow(aliases = {"this$0", "field_213913_a"}, remap = false) private LoomContainer outerThis;

    @Override
    public Location bridge$getLocation() {
        return ((IWorldPosCallableBridge) ((LoomContainerBridge) outerThis).bridge$getWorldPos()).bridge$getLocation();
    }
}
