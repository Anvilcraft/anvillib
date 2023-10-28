package net.anvilcraft.anvillib.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class RecipesEvent extends Event implements IModBusEvent {
    public Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes;
    public Map<Identifier, Recipe<?>> recipesById;

    public RecipesEvent(
        Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes,
        Map<Identifier, Recipe<?>> recipesById
    ) {
        this.recipes = recipes;
        this.recipesById = recipesById;
    }

    public void registerRecipe(Recipe<?> recipe) {
        if (!this.recipes.containsKey(recipe.getType()))
            this.recipes.put(recipe.getType(), new HashMap<>());

        this.recipes.get(recipe.getType()).put(recipe.getId(), recipe);
        this.recipesById.put(recipe.getId(), recipe);
    }

    public Optional<Recipe<?>> removeRecipeID(Identifier id) {
        if (this.recipesById.containsKey(id)) {
            return Optional.of(
                this.recipes.get(this.recipesById.remove(id).getType()).remove(id)
            );
        }

        return Optional.empty();
    }

    public void removeRecipesMatching(Predicate<Recipe<?>> p) {
        var iter = this.recipesById.entrySet().iterator();
        while (iter.hasNext()) {
            var entry = iter.next();
            if (p.test(entry.getValue())) {
                iter.remove();
                this.recipes.get(entry.getValue().getType()).remove(entry.getKey());
            }
        }
    }

    public void mapRecipes(IRecipeMapper mapper) {
        var iter = this.recipesById.entrySet().iterator();
        while (iter.hasNext()) {
            var entry = iter.next();
            if (mapper.shouldMap(entry.getValue())) {
                var mapped = mapper.apply(entry.getValue());
                if (mapped != entry.getValue()) {
                    iter.remove();
                    this.recipes.get(entry.getValue().getType()).remove(entry.getKey());
                    this.registerRecipe(mapped);
                }
            }
        }
    }

    public void mapRecipeID(Identifier id, Function<Recipe<?>, Recipe<?>> func) {
        var recipe = this.recipesById.get(id);
        if (recipe != null) {
            var mapped = func.apply(recipe);
            if (recipe != mapped) {
                this.removeRecipeID(id);
                this.registerRecipe(mapped);
            }
        }
    }
}
