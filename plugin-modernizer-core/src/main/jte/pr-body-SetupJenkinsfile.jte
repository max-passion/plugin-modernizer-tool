@import io.jenkins.tools.pluginmodernizer.core.model.Plugin
@import io.jenkins.tools.pluginmodernizer.core.model.Recipe
@param Plugin plugin
@param Recipe recipe
Hello `${plugin.getName()}` developers!

This is an automated pull request created by the [Jenkins Plugin Modernizer](https://github.com/jenkins-infra/plugin-modernizer-tool) tool. The tool has applied the following recipes to modernize the plugin:
<details>
    <summary>${recipe.getDisplayName()}</summary>
    <p><em>${recipe.getName()}</em></p>
    <blockquote>${recipe.getDescription()}</blockquote>
</details>

### Why is this important?

The `SetupJenkinsfile` recipe is crucial for ensuring that your Jenkins plugin is built and tested using the Jenkins infrastructure. By adding a Jenkinsfile, you enable continuous integration and continuous delivery (CI/CD) for your plugin, which helps in maintaining the quality and reliability of the plugin. It also ensures that your plugin is tested against the latest Jenkins core and other plugins, providing early feedback on any potential issues.
