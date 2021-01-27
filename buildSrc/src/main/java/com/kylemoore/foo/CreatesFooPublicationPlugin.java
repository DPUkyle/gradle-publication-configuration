package com.kylemoore.foo;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.ivy.IvyPublication;
import org.gradle.api.publish.ivy.plugins.IvyPublishPlugin;
import org.gradle.api.publish.tasks.GenerateModuleMetadata;

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
    //configureFooPublication(fooPublication);
  }

  private void configureFooPublication(IvyPublication fooPublication) {
    fooPublication.configurations(configurations -> configurations.create("docs"));
  }

}
