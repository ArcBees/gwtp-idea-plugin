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

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ArchetypesTableModel extends DefaultTableModel {
    private static final String[] COLUMN_TITLES = {"Name", "Categories", "Tags"};
    private Archetype[] archetypesArray;

    public ArchetypesTableModel() {
    }

    @Override
    public int getColumnCount() {
        return COLUMN_TITLES.length;
    }

    @Override
    public String getColumnName(int i) {
        return COLUMN_TITLES[i];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public void addCollection(ArchetypeCollection collection) {
        clear();
        archetypesArray = null;

        List<Archetype> archetypes = collection.getArchetypes();
        if (archetypes == null) {
            return;
        }

        archetypesArray = new Archetype[archetypes.size()];
        archetypes.toArray(archetypesArray);

        int i = 0;
        for (Archetype archetype : archetypes) {
            addRow(archetype);

            archetypesArray[i] = archetype;
            i++;
        }
    }

    public void addRow(Archetype archetype) {
        String[] row = new String[3];
        row[0] = archetype.getName();
        row[1] = archetype.getTagsAsString();
        row[2] = archetype.getCategoriesAsString();
        addRow(row);
    }

    public Archetype getArchetype(int index) {
        if (archetypesArray == null || index > archetypesArray.length || index == -1) {
            return null;
        }
        return archetypesArray[index];
    }

    private void clear() {
        setNumRows(0);
        setRowCount(0);
    }
}