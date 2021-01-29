package com.kylemoore.foo;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.ivy.IvyPublication;
import org.gradle.api.publish.ivy.plugins.IvyPublishPlugin;
import org.gradle.api.publish.tasks.GenerateModuleMetadata;
import org.gradle.api.tasks.bundling.Jar;

public class CreatesFooPublicationPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPlugins().apply(JavaPlugin.class);
    project.getPlugins().apply(IvyPublishPlugin.class);
    PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
    IvyPublication fooPublication = publishing.getPublications().create("foo", IvyPublication.class, p -> {
      p.from(project.getComponents().getByName("java"));
    });
    project.getTasks().withType(GenerateModuleMetadata.class).configureEach(t -> t.setEnabled(false));

    // now, add a custom configuration to the publication
    configureFooPublication(project, fooPublication);

    // create sources jar
    Jar sourcesJar = project.getTasks().create("sourcesJar", Jar.class);
    sourcesJar.dependsOn(project.getTasks().getByName(JavaPlugin.CLASSES_TASK_NAME));
    sourcesJar.setGroup(JavaBasePlugin.DOCUMENTATION_GROUP);
    sourcesJar.setDescription("Generates a jar file containing the sources for the main source code.");
    sourcesJar.getArchiveClassifier().set("sources");

    sourcesJar.from(project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().getByName("main").getAllSource());
  }

  /**
   * {@link Project#afterEvaluate(Action)} is necessary due to limitations in the IvyPublication implementation
   * related to the Software Model.
   *
   * Premature configuration of the publication will cause the Software Component to be evaluated
   * too soon, producing a nearly empty descriptor.
   *
   * @see <a href="https://github.com/gradle/gradle/issues/12026">gradle/gradle#12026</a>
   * @param project the project containing the publication
   * @param fooPublication the publication to configure, after the project is evaluated
   */
  private void configureFooPublication(Project project, IvyPublication fooPublication) {
    project.afterEvaluate( p -> {
      fooPublication.configurations(configurations -> configurations.create("docs", docs -> {
        docs.extend("javadoc");
        docs.extend("sources");
      }));

      fooPublication.configurations(configurations -> configurations.create("sources"));
      fooPublication.artifact(project.getTasks().withType(Jar.class).getByName("sourcesJar"), sources -> {
        sources.setConf("sources");
      });
    });
  }

}
