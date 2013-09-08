/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.plugin.idea.moduletypes;

import com.arcbees.plugin.idea.domain.Archetype;
import com.arcbees.plugin.idea.domain.ProjectConfigModel;
import com.arcbees.plugin.idea.wizards.createproject.CreateProjectWizard;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.maven.shared.invoker.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.MavenUtil;


import java.io.*;
import java.util.Collections;
import java.util.Properties;

public class CreateProjectBuilder extends JavaModuleBuilder implements SourcePathsBuilder, ModuleBuilderListener {
    private CreateProjectWizard projectWizard;

    public CreateProjectBuilder() {
        addListener(this);
    }

    // TODO
    @Override
    public void moduleCreated(@NotNull Module module) {
        Project project = module.getProject();

        final File workingDir;
        try {
            workingDir = FileUtil.createTempDirectory("archetype", "tmp");
            workingDir.deleteOnExit();
        }
        catch (IOException e) {
            e.printStackTrace(); // TODO
            return;
        }

        try {
            generateArchetype(project, workingDir);
        } catch (MavenInvocationException e) {
            e.printStackTrace();  // TODO
        }

        // TODO
        System.out.println("finished");
    }

    @Override
    public ModuleType getModuleType() {
        return CreateProjectModuleType.getInstance();
    }

    @Override
    public String getGroupName() {
        return CreateProjectModuleType.GWT_GROUP;
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdk) {
        return sdk == JavaSdk.getInstance();
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(WizardContext wizardContext, ModulesProvider modulesProvider) {
        projectWizard = new CreateProjectWizard(this, wizardContext, modulesProvider);

        return new ModuleWizardStep[] { projectWizard };
    }

    private void generateArchetype(Project project, File workingDir) throws MavenInvocationException {
        ProjectConfigModel projectConfig = getProjectConfig();
        Archetype archetype = getProjectConfigArchetype();

        File mavenHome = MavenUtil.resolveMavenHomeDirectory(null/** override maven bin path **/);

        InvocationRequest request = new DefaultInvocationRequest();
        request.setGoals( Collections.singletonList("archetype:generate") );
        request.setInteractive(false);

        Properties properties = new Properties();

        // select archetype parameters
        properties.setProperty("archetypeRepository", archetype.getRepository());
        properties.setProperty("archetypeGroupId", archetype.getGroupId());
        properties.setProperty("archetypeArtifactId", archetype.getArtifactId());
        properties.setProperty("archetypeVersion", archetype.getVersion());

        // new project parameters
        properties.setProperty("groupId", projectConfig.getGroupId());
        properties.setProperty("artifactId", projectConfig.getArtifactId());
        properties.setProperty("module", projectConfig.getModuleName());

        // generate parameters
        properties.setProperty("interactiveMode", "false");
        request.setProperties(properties);

        Invoker invoker = new DefaultInvoker();
        invoker.setWorkingDirectory(workingDir);
        invoker.setMavenHome(mavenHome);
        InvocationResult result = invoker.execute(request);

        // post import tasks
        copyGeneratedFilesToProject(project, workingDir);

        try {
            importMavenProject(project);
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }

    /**
     * TODO import the project as a maven project.
     *
     * 1. does the idea compiler need to be setup
     * 2. I wish I could easily use the project manager and then use the MavenProjectImporter
     */
    private void importMavenProject(Project project) throws IOException {
        ProjectConfigModel projectConfig = getProjectConfig();
        String pomPath = project.getBasePath() + File.separator + "pom.xml";
        File pom = new File(pomPath);

        VirtualFile pomVirtFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(pom);
        MavenProject mavenProject = new MavenProject(pomVirtFile);


//        String pluginGroupID = "com.arcbees.plugin.idea.mavenimporter";
//        String pluginArtifactID = "com.arcbees.plugin.idea";
//        ArchetypeMavenImporter mavenImporter = new ArchetypeMavenImporter(pluginGroupID, pluginArtifactID);


        //MavenProjectsManager myProjectsManager = MavenProjectsManager.getInstance(project);

        System.out.println("end");
    }

    private void copyGeneratedFilesToProject(Project project, File workingDir) {
        ProjectConfigModel projectConfig = getProjectConfig();
        String baseDir = project.getBasePath();
        File baseDirFile = new File(baseDir);

        try {
            FileUtil.copyDir(new File(workingDir, projectConfig.getArtifactId()), baseDirFile);
        }
        catch (IOException e) {
            e.printStackTrace(); // TODO
        }

        FileUtil.delete(workingDir);
        LocalFileSystem.getInstance().refreshWithoutFileWatcher(true);
    }

    private ProjectConfigModel getProjectConfig() {
        ProjectConfigModel projectConfig = new ProjectConfigModel();
        projectWizard.getData(projectConfig);
        return projectConfig;
    }

    private Archetype getProjectConfigArchetype() {
        ProjectConfigModel projectConfig = getProjectConfig();
        return projectConfig.getArchetypeSelected();
    }
}
