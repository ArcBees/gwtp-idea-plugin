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

package com.arcbees.plugin.idea.wizards;

import com.intellij.ide.util.newProjectWizard.StepSequence;
import com.intellij.ide.util.newProjectWizard.modes.WizardMode;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateProjectWizard extends WizardMode {
    @NotNull
    @Override
    public String getDisplayName(WizardContext wizardContext) {
        return null;
    }

    @NotNull
    @Override
    public String getDescription(WizardContext wizardContext) {
        return null;
    }

    @Override
    public boolean isAvailable(WizardContext wizardContext) {
        return false;
    }

    @Nullable
    @Override
    protected StepSequence createSteps(WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return null;
    }

    @Nullable
    @Override
    public ProjectBuilder getModuleBuilder() {
        return null;
    }

    @Override
    public void onChosen(boolean b) {

    }
}
