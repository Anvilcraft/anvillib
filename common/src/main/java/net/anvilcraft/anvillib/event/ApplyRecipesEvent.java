package net.anvilcraft.anvillib.event;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeManager;

public record ApplyRecipesEvent(RecipeManager recipeManager, HolderLookup.Provider registryAccess) {}
