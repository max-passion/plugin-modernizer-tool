package io.jenkins.tools.pluginmodernizer.cli.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jenkins.tools.pluginmodernizer.core.config.Config;
import io.jenkins.tools.pluginmodernizer.core.model.ModernizerException;
import io.jenkins.tools.pluginmodernizer.core.model.Plugin;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

/**
 * Test for {@link PluginOptions}
 */
public class PluginOptionsTest {

    @Test
    public void testWithoutPluginSetToDefault() {
        Config.Builder builder = Config.builder();
        PluginOptions pluginOptions = new PluginOptions();
        // Test
        assertThrows(
                ModernizerException.class,
                () -> {
                    pluginOptions.config(builder);
                },
                "Path does not contain a Jenkins plugin: .");
    }

    @Test
    public void testWithOnePlugin() throws Exception {

        Config.Builder builder = Config.builder();
        PluginOptions pluginOptions = new PluginOptions();

        // Set one plugin
        Field field = ReflectionUtils.findFields(
                        PluginOptions.class,
                        f -> f.getName().equals("plugins"),
                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)
                .get(0);
        field.setAccessible(true);
        field.set(pluginOptions, List.of(Plugin.build("one-plugin")));
        pluginOptions.config(builder);

        // Check only one plugin on the list
        Config config = builder.build();
        assertEquals(1, config.getPlugins().size(), "Only one plugin should be on the list");
        assertEquals("one-plugin", config.getPlugins().get(0).getName(), "Plugin name should be 'one-plugin'");
    }
}
