@import io.jenkins.tools.pluginmodernizer.core.model.Plugin
@import io.jenkins.tools.pluginmodernizer.core.model.Recipe
@param Plugin plugin
@param Recipe recipe

Hello `${plugin.getName()}` developers! :wave:

This is an automated pull request created by the [Jenkins Plugin Modernizer](https://github.com/jenkins-infra/plugin-modernizer-tool) tool. The tool has applied the following recipe to modernize the plugin:

<details aria-label="Recipe details for ${recipe.getDisplayName()}">
    <summary>${recipe.getDisplayName()}</summary>
    <p><em>${recipe.getName()}</em></p>
    <blockquote>${recipe.getDescription()}</blockquote>
</details>

## Why is this important?

### Removing `developers` Tag from `pom.xml`

Jenkins no longer requires the `developers` tag in `pom.xml` since the `developers` section in the pom.xml file was traditionally used to specify individuals responsible for the plugin.
However, Jenkins has transitioned to using the Repository Permission Updater (RPU) for managing permissions and developer information.

Benefits of Removing `developers` Tag:

- **Simplification:** Eliminates unnecessary metadata from the pom.xml, making it cleaner and more maintainable.
- **Consistency:** Ensures that developer information is managed centrally through the RPU, reducing discrepancies.
- **Security:** Relies on the RPU's controlled permission management, enhancing the security of artifact deployments.

By removing the `developers` tag, we adhere to the modern Jenkins infrastructure standards and prevent the inclusion of outdated or redundant developer information in plugin metadata.