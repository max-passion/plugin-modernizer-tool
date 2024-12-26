package io.jenkins.tools.pluginmodernizer.core.recipes;

import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.maven.Assertions.pomXml;

import io.jenkins.tools.pluginmodernizer.core.config.Settings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RewriteTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeclarativeRecipesTest implements RewriteTest {

    private static final Logger LOG = LoggerFactory.getLogger(DeclarativeRecipesTest.class);

    @Test
    void upgradeToRecommendCoreVersionTest() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.UpgradeToRecommendCoreVersion"),
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

    /**
     * Collect rewrite test dependencies from target/openrewrite-classpath directory
     * @return List of Path
     * @throws Exception if an I/O error occurs
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
                        """));
    }

    @Test
    void upgradeNextMajorParentVersionTestWithBom() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.UpgradeNextMajorParentVersion"),
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
    void migrateToJenkinsBaseLinePropertyTest() {
        rewriteRun(
                spec -> spec.recipeFromResource(
                        "/META-INF/rewrite/recipes.yml",
                        "io.jenkins.tools.pluginmodernizer.MigrateToJenkinsBaseLineProperty"),
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
}
