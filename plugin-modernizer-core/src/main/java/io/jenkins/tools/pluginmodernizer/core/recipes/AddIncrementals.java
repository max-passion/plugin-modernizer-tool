package io.jenkins.tools.pluginmodernizer.core.recipes;

import io.jenkins.tools.pluginmodernizer.core.config.Settings;
import io.jenkins.tools.pluginmodernizer.core.extractor.ArchetypeCommonFile;
import io.jenkins.tools.pluginmodernizer.core.visitors.AddIncrementalsVisitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.intellij.lang.annotations.Language;
import org.openrewrite.ExecutionContext;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.SourceFile;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.text.PlainTextParser;
import org.openrewrite.xml.XmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Recipe to enable incrementals in a Jenkins plugin.
 * Transforms the POM to use Git-based versioning and creates required .mvn files.
 */
public class AddIncrementals extends ScanningRecipe<AddIncrementals.ConfigState> {

    private static final Logger LOG = LoggerFactory.getLogger(AddIncrementals.class);

    @Language("xml")
    private static final String MAVEN_EXTENSIONS_TEMPLATE = """
            <?xml version="1.0" encoding="UTF-8"?>
            <extensions xmlns="http://maven.apache.org/EXTENSIONS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/EXTENSIONS/1.0.0 http://maven.apache.org/xsd/core-extensions-1.0.0.xsd">
              <extension>
                <groupId>io.jenkins.tools.incrementals</groupId>
                <artifactId>git-changelist-maven-extension</artifactId>
                <version>%s</version>
              </extension>
            </extensions>
            """;

    @Language("txt")
    private static final String MAVEN_CONFIG_TEMPLATE = """
            -Pconsume-incrementals
            -Pmight-produce-incrementals
            -Dchangelist.format=%d.v%s
            """;

    @Override
    public String getDisplayName() {
        return "Add incrementals";
    }

    @Override
    public String getDescription() {
        return "Enables incrementals by transforming POM version structure and creating .mvn configuration files.";
    }

    @Override
    public ConfigState getInitialValue(ExecutionContext ctx) {
        return new ConfigState();
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(ConfigState state) {
        return new TreeVisitor<>() {
            @Override
            public Tree visit(Tree tree, ExecutionContext ctx) {
                if (tree instanceof SourceFile sourceFile) {
                    if (ArchetypeCommonFile.MAVEN_CONFIG.same(sourceFile.getSourcePath())) {
                        LOG.debug(".mvn/maven.config already exists. Marking as present.");
                        state.setMavenConfigExists(true);
                    }
                    if (ArchetypeCommonFile.MAVEN_EXTENSIONS.same(sourceFile.getSourcePath())) {
                        LOG.debug(".mvn/extensions.xml already exists. Marking as present.");
                        state.setMavenExtensionsExists(true);
                    }
                    if (ArchetypeCommonFile.POM.same(sourceFile.getSourcePath())) {
                        LOG.debug("POM file found. Will be processed by visitor.");
                        state.setPomExists(true);
                    }
                }
                return tree;
            }
        };
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor(ConfigState state) {
        return new AddIncrementalsVisitor();
    }

    @Override
    public Collection<SourceFile> generate(ConfigState state, ExecutionContext ctx) {
        if (!state.isPomExists()) {
            LOG.warn("No pom.xml found. Cannot generate .mvn files.");
            return Collections.emptyList();
        }

        Collection<SourceFile> generatedFiles = new ArrayList<>();

        if (!state.isMavenConfigExists()) {
            LOG.debug("Generating .mvn/maven.config");
            generatedFiles.addAll(PlainTextParser.builder()
                    .build()
                    .parse(MAVEN_CONFIG_TEMPLATE)
                    .map(brandNewFile ->
                            (SourceFile) brandNewFile.withSourcePath(ArchetypeCommonFile.MAVEN_CONFIG.getPath()))
                    .collect(Collectors.toList()));
        }

        if (!state.isMavenExtensionsExists()) {
            LOG.debug("Generating .mvn/extensions.xml");
            String extensionsXml = MAVEN_EXTENSIONS_TEMPLATE.formatted(Settings.getIncrementalExtensionVersion());
            generatedFiles.addAll(XmlParser.builder()
                    .build()
                    .parse(extensionsXml)
                    .map(brandNewFile ->
                            (SourceFile) brandNewFile.withSourcePath(ArchetypeCommonFile.MAVEN_EXTENSIONS.getPath()))
                    .collect(Collectors.toList()));
        }

        return generatedFiles;
    }

    /**
     * Configuration state for the recipe
     */
    public static class ConfigState {
        private boolean mavenConfigExists = false;
        private boolean mavenExtensionsExists = false;
        private boolean pomExists = false;

        public boolean isMavenConfigExists() {
            return mavenConfigExists;
        }

        public void setMavenConfigExists(boolean value) {
            mavenConfigExists = value;
        }

        public boolean isMavenExtensionsExists() {
            return mavenExtensionsExists;
        }

        public void setMavenExtensionsExists(boolean value) {
            mavenExtensionsExists = value;
        }

        public boolean isPomExists() {
            return pomExists;
        }

        public void setPomExists(boolean value) {
            pomExists = value;
        }
    }
}
