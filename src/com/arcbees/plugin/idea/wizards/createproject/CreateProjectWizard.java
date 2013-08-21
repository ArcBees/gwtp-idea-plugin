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

import com.arcbees.plugin.idea.domain.ArchetypeCollection;
import com.arcbees.plugin.idea.moduletypes.CreateProjectBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * TODO loading icon
 * TODO validation of project model settings, disable finish until all parameters are retrieved.
 * TODO update project model settings
 */
public class CreateProjectWizard extends ModuleWizardStep {
    private final CreateProjectBuilder createProjectBuilder;
    private final WizardContext wizardContext;
    private final ModulesProvider modulesProvider;

    private JPanel mainPanel;
    private JTextField artifactId;
    private JTextField groupId;
    private JTable archetypesTable;

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

    @Override
    public void updateStep() {
        fetchArchetypes();
    }

    private void fetchArchetypes() {
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            public void run() {
                FetchArchetypes fetch = new FetchArchetypes();
                final ArchetypeCollection collection = fetch.fetchArchetypes();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ArchetypesTableModel model = (ArchetypesTableModel) archetypesTable.getModel();
                        model.addCollection(collection);
                        archetypesTable.repaint();
                    }
                });
            }
        });
    }

    private void createUIComponents() {
        ArchetypesTableModel tableModel = new ArchetypesTableModel();
        archetypesTable = new JBTable(tableModel);
        archetypesTable.setShowGrid(true);
        archetypesTable.setShowHorizontalLines(true);
        archetypesTable.setShowVerticalLines(true);

        ListSelectionModel selectionModel = archetypesTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                ListSelectionModel model = archetypesTable.getSelectionModel();
                int selected = model.getLeadSelectionIndex();
                // TODO
            }
        });
    }
}
