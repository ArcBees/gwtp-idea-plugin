package com.arcbees.plugin.idea.moduletypes;

import com.intellij.openapi.module.Module;
import org.jetbrains.idea.maven.importing.MavenImporter;
import org.jetbrains.idea.maven.importing.MavenModifiableModelsProvider;
import org.jetbrains.idea.maven.importing.MavenRootModelAdapter;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectChanges;
import org.jetbrains.idea.maven.project.MavenProjectsProcessorTask;
import org.jetbrains.idea.maven.project.MavenProjectsTree;

import java.util.List;
import java.util.Map;

public class ArchetypeMavenImporter extends MavenImporter {
    public ArchetypeMavenImporter(String pluginGroupID, String pluginArtifactID) {
        super(pluginGroupID, pluginArtifactID);
    }

    @Override
    public void preProcess(Module module, MavenProject mavenProject, MavenProjectChanges mavenProjectChanges, MavenModifiableModelsProvider mavenModifiableModelsProvider) {

    }

    @Override
    public void process(MavenModifiableModelsProvider mavenModifiableModelsProvider, Module module, MavenRootModelAdapter mavenRootModelAdapter, MavenProjectsTree mavenProjectsTree, MavenProject mavenProject, MavenProjectChanges mavenProjectChanges, Map<MavenProject, String> mavenProjectStringMap, List<MavenProjectsProcessorTask> mavenProjectsProcessorTasks) {

    }
}
