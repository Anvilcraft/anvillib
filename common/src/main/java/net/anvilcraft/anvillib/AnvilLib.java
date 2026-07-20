package net.anvilcraft.anvillib;

import java.util.HashMap;
import java.util.Map;

import net.anvilcraft.anvillib.event.ApplyRecipesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.anvilcraft.anvillib.cosmetics.ClientEventHandler;
import net.anvilcraft.anvillib.event.Bus;
import net.anvilcraft.anvillib.recipe.RecipesEvent;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;

public class AnvilLib {
    public static final String MODID = "anvillib";
    public static final Logger LOGGER = LogManager.getLogger();

    public static void initialize() {
        if (Compat.hasGeckolib()) {
            GeckoLib.initialize();
        }
        Bus.MAIN.register(ApplyRecipesEvent.class, (event) -> {
            Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes = new HashMap<>();
            event.recipeManager().recipes.forEach((k, v) -> recipes.put(k, new HashMap<>(v)));

            var ev = new RecipesEvent(recipes, new HashMap<>(event.recipeManager().recipesById));
            Bus.MAIN.fire(ev);

            event.recipeManager().recipes = ev.recipes;
            event.recipeManager().recipesById = ev.recipesById;
        });
    }

    public static void initializeClient() {
        Bus.MAIN.register(new ClientEventHandler());
    }
}
