package io.jenkins.tools.pluginmodernizer.core.extractor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.tools.pluginmodernizer.core.impl.CacheManager;
import io.jenkins.tools.pluginmodernizer.core.model.CacheEntry;
import io.jenkins.tools.pluginmodernizer.core.model.Plugin;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modernization metadata for a plugin extracted after executing the recipes
 */
public class ModernizationMetadata extends CacheEntry<ModernizationMetadata> {

    private static final Logger LOG = LoggerFactory.getLogger(ModernizationMetadata.class);

    /**
     * Name of the plugin
     */
    private String pluginName;

    /**
     * Repository of the plugin
     */
    private String pluginRepository;

    /**
     * Version of the plugin
     */
    private String pluginVersion;

    /**
     * Baseline for the RPU
     */
    private String rpuBaseline;

    /**
     * Name of the migration
     */
    private String migrationName;

    /**
     * Description of the migration
     */
    private String migrationDescription;

    /**
     * Tags for the migration
     */
    private Set<String> tags;

    /**
     * Unique identifier for the migration
     */
    private String migrationId;

    /**
     * Number of deprecated APIs removed by the migration
     */
    private Integer removedDeprecatedApis;

    /**
     * The pull request URL to the modernized plugin if any
     */
    private String pullRequestUrl = "";

    /**
     * The pull request status to the modernized plugin if any
     */
    private String pullRequestStatus = "";

    /**
     * The tool state when performing the modernization
     */
    private boolean dryRun;

    /**
     * Number of additions
     */
    private Integer additions;

    /**
     * Number of deletions
     */
    private Integer deletions;

    /**
     * Number of changes files
     */
    private Integer changedFiles;

    /**
     * Create a new modernization metadata
     * Store the metadata in the relative target directory of current folder
     */
    public ModernizationMetadata() {
        super(
                new CacheManager(Path.of("target")),
                ModernizationMetadata.class,
                CacheManager.MODERNIZATION_METADATA_CACHE_KEY,
                Path.of("."));
    }

    /**
     * Create a new modernization metadata with the given key
     * @param key The key
     */
    @JsonCreator
    public ModernizationMetadata(@JsonProperty("key") String key) {
        super(new CacheManager(Path.of("target")), ModernizationMetadata.class, key, Path.of("."));
    }

    /**
     * Create a new modernization metadata. Store the metadata at the root of the given cache manager
     * @param cacheManager The cache manager
     */
    public ModernizationMetadata(CacheManager cacheManager) {
        super(
                cacheManager,
                ModernizationMetadata.class,
                CacheManager.MODERNIZATION_METADATA_CACHE_KEY,
                cacheManager.root());
    }

    /**
     * Create a new modernization metadata. Store the metadata to the plugin subdirectory of the given cache manager
     * @param cacheManager The cache manager
     * @param plugin The plugin
     */
    public ModernizationMetadata(CacheManager cacheManager, Plugin plugin) {
        super(
                cacheManager,
                ModernizationMetadata.class,
                CacheManager.MODERNIZATION_METADATA_CACHE_KEY,
                Path.of(plugin.getName()));
    }

    /**
     * Validate the fields of modernization metadata
     * @return true if all required fields are present, else false
     */
    public boolean validate() {
        Map<String, Object> requiredFields = new HashMap<>();
        requiredFields.put("pluginName", pluginName);
        requiredFields.put("pluginRepository", pluginRepository);
        requiredFields.put("pluginVersion", pluginVersion);
        requiredFields.put("rpuBaseline", rpuBaseline);
        requiredFields.put("migrationName", migrationName);
        requiredFields.put("migrationDescription", migrationDescription);
        requiredFields.put("tags", (tags != null && !tags.isEmpty()) ? tags : null);
        requiredFields.put("migrationId", migrationId);
        requiredFields.put("dryRun", dryRun);
        requiredFields.put("additions", additions);
        requiredFields.put("deletions", deletions);
        requiredFields.put("changedFiles", changedFiles);

        List<String> missingFields = requiredFields.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .toList();

        if (!missingFields.isEmpty()) {
            LOG.info("Missing required fields: {}", String.join(", ", missingFields));
            return false;
        }

        return true;
    }

    public String getPluginRepository() {
        return pluginRepository;
    }

    public void setPluginRepository(String pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getMigrationName() {
        return migrationName;
    }

    public void setMigrationName(String migrationName) {
        this.migrationName = migrationName;
    }

    public String getMigrationDescription() {
        return migrationDescription;
    }

    public void setMigrationDescription(String migrationDescription) {
        this.migrationDescription = migrationDescription;
    }

    public Integer getRemovedDeprecatedApis() {
        return removedDeprecatedApis;
    }

    public void setRemovedDeprecatedApis(Integer removedDeprecatedApis) {
        this.removedDeprecatedApis = removedDeprecatedApis;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getMigrationId() {
        return migrationId;
    }

    public void setMigrationId(String migrationId) {
        this.migrationId = migrationId;
    }

    public String getRpuBaseline() {
        return rpuBaseline;
    }

    public void setRpuBaseline(String rpuBaseline) {
        this.rpuBaseline = rpuBaseline;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getPullRequestUrl() {
        return pullRequestUrl;
    }

    public void setPullRequestUrl(String pullRequestUrl) {
        this.pullRequestUrl = pullRequestUrl;
    }

    public String getPullRequestStatus() {
        return pullRequestStatus;
    }

    public void setPullRequestStatus(String pullRequestStatus) {
        this.pullRequestStatus = pullRequestStatus;
    }

    public boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public Integer getAdditions() {
        return additions;
    }

    public void setAdditions(Integer additions) {
        this.additions = additions;
    }

    public Integer getDeletions() {
        return deletions;
    }

    public void setDeletions(Integer deletions) {
        this.deletions = deletions;
    }

    public Integer getChangedFiles() {
        return changedFiles;
    }

    public void setChangedFiles(Integer changedFiles) {
        this.changedFiles = changedFiles;
    }
}
