package io.izzel.arclight.mixin.core.village;

import io.izzel.arclight.bridge.world.server.ServerWorldBridge;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.server.ServerWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillageSiege.class)
public class VillageSiegeMixin {

    @Inject(method = "func_75530_c", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;addEntity(Lnet/minecraft/entity/Entity;)Z"))
    public void arclight$addEntityReason(ServerWorld world, CallbackInfo ci) {
        ((ServerWorldBridge) world).bridge$pushAddEntityReason(CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION);
    }
}
