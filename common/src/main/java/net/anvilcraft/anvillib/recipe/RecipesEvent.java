package net.anvilcraft.anvillib.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.mojang.serialization.DynamicOps;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipesEvent {
    public Map<RecipeType<?>, Map<ResourceLocation, RecipeHolder<?>>> recipes;
    public Map<ResourceLocation, RecipeHolder<?>> recipesById;
    public HolderLookup.Provider registryAccess;

    public RecipesEvent(
        Map<RecipeType<?>, Map<ResourceLocation, RecipeHolder<?>>> recipes,
        Map<ResourceLocation, RecipeHolder<?>> recipesById,
        HolderLookup.Provider registryAccess
    ) {
        this.recipes = recipes;
        this.recipesById = recipesById;
        this.registryAccess = registryAccess;
    }

    public void registerRecipe(RecipeHolder<?> recipe) {
        if (!this.recipes.containsKey(recipe.value().getType()))
            this.recipes.put(recipe.value().getType(), new HashMap<>());

        this.recipes.get(recipe.value().getType()).put(recipe.id(), recipe);
        this.recipesById.put(recipe.id(), recipe);
    }

    public void registerRecipe(ResourceLocation id, Recipe<?> recipe) {
        this.registerRecipe(new RecipeHolder<>(id, recipe));
    }

    public Optional<RecipeHolder<?>> removeRecipeID(ResourceLocation id) {
        if (this.recipesById.containsKey(id)) {
            return Optional.of(
                this.recipes.get(this.recipesById.remove(id).value().getType()).remove(id)
            );
        }

        return Optional.empty();
    }

    public void removeRecipesMatching(Predicate<RecipeHolder<?>> p) {
        var iter = this.recipesById.entrySet().iterator();
        while (iter.hasNext()) {
            var entry = iter.next();
            if (p.test(entry.getValue())) {
                iter.remove();
                this.recipes.get(entry.getValue().value().getType()).remove(entry.getKey());
            }
        }
    }

    public void mapRecipes(IRecipeMapper mapper) {
        var iter = this.recipesById.entrySet().iterator();
        List<RecipeHolder<?>> toRegister = new ArrayList<>();
        List<ResourceLocation> toRemove = new ArrayList<>();
        while (iter.hasNext()) {
            var entry = iter.next();
            if (mapper.shouldMap(entry.getValue())) {
                var mapped = mapper.apply(entry.getValue());
                if (mapped != entry.getValue()) {
                    toRegister.add(mapped);
                    toRemove.add(entry.getKey());
                }
            }
        }
        toRemove.forEach(this::removeRecipeID);
        toRegister.forEach(this::registerRecipe);
    }

    public void mapRecipeID(ResourceLocation id, Function<RecipeHolder<?>, RecipeHolder<?>> func) {
        var recipe = this.recipesById.get(id);
        if (recipe != null) {
            var mapped = func.apply(recipe);
            if (recipe != mapped) {
                this.removeRecipeID(id);
                this.registerRecipe(mapped);
            }
        }
    }

    public <E> Recipe<?> createRecipe(E object, DynamicOps<E> ops) throws IllegalStateException {
        RecipeSerializer<?> serializer = ops
            .get(object, "type")
            .flatMap(ops::getStringValue)
            .map(ResourceLocation::tryParse)
            .map(BuiltInRegistries.RECIPE_SERIALIZER::get)
            .getOrThrow();
            
        if (serializer == null) {
            throw new IllegalStateException("ALEC");
        }

        return serializer.codec().codec().parse(ops, object).getOrThrow();
    }
}
