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

import com.arcbees.plugin.idea.wizards.createproject.CreateProjectWizard;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.apache.maven.shared.invoker.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.utils.MavenUtil;

import java.io.File;
import java.util.Collections;
import java.util.Properties;

public class CreateProjectBuilder extends JavaModuleBuilder implements SourcePathsBuilder, ModuleBuilderListener {
    public CreateProjectBuilder() {
        addListener(this);
    }

    // TODO
    @Override
    public void moduleCreated(@NotNull Module module) {
        Project project = module.getProject();
        String basePath = project.getBasePath();

        try {
            generateArchetype(project, new File(basePath));
        } catch (MavenInvocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
        return new ModuleWizardStep[] {
                new CreateProjectWizard(this, wizardContext, modulesProvider)
        };
    }

    // TODO
    private void generateArchetype(Project project, File basePath) throws MavenInvocationException {
        File mavenHome = MavenUtil.resolveMavenHomeDirectory(null/** override maven bin path **/);

        InvocationRequest request = new DefaultInvocationRequest();
        request.setGoals( Collections.singletonList("archetype:generate") );
        request.setInteractive(false);

        Properties properties = new Properties();

        // select archetype parameters
        properties.setProperty("archetypeRepository", "https://oss.sonatype.org/content/repositories/snapshots/");
        properties.setProperty("archetypeGroupId", "com.arcbees.archetypes");
        properties.setProperty("archetypeArtifactId", "gwtp-basic-archetype");
        properties.setProperty("archetypeVersion", "1.0-SNAPSHOT");

        // new project parameters
        properties.setProperty("groupId", "com.projectname.project");
        properties.setProperty("artifactId", "new-project-name");
        properties.setProperty("module", "Project");

        // generate parameters
        properties.setProperty("interactiveMode", "false");
        request.setProperties(properties);

        Invoker invoker = new DefaultInvoker();
        invoker.setWorkingDirectory(basePath);
        invoker.setMavenHome(mavenHome);
        InvocationResult result = invoker.execute(request);
    }
}
