package io.jenkins.tools.pluginmodernizer.cli.converter;

import io.jenkins.tools.pluginmodernizer.core.config.Settings;
import io.jenkins.tools.pluginmodernizer.core.model.Recipe;
import java.util.Iterator;
import picocli.CommandLine;

/**
 * Custom converter for Recipe interface.
 */
public final class RecipeConverter implements CommandLine.ITypeConverter<Recipe>, Iterable<String> {
    @Override
    public Recipe convert(String value) {
        return Settings.AVAILABLE_RECIPES.stream()
                // Compare without and without the FQDN prefix
                .filter(recipe -> recipe.getName().equals(value)
                        || recipe.getName()
                                .replace(Settings.RECIPE_FQDN_PREFIX + ".", "")
                                .equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid recipe name: " + value));
    }

    @Override
    public Iterator<String> iterator() {
        return Settings.AVAILABLE_RECIPES.stream()
                .map(r -> r.getName().replace(Settings.RECIPE_FQDN_PREFIX + ".", ""))
                .iterator();
    }
}
