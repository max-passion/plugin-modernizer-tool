package io.jenkins.tools.pluginmodernizer.core.recipes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergeGitIgnoreRecipe extends Recipe {
    private static final Logger LOG = LoggerFactory.getLogger(MergeGitIgnoreRecipe.class);
    private final Path archetypeGitIgnorePath;

    public MergeGitIgnoreRecipe(Path archetypeGitIgnorePath) {
        this.archetypeGitIgnorePath = archetypeGitIgnorePath;
    }

    @Override
    public String getDisplayName() {
        return "Merge .gitignore Entries";
    }

    @Override
    public String getDescription() {
        return "Merges .gitignore entries from archetype with existing .gitignore file.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new GitIgnoreMerger(archetypeGitIgnorePath);
    }

    private static class GitIgnoreMerger extends MavenIsoVisitor<ExecutionContext> {
        private final Path archetypeGitIgnorePath;

        GitIgnoreMerger(Path archetypeGitIgnorePath) {
            this.archetypeGitIgnorePath = archetypeGitIgnorePath;
        }

        @Override
        public Xml.Document visitDocument(Xml.Document document, ExecutionContext ctx) {
            Path projectPath = document.getSourcePath().getParent();
            if (projectPath == null) return document;

            Path gitIgnorePath = projectPath.resolve(".gitignore");

            try {
                if (Files.exists(gitIgnorePath) && Files.exists(archetypeGitIgnorePath)) {
                    String merged = mergeGitIgnoreFiles(
                            Files.readString(gitIgnorePath), Files.readString(archetypeGitIgnorePath));
                    Files.writeString(gitIgnorePath, merged);
                }
            } catch (IOException e) {
                LOG.error("Error processing .gitignore files", e);
            }
            return document;
        }

        private String mergeGitIgnoreFiles(String existing, String fromArchetype) {
            List<String> existingLines = existing.lines().collect(Collectors.toList());
            List<String> archetypeLines = fromArchetype.lines().collect(Collectors.toList());

            StringBuilder merged = new StringBuilder(existing);
            if (!existing.endsWith("\n")) {
                merged.append("\n");
            }
            merged.append("\n# Added from archetype\n");

            for (String line : archetypeLines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#") && !existingContains(existingLines, trimmed)) {
                    merged.append(line).append("\n");
                }
            }

            return merged.toString();
        }

        private boolean existingContains(List<String> existingLines, String newLine) {
            return existingLines.contains(newLine) || existingLines.contains(newLine + "/");
        }
    }
}
