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
import com.arcbees.plugin.idea.icons.PluginIcons;
import com.arcbees.plugin.idea.wizards.createproject.CreateProjectWizard;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.apache.maven.shared.invoker.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;

import javax.swing.*;
import java.io.*;
import java.util.Collections;
import java.util.Properties;

public class CreateProjectBuilder extends MavenModuleBuilder implements SourcePathsBuilder, ModuleBuilderListener {
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

    private void displayBalloon(Project project, String htmlText, MessageType messageType) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(htmlText, messageType, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }

    @Override
    public String getPresentableName() {
        return "GWTP";
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
    public Icon getBigIcon() {
        // TODO 24x24
        return PluginIcons.GWTP_ICON_16x16;
    }

    @Override
    public Icon getNodeIcon() {
        return PluginIcons.GWTP_ICON_16x16;
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
