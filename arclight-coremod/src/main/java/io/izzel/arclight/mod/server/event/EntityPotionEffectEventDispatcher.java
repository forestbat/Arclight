package io.izzel.arclight.mod.server.event;

import io.izzel.arclight.bridge.entity.LivingEntityBridge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.bukkit.craftbukkit.v1_14_R1.event.CraftEventFactory;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public class EntityPotionEffectEventDispatcher {

    // todo 再检查一遍
    @SubscribeEvent(receiveCanceled = true)
    public void onPotionRemove(PotionEvent.PotionRemoveEvent event) {
        EntityPotionEffectEvent.Cause cause = ((LivingEntityBridge) event.getEntityLiving()).bridge$getEffectCause().orElse(EntityPotionEffectEvent.Cause.UNKNOWN);
        EntityPotionEffectEvent.Action action = ((LivingEntityBridge) event.getEntityLiving()).bridge$getAndResetAction();
        EntityPotionEffectEvent bukkitEvent = CraftEventFactory.callEntityPotionEffectChangeEvent(event.getEntityLiving(), event.getPotionEffect(), null, cause, action);
        event.setCanceled(bukkitEvent.isCancelled());
    }
}
