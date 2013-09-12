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

import com.arcbees.plugin.idea.domain.Archetype;
import com.arcbees.plugin.idea.domain.ArchetypeCollection;
import com.arcbees.plugin.idea.domain.ProjectConfigModel;
import com.arcbees.plugin.idea.moduletypes.CreateProjectBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.AsyncProcessIcon;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * TODO loading icon
 * TODO validation of project model settings, disable finish until all parameters are retrieved.
 * TODO update project model settings
 */
public class CreateProjectWizard extends ModuleWizardStep {
    private final AsyncProcessIcon loadingIcon = new AsyncProcessIcon.Big(getClass() + ".loading");

    private final CreateProjectBuilder createProjectBuilder;
    private final WizardContext wizardContext;
    private final ModulesProvider modulesProvider;

    private JPanel mainPanel;
    private JTextField artifactId;
    private JTextField groupId;
    private JTable archetypesTable;
    private JPanel loadingPanel;
    private JTextField moduleName;
    private Archetype archetypeSelected;

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
    }

    @Override
    public void updateStep() {
        fetchArchetypes();
    }

    @Override
    public void disposeUIResources() {
        loadingIcon.dispose();
        super.disposeUIResources();
    }

    @Override
    public boolean validate() throws ConfigurationException {
        if (StringUtil.isEmptyOrSpaces(artifactId.getText())) {
            throw new ConfigurationException("Please, specify artifactId");
        }

        if (StringUtil.isEmptyOrSpaces(groupId.getText())) {
            throw new ConfigurationException("Please, specify groupId");
        }

        if (StringUtil.isEmptyOrSpaces(moduleName.getText())) {
            throw new ConfigurationException("Please, specify moduleName");
        }

        if (archetypeSelected == null) {
            throw new ConfigurationException("Please, select an archetype");
        }

        return true;
    }

    private void fetchArchetypes() {
        displayLoading(true);

        archetypesTable.clearSelection();

        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            public void run() {
                FetchArchetypes fetch = new FetchArchetypes();
                final ArchetypeCollection collection = fetch.fetchArchetypes();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ArchetypesTableModel model = (ArchetypesTableModel) archetypesTable.getModel();
                        model.addCollection(collection);
                        displayLoading(false);
                        archetypesTable.repaint();
                    }
                });
            }
        });
    }

    private void displayLoading(boolean display) {
        loadingIcon.setSize(new Dimension(40, 40));
        loadingPanel.add(loadingIcon);
        loadingIcon.setVisible(display);
        loadingPanel.setVisible(display);
        if (display) {
            loadingIcon.resume();
        } else {
            loadingIcon.suspend();
        }
    }

    private void createUIComponents() {
        ArchetypesTableModel tableModel = new ArchetypesTableModel();
        archetypesTable = new JBTable(tableModel);
        archetypesTable.setShowGrid(true);
        archetypesTable.setShowHorizontalLines(true);
        archetypesTable.setShowVerticalLines(true);

        addTableSelectionModel();
    }

    private void addTableSelectionModel() {
        ListSelectionModel selectionModel = archetypesTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                ListSelectionModel model = archetypesTable.getSelectionModel();
                int selectedIndex = model.getLeadSelectionIndex();

                ArchetypesTableModel tableModel = (ArchetypesTableModel) archetypesTable.getModel();
                if (selectedIndex != -1) {
                    archetypeSelected = tableModel.getArchetype(selectedIndex);
                }
            }
        });
    }

    public void setData(ProjectConfigModel data) {
        artifactId.setText(data.getArtifactId());
        groupId.setText(data.getGroupId());
        moduleName.setText(data.getModuleName());
    }

    public void getData(ProjectConfigModel data) {
        data.setArtifactId(artifactId.getText().trim());
        data.setGroupId(groupId.getText().trim());
        data.setModuleName(moduleName.getText().trim());
        data.setArchetypeSelected(archetypeSelected);
    }

    public boolean isModified(ProjectConfigModel data) {
        if (artifactId.getText() != null ? !artifactId.getText().equals(data.getArtifactId()) : data.getArtifactId() != null)
            return true;
        if (groupId.getText() != null ? !groupId.getText().equals(data.getGroupId()) : data.getGroupId() != null)
            return true;
        if (moduleName.getText() != null ? !moduleName.getText().equals(data.getModuleName()) : data.getModuleName() != null)
            return true;
        return false;
    }
}
