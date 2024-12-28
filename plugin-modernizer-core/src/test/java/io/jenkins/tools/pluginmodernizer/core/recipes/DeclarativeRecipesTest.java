package io.jenkins.tools.pluginmodernizer.core.recipes;

import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.java.Assertions.srcMainResources;
import static org.openrewrite.maven.Assertions.pomXml;
import static org.openrewrite.test.SourceSpecs.text;
import static org.openrewrite.yaml.Assertions.yaml;

import io.jenkins.tools.pluginmodernizer.core.config.Settings;
import io.jenkins.tools.pluginmodernizer.core.extractor.ArchetypeCommonFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RewriteTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test for declarative recipes from recipes.yml.
 */
public class DeclarativeRecipesTest implements RewriteTest {

    /**
     * LOGGER.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DeclarativeRecipesTest.class);

    /**
     * Test declarative recipe
     * See @{{@link io.jenkins.tools.pluginmodernizer.core.extractor.FetchMetadataTest} for more tests
     */
    @Test
    void fetchMinimalMetadata() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.FetchMetadata"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <name>Empty pom</name>
                        </project>
                        """));
    }

    @Test
    @EnabledOnOs(OS.LINUX) // https://github.com/openrewrite/rewrite-jenkins/pull/83
    void addCodeOwner() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.AddCodeOwner"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <name>Empty pom</name>
                        </project>
                        """),
                text(null, "* @jenkinsci/empty-plugin-developers", sourceSpecs -> {
                    sourceSpecs.path(ArchetypeCommonFile.CODEOWNERS.getPath());
                }));
    }

    @Test
    @EnabledOnOs(OS.LINUX) // https://github.com/openrewrite/rewrite-jenkins/pull/83
    void shouldNotAddCodeOwnerIfAdded() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.AddCodeOwner"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <name>Empty pom</name>
                        </project>
                        """),
                text("* @jenkinsci/empty-plugin-developers", sourceSpecs -> {
                    sourceSpecs.path(ArchetypeCommonFile.CODEOWNERS.getPath());
                }));
    }

    @Test
    @EnabledOnOs(OS.LINUX) // https://github.com/openrewrite/rewrite-jenkins/pull/83
    void shouldAddCodeOwnerIfNeeded() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.AddCodeOwner"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <name>Empty pom</name>
                        </project>
                        """),
                text(
                        """
                        * @my-custom-team
                        """,
                        """
                        * @jenkinsci/empty-plugin-developers
                        * @my-custom-team
                    """,
                        sourceSpecs -> {
                            sourceSpecs.path(Path.of(ArchetypeCommonFile.CODEOWNERS.getPath()));
                        }));
    }

    @Test
    void upgradeParentBom() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.UpgradeParentVersion"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.87</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                            <jenkins.version>2.440.3</jenkins.version>
                          </properties>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """,
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.88</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                            <jenkins.version>2.440.3</jenkins.version>
                          </properties>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """));
    }

    @Test
    void testUpgradeBomVersion() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.UpgradeBomVersion"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.87</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <jenkins.version>2.440.3</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-2.440.x</artifactId>
                                <version>3120.v4d898e1e9fc4</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """,
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.87</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <jenkins.version>2.440.3</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-2.440.x</artifactId>
                                <version>3435.v238d66a_043fb_</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """
                                .formatted(Settings.getJenkinsParentVersion(), Settings.getBomVersion())));
    }

    @Test
    void testRemoveDependenciesOverride() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.RemoveDependencyVersionOverride"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.87</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <jenkins.version>2.440.3</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-2.440.x</artifactId>
                                <version>3120.v4d898e1e9fc4</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <dependencies>
                            <dependency>
                              <groupId>io.jenkins.plugins</groupId>
                              <artifactId>json-api</artifactId>
                              <version>20240303-41.v94e11e6de726</version>
                            </dependency>
                          </dependencies>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """,
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.87</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <jenkins.version>2.440.3</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-2.440.x</artifactId>
                                <version>3120.v4d898e1e9fc4</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <dependencies>
                            <dependency>
                              <groupId>io.jenkins.plugins</groupId>
                              <artifactId>json-api</artifactId>
                            </dependency>
                          </dependencies>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """
                                .formatted(Settings.getJenkinsParentVersion(), Settings.getBomVersion())));
    }

    @Test
    void shouldRemoveExtraProperties() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.RemoveExtraMavenProperties"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <name>Empty pom</name>
                          <properties>
                            <configuration-as-code.version>1909.vb_b_f59a_27d013</configuration-as-code.version>
                            <java.version>11</java.version>
                          </properties>
                        </project>
                        """,
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <name>Empty pom</name>
                        </project>
                        """));
    }

    @Test
    void upgradeToRecommendCoreVersionTest() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.UpgradeToRecommendCoreVersion"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.87</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                            <jenkins.version>2.440.3</jenkins.version>
                          </properties>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """,
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.88</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                            <jenkins.version>2.452.4</jenkins.version>
                          </properties>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """));
    }

    @Test
    void upgradeToRecommendCoreVersionTestWithBaseline() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.UpgradeToRecommendCoreVersion"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.87</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <jenkins.baseline>2.440</jenkins.baseline>
                             <jenkins.version>${jenkins.baseline}.3</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-${jenkins.baseline}.x</artifactId>
                                <version>3435.v238d66a_043fb_</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """,
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.88</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <!-- https://www.jenkins.io/doc/developer/plugin-development/choosing-jenkins-baseline/ -->
                             <jenkins.baseline>2.452</jenkins.baseline>
                             <jenkins.version>${jenkins.baseline}.4</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-${jenkins.baseline}.x</artifactId>
                                <version>%s</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """
                                .formatted(Settings.getBomVersion())));
    }

    @Test
    void upgradeToUpgradeToLatestJava11CoreVersion() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.UpgradeToLatestJava11CoreVersion"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.55</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <jenkins.version>2.440.3</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-2.440.x</artifactId>
                                <version>3435.v238d66a_043fb_</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """,
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.88</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <!-- https://www.jenkins.io/doc/developer/plugin-development/choosing-jenkins-baseline/ -->
                             <jenkins.baseline>2.462</jenkins.baseline>
                             <jenkins.version>${jenkins.baseline}.3</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-${jenkins.baseline}.x</artifactId>
                                <version>%s</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """
                                .formatted(Settings.getBomVersion())));
    }

    @Test
    void upgradeToUpgradeToLatestJava8CoreVersion() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.UpgradeToLatestJava8CoreVersion"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.40</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <jenkins.version>2.303.3</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-2.303.x</artifactId>
                                <version>1500.ve4d05cd32975</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """,
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.51</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <!-- https://www.jenkins.io/doc/developer/plugin-development/choosing-jenkins-baseline/ -->
                             <jenkins.baseline>2.346</jenkins.baseline>
                             <jenkins.version>${jenkins.baseline}.3</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-${jenkins.baseline}.x</artifactId>
                                <version>1763.v092b_8980a_f5e</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """));
    }

    @Test
    void upgradeNextMajorParentVersionTest() {
        rewriteRun(
                spec -> {
                    var parser = JavaParser.fromJavaVersion().logCompilationWarningsAndErrors(true);
                    collectRewriteTestDependencies().forEach(parser::addClasspathEntry);
                    spec.recipeFromResource(
                                    "/META-INF/rewrite/recipes.yml",
                                    "io.jenkins.tools.pluginmodernizer.UpgradeNextMajorParentVersion")
                            .parser(parser);
                },
                mavenProject("test"),
                // language=xml
                pomXml(
                        """
                            <?xml version="1.0" encoding="UTF-8"?>
                            <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                              <modelVersion>4.0.0</modelVersion>
                              <parent>
                                <groupId>org.jenkins-ci.plugins</groupId>
                                <artifactId>plugin</artifactId>
                                <version>4.87</version>
                                <relativePath />
                              </parent>
                              <groupId>io.jenkins.plugins</groupId>
                              <artifactId>empty</artifactId>
                              <version>1.0.0-SNAPSHOT</version>
                              <packaging>hpi</packaging>
                              <name>Empty Plugin</name>
                              <properties>
                                <maven.compiler.release>11</maven.compiler.release>
                                <jenkins.version>2.440.3</jenkins.version>
                              </properties>
                              <repositories>
                                <repository>
                                  <id>repo.jenkins-ci.org</id>
                                  <url>https://repo.jenkins-ci.org/public/</url>
                                </repository>
                              </repositories>
                              <pluginRepositories>
                                <pluginRepository>
                                  <id>repo.jenkins-ci.org</id>
                                  <url>https://repo.jenkins-ci.org/public/</url>
                                </pluginRepository>
                              </pluginRepositories>
                            </project>
                            """,
                        """
                            <?xml version="1.0" encoding="UTF-8"?>
                            <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                              <modelVersion>4.0.0</modelVersion>
                              <parent>
                                <groupId>org.jenkins-ci.plugins</groupId>
                                <artifactId>plugin</artifactId>
                                <version>%s</version>
                                <relativePath />
                              </parent>
                              <groupId>io.jenkins.plugins</groupId>
                              <artifactId>empty</artifactId>
                              <version>1.0.0-SNAPSHOT</version>
                              <packaging>hpi</packaging>
                              <name>Empty Plugin</name>
                              <properties>
                                <jenkins.version>2.479.1</jenkins.version>
                              </properties>
                              <repositories>
                                <repository>
                                  <id>repo.jenkins-ci.org</id>
                                  <url>https://repo.jenkins-ci.org/public/</url>
                                </repository>
                              </repositories>
                              <pluginRepositories>
                                <pluginRepository>
                                  <id>repo.jenkins-ci.org</id>
                                  <url>https://repo.jenkins-ci.org/public/</url>
                                </pluginRepository>
                              </pluginRepositories>
                            </project>
                            """
                                .formatted(Settings.getJenkinsParentVersion())),
                srcMainResources(
                        // language=java
                        java(
                                """
                                import javax.servlet.ServletException;
                                import org.kohsuke.stapler.Stapler;
                                import org.kohsuke.stapler.StaplerRequest;

                                public class Foo {
                                    public void foo() {
                                        StaplerRequest req = Stapler.getCurrentRequest();
                                    }
                                }
                                """,
                                """
                                import jakarta.servlet.ServletException;
                                import org.kohsuke.stapler.Stapler;
                                import org.kohsuke.stapler.StaplerRequest2;

                                public class Foo {
                                    public void foo() {
                                        StaplerRequest2 req = Stapler.getCurrentRequest2();
                                    }
                                }
                                """)));
    }

    @Test
    void upgradeNextMajorParentVersionTestWithBom() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.UpgradeNextMajorParentVersion"),
                // language=xml
                pomXml(
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>4.87</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <jenkins.version>2.440.3</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-2.440.x</artifactId>
                                <version>3435.v238d66a_043fb_</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """,
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                          <modelVersion>4.0.0</modelVersion>
                          <parent>
                            <groupId>org.jenkins-ci.plugins</groupId>
                            <artifactId>plugin</artifactId>
                            <version>%s</version>
                            <relativePath />
                          </parent>
                          <groupId>io.jenkins.plugins</groupId>
                          <artifactId>empty</artifactId>
                          <version>1.0.0-SNAPSHOT</version>
                          <packaging>hpi</packaging>
                          <name>Empty Plugin</name>
                          <properties>
                             <!-- https://www.jenkins.io/doc/developer/plugin-development/choosing-jenkins-baseline/ -->
                             <jenkins.baseline>2.479</jenkins.baseline>
                             <jenkins.version>${jenkins.baseline}.1</jenkins.version>
                          </properties>
                          <dependencyManagement>
                            <dependencies>
                              <dependency>
                                <groupId>io.jenkins.tools.bom</groupId>
                                <artifactId>bom-${jenkins.baseline}.x</artifactId>
                                <version>%s</version>
                                <type>pom</type>
                                <scope>import</scope>
                              </dependency>
                            </dependencies>
                          </dependencyManagement>
                          <repositories>
                            <repository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </repository>
                          </repositories>
                          <pluginRepositories>
                            <pluginRepository>
                              <id>repo.jenkins-ci.org</id>
                              <url>https://repo.jenkins-ci.org/public/</url>
                            </pluginRepository>
                          </pluginRepositories>
                        </project>
                        """
                                .formatted(Settings.getJenkinsParentVersion(), Settings.getBomVersion())));
    }

    @Test
    void upgradeNextMajorParentVersionTestWithBaseline() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.UpgradeNextMajorParentVersion"),
                // language=xml
                pomXml(
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.87</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                     <jenkins.baseline>2.440</jenkins.baseline>
                     <jenkins.version>${jenkins.baseline}.3</jenkins.version>
                  </properties>
                  <dependencyManagement>
                    <dependencies>
                      <dependency>
                        <groupId>io.jenkins.tools.bom</groupId>
                        <artifactId>bom-${jenkins.baseline}.x</artifactId>
                        <version>3435.v238d66a_043fb_</version>
                        <type>pom</type>
                        <scope>import</scope>
                      </dependency>
                    </dependencies>
                  </dependencyManagement>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """,
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>%s</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                     <!-- https://www.jenkins.io/doc/developer/plugin-development/choosing-jenkins-baseline/ -->
                     <jenkins.baseline>2.479</jenkins.baseline>
                     <jenkins.version>${jenkins.baseline}.1</jenkins.version>
                  </properties>
                  <dependencyManagement>
                    <dependencies>
                      <dependency>
                        <groupId>io.jenkins.tools.bom</groupId>
                        <artifactId>bom-${jenkins.baseline}.x</artifactId>
                        <version>%s</version>
                        <type>pom</type>
                        <scope>import</scope>
                      </dependency>
                    </dependencies>
                  </dependencyManagement>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """
                                .formatted(Settings.getJenkinsParentVersion(), Settings.getBomVersion())));
    }

    @Test
    void addPluginBomTest() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.AddPluginsBom"),
                // language=xml
                pomXml(
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <jenkins.version>2.440.3</jenkins.version>
                    <configuration-as-code.version>1909.vb_b_f59a_27d013</configuration-as-code.version>
                    <java.version>11</java.version>
                  </properties>
                  <dependencies>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>gson-api</artifactId>
                      <version>2.11.0-41.v019fcf6125dc</version>
                    </dependency>
                  </dependencies>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """,
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <!-- https://www.jenkins.io/doc/developer/plugin-development/choosing-jenkins-baseline/ -->
                    <jenkins.baseline>2.440</jenkins.baseline>
                    <jenkins.version>${jenkins.baseline}.3</jenkins.version>
                  </properties>
                  <dependencyManagement>
                    <dependencies>
                      <dependency>
                        <groupId>io.jenkins.tools.bom</groupId>
                        <artifactId>bom-${jenkins.baseline}.x</artifactId>
                        <version>3435.v238d66a_043fb_</version>
                        <type>pom</type>
                        <scope>import</scope>
                      </dependency>
                    </dependencies>
                  </dependencyManagement>
                  <dependencies>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>gson-api</artifactId>
                    </dependency>
                  </dependencies>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """));
    }

    @Test
    @Disabled("See https://github.com/jenkins-infra/plugin-modernizer-tool/issues/517")
    void addPluginBomTestAndRemoveProperties() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.AddPluginsBom"),
                // language=xml
                pomXml(
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <jenkins.version>2.440.3</jenkins.version>
                    <configuration-as-code.version>1909.vb_b_f59a_27d013</configuration-as-code.version>
                    <java.version>11</java.version>
                  </properties>
                  <dependencies>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>gson-api</artifactId>
                      <version>2.11.0-41.v019fcf6125dc</version>
                    </dependency>
                    <dependency>
                      <groupId>io.jenkins</groupId>
                      <artifactId>configuration-as-code</artifactId>
                      <version>${configuration-as-code.version}</version>
                    </dependency>
                  </dependencies>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """,
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <!-- https://www.jenkins.io/doc/developer/plugin-development/choosing-jenkins-baseline/ -->
                    <jenkins.baseline>2.440</jenkins.baseline>
                    <jenkins.version>${jenkins.baseline}.3</jenkins.version>
                    <configuration-as-code.version>1909.vb_b_f59a_27d013</configuration-as-code.version>
                  </properties>
                  <dependencyManagement>
                    <dependencies>
                      <dependency>
                        <groupId>io.jenkins.tools.bom</groupId>
                        <artifactId>bom-${jenkins.baseline}.x</artifactId>
                        <version>3435.v238d66a_043fb_</version>
                        <type>pom</type>
                        <scope>import</scope>
                      </dependency>
                    </dependencies>
                  </dependencyManagement>
                  <dependencies>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>gson-api</artifactId>
                    </dependency>
                    <dependency>
                      <groupId>io.jenkins</groupId>
                      <artifactId>configuration-as-code</artifactId>
                      <version>${configuration-as-code.version}</version>
                    </dependency>
                  </dependencies>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """));
    }

    @Test
    void replaceLibrariesByApiPluginsSimple() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.ReplaceLibrariesWithApiPlugin"),
                // language=xml
                pomXml(
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <jenkins.version>2.452.4</jenkins.version>
                  </properties>
                  <dependencies>
                    <dependency>
                      <groupId>com.google.code.gson</groupId>
                      <artifactId>gson</artifactId>
                      <version>2.10.1</version>
                    </dependency>
                    <dependency>
                      <groupId>joda-time</groupId>
                      <artifactId>joda-time</artifactId>
                      <version>2.13.0</version>
                    </dependency>
                    <dependency>
                      <groupId>net.bytebuddy</groupId>
                      <artifactId>byte-buddy</artifactId>
                      <version>1.15.11</version>
                    </dependency>
                    <dependency>
                      <groupId>org.apache.commons</groupId>
                      <artifactId>commons-compress</artifactId>
                      <version>1.26.1</version>
                    </dependency>
                    <dependency>
                      <groupId>org.apache.commons</groupId>
                      <artifactId>commons-lang3</artifactId>
                      <version>3.17.0</version>
                    </dependency>
                    <dependency>
                      <groupId>org.apache.commons</groupId>
                      <artifactId>commons-text</artifactId>
                      <version>1.13.0</version>
                    </dependency>
                    <dependency>
                      <groupId>org.json</groupId>
                      <artifactId>json</artifactId>
                      <version>20240303</version>
                    </dependency>
                    <dependency>
                      <groupId>com.jayway.jsonpath</groupId>
                      <artifactId>json-path</artifactId>
                      <version>2.9.0</version>
                    </dependency>
                    <dependency>
                      <groupId>org.ow2.asm</groupId>
                      <artifactId>asm</artifactId>
                      <version>9.7.1</version>
                    </dependency>
                  </dependencies>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """,
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <jenkins.version>2.452.4</jenkins.version>
                  </properties>
                  <dependencies>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>byte-buddy-api</artifactId>
                      <version>1.15.11-99.v078c614a_5258</version>
                    </dependency>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>commons-lang3-api</artifactId>
                      <version>3.17.0-84.vb_b_938040b_078</version>
                    </dependency>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>commons-text-api</artifactId>
                      <version>1.12.0-129.v99a_50df237f7</version>
                    </dependency>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>gson-api</artifactId>
                      <version>2.11.0-85.v1f4e87273c33</version>
                    </dependency>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>joda-time-api</artifactId>
                      <version>2.13.0-93.v9934da_29b_a_e9</version>
                    </dependency>
                    <dependency>
                      <groupId>org.apache.commons</groupId>
                      <artifactId>commons-compress</artifactId>
                      <version>1.26.1</version>
                    </dependency>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>json-api</artifactId>
                      <version>20240303-101.v7a_8666713110</version>
                    </dependency>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>json-path-api</artifactId>
                      <version>2.9.0-118.v7f23ed82a_8b_8</version>
                    </dependency>
                    <dependency>
                      <groupId>org.ow2.asm</groupId>
                      <artifactId>asm</artifactId>
                      <version>9.7.1</version>
                    </dependency>
                  </dependencies>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """));
    }

    @Test
    void replaceLibrariesByApiPluginsAsm() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.ReplaceLibrariesWithApiPlugin"),
                // language=xml
                pomXml(
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <jenkins.version>2.479.1</jenkins.version>
                  </properties>
                  <dependencies>
                    <dependency>
                      <groupId>org.ow2.asm</groupId>
                      <artifactId>asm</artifactId>
                      <version>9.7.1</version>
                    </dependency>
                  </dependencies>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """,
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <jenkins.version>2.479.1</jenkins.version>
                  </properties>
                  <dependencies>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>asm-api</artifactId>
                      <version>9.7.1-97.v4cc844130d97</version>
                    </dependency>
                  </dependencies>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """));
    }

    @Test
    void replaceLibrariesByApiPluginsCompress() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.UseCompressApiPlugin"),
                // language=xml
                pomXml(
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <jenkins.version>2.489</jenkins.version>
                  </properties>
                  <dependencies>
                    <dependency>
                      <groupId>org.apache.commons</groupId>
                      <artifactId>commons-compress</artifactId>
                      <version>1.27.1</version>
                    </dependency>
                    <dependency>
                      <groupId>org.apache.commons</groupId>
                      <artifactId>commons-lang3</artifactId>
                      <version>3.17.0</version>
                    </dependency>
                  </dependencies>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """,
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <jenkins.version>2.489</jenkins.version>
                  </properties>
                  <dependencies>
                    <dependency>
                      <groupId>io.jenkins.plugins</groupId>
                      <artifactId>commons-compress-api</artifactId>
                      <version>1.27.1-1</version>
                    </dependency>
                  </dependencies>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """));
    }

    @Test
    void migrateToJenkinsBaseLinePropertyTest() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.MigrateToJenkinsBaseLineProperty"),
                // language=xml
                pomXml(
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <jenkins.version>2.440.3</jenkins.version>
                  </properties>
                  <dependencyManagement>
                    <dependencies>
                      <dependency>
                        <groupId>io.jenkins.tools.bom</groupId>
                        <artifactId>bom-2.440.x</artifactId>
                        <version>3435.v238d66a_043fb_</version>
                        <type>pom</type>
                        <scope>import</scope>
                      </dependency>
                    </dependencies>
                  </dependencyManagement>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """,
                        """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>org.jenkins-ci.plugins</groupId>
                    <artifactId>plugin</artifactId>
                    <version>4.88</version>
                    <relativePath />
                  </parent>
                  <groupId>io.jenkins.plugins</groupId>
                  <artifactId>empty</artifactId>
                  <version>1.0.0-SNAPSHOT</version>
                  <packaging>hpi</packaging>
                  <name>Empty Plugin</name>
                  <properties>
                    <!-- https://www.jenkins.io/doc/developer/plugin-development/choosing-jenkins-baseline/ -->
                    <jenkins.baseline>2.440</jenkins.baseline>
                    <jenkins.version>${jenkins.baseline}.3</jenkins.version>
                  </properties>
                  <dependencyManagement>
                    <dependencies>
                      <dependency>
                        <groupId>io.jenkins.tools.bom</groupId>
                        <artifactId>bom-${jenkins.baseline}.x</artifactId>
                        <version>3435.v238d66a_043fb_</version>
                        <type>pom</type>
                        <scope>import</scope>
                      </dependency>
                    </dependencies>
                  </dependencyManagement>
                  <repositories>
                    <repository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </repository>
                  </repositories>
                  <pluginRepositories>
                    <pluginRepository>
                      <id>repo.jenkins-ci.org</id>
                      <url>https://repo.jenkins-ci.org/public/</url>
                    </pluginRepository>
                  </pluginRepositories>
                </project>
                """));
    }

    /**
     * Note this test need to be adapted to fix the dependabot config
     * (For example to reduce frequency or increase frequency for API plugins)
     */
    @Test
    void shouldNotAddDependabotIfRenovateConfigured() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.SetupDependabot"),
                text(""), // Need one minimum file to trigger the recipe
                text("{}", sourceSpecs -> {
                    sourceSpecs.path(ArchetypeCommonFile.RENOVATE.getPath());
                }));
    }

    /**
     * Note this test need to be adapted to fix the dependabot config
     * (For example to reduce frequency or increase frequency for API plugins)
     */
    @Test
    void shouldNotChangeDependabotIfAlreadyExists() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.SetupDependabot"),
                text(""), // Need one minimum file to trigger the recipe
                // language=yaml
                yaml(
                        """
                    ---
                    version: 2
                    """,
                        sourceSpecs -> {
                            sourceSpecs.path(ArchetypeCommonFile.DEPENDABOT.getPath());
                        }));
    }

    @Test
    void shouldAddDependabot() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml", "io.jenkins.tools.pluginmodernizer.SetupDependabot"),
                text(""), // Need one minimum file to trigger the recipe
                // language=yaml
                yaml(
                        null,
                        """
                    version: 2
                    updates:
                    - package-ecosystem: maven
                      directory: /
                      schedule:
                        interval: monthly
                    - package-ecosystem: github-actions
                      directory: /
                      schedule:
                        interval: monthly
                    """,
                        sourceSpecs -> {
                            sourceSpecs.path(ArchetypeCommonFile.DEPENDABOT.getPath());
                        }));
    }

    /**
     * Collect rewrite test dependencies from target/openrewrite-classpath directory
     *
     * @return List of Path
     */
    private List<Path> collectRewriteTestDependencies() {
        try {
            List<Path> entries = Files.list(Path.of("target/openrewrite-classpath"))
                    .filter(p -> p.toString().endsWith(".jar"))
                    .toList();
            LOG.debug("Collected rewrite test dependencies: {}", entries);
            return entries;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
