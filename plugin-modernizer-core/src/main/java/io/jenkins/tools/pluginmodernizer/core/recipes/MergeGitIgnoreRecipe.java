package io.jenkins.tools.pluginmodernizer.core.recipes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.text.PlainText;
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

    private static class GitIgnoreMerger extends TreeVisitor<PlainText, ExecutionContext> {
        private final Path archetypeGitIgnorePath;

        GitIgnoreMerger(Path archetypeGitIgnorePath) {
            this.archetypeGitIgnorePath = archetypeGitIgnorePath;
        }

        @Override
        public PlainText visit(Tree tree, ExecutionContext ctx) {
            PlainText text = (PlainText) tree;
            if (!(tree instanceof PlainText)) {
                return null;
            }
            // Check if the current file is a `.gitignore` file
            if (!text.getSourcePath().getFileName().toString().equals(".gitignore")) {
                return text;
            }

            Path gitIgnorePath = text.getSourcePath();
            try {
                if (Files.exists(gitIgnorePath) && Files.exists(archetypeGitIgnorePath)) {
                    String existingContent = Files.readString(gitIgnorePath);
                    String archetypeContent = Files.readString(archetypeGitIgnorePath);
                    String mergedContent = mergeGitIgnoreFiles(existingContent, archetypeContent);

                    // Write back the merged content to the file
                    Files.writeString(gitIgnorePath, mergedContent);
                    LOG.info("Merged .gitignore at {}", gitIgnorePath);
                }
            } catch (IOException e) {
                LOG.error("Error processing .gitignore files at {}", gitIgnorePath, e);
                throw new RuntimeException("Failed to process .gitignore files", e);
            }

            return text;
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
                if (!trimmed.isEmpty() && !trimmed.startsWith("#") && !existingLines.contains(trimmed)) {
                    merged.append(line).append("\n");
                }
            }

            return merged.toString();
        }
    }
}
