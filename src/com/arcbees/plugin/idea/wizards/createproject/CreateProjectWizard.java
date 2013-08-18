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

package com.arcbees.plugin.idea.wizards.createproject;

import com.arcbees.plugin.idea.moduletypes.CreateProjectBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;

import javax.swing.*;

public class CreateProjectWizard extends ModuleWizardStep {
    private final CreateProjectBuilder createProjectBuilder;
    private final WizardContext wizardContext;
    private final ModulesProvider modulesProvider;

    private JPanel mainPanel;
    private JTextField artifactId;
    private JTextField groupId;
    private JTable archetypes;

    public CreateProjectWizard(CreateProjectBuilder createProjectBuilder, WizardContext wizardContext,
                               ModulesProvider modulesProvider) {
        this.createProjectBuilder = createProjectBuilder;
        this.wizardContext = wizardContext;
        this.modulesProvider = modulesProvider;
    }

    @Override
    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public void updateDataModel() {
        // TODO
    }
}
