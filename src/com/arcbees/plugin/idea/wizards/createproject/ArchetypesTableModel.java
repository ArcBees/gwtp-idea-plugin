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

import javax.swing.table.DefaultTableModel;

public class ArchetypesTableModel extends DefaultTableModel {
    private static final String[] COLUMN_TITLES = {"Name", "Categories", "Tags"};

    public ArchetypesTableModel() {
        setColumnCount(3);
//        setColumnIdentifiers(COLUMN_TITLES);
        Object col = "test";
        addColumn(col);
    }

    public void addRow(Archetype item) {
        addRow(new Object[] {item});
    }
}