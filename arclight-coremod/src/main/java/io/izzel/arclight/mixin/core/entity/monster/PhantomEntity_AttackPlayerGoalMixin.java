package io.izzel.arclight.mixin.core.entity.monster;

import io.izzel.arclight.bridge.entity.MobEntityBridge;
import net.minecraft.entity.monster.PhantomEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.entity.monster.PhantomEntity.AttackPlayerGoal")
public abstract class PhantomEntity_AttackPlayerGoalMixin {

    @Shadow(aliases = {"this$0", "field_203141_a"}) private PhantomEntity outerThis;

    @Inject(method = "shouldExecute", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/PhantomEntity$AttackPlayerGoal;setAttackTarget(Lnet/minecraft/entity/LivingEntity;)V"))
    private void arclight$reason(CallbackInfoReturnable<Boolean> cir) {
        ((MobEntityBridge) outerThis).bridge$pushGoalTargetReason(EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
    }
}
