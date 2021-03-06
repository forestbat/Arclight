package io.izzel.arclight.mixin.core.item.crafting;

import io.izzel.arclight.bridge.item.crafting.IRecipeBridge;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShulkerBoxColoringRecipe;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import io.izzel.arclight.mod.util.ArclightSpecialRecipe;

@Mixin(ShulkerBoxColoringRecipe.class)
public abstract class ShulkerBoxColoringRecipeMixin extends SpecialRecipe implements IRecipeBridge {

    public ShulkerBoxColoringRecipeMixin(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public Recipe bridge$toBukkitRecipe() {
        return ArclightSpecialRecipe.shapeless(CraftItemStack.asCraftMirror(new ItemStack(Blocks.WHITE_SHULKER_BOX)), this,
            Ingredient.fromItems(Items.BONE_MEAL));
    }
}
