package net.anvilcraft.anvillib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.anvilcraft.anvillib.cosmetics.ClientEventHandler;
import net.anvilcraft.anvillib.event.ApplyRecipesEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.anvilcraft.anvillib.recipe.RecipesEvent;

import java.util.HashMap;
import java.util.Map;

public class AnvilLib {
    public static final String MODID = "anvillib";
    public static final Logger LOGGER = LogManager.getLogger();

    public static String VERSION = null;

    public static void initialize(String version) {
        VERSION = version;
        Bus.MAIN.register(ApplyRecipesEvent.class, (event) -> {
            Map<RecipeType<?>, Map<ResourceLocation, RecipeHolder<?>>> recipes = new HashMap<>();
            event.recipeManager().byType.forEach((type, holder) ->
                    recipes.computeIfAbsent(type, v -> new HashMap<>()).put(holder.id(), holder)
            );

            var ev = new RecipesEvent(recipes, new HashMap<>(event.recipeManager().byName));
            Bus.MAIN.fire(ev);
            event.recipeManager().replaceRecipes(ev.recipesById.values());
        });
    }

    public static void initializeClient() {
        Bus.MAIN.register(new ClientEventHandler());
    }
}
