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

package com.arcbees.plugin.idea.dialogs;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedMembersSearch;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ContentSlotDialog extends DialogWrapper {
    // project
    private final Project project;
    private final AnActionEvent sourceEvent;

    // panels
    private JPanel contentPanel;
    private JList contentSlotsList;

    // vars
    private String selection;
    private Map<String, PsiMember> slots;

    public ContentSlotDialog(@Nullable Project project, boolean canBeParent, AnActionEvent sourceEvent) {
        super(project, canBeParent);

        this.project = project;
        this.sourceEvent = sourceEvent;

        init();
        setTitle("Select a Content Slot");
        setSize(500, 600);
        findSlots();

        contentSlotsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selected = contentSlotsList.getSelectedIndex();
                ListModel list = contentSlotsList.getModel();
                selection = (String) list.getElementAt(selected);
            }
        });
    }

    public String getContentSlot() {
        return selection;
    }

    public PsiClass getContentSlotPsiClass() {
        PsiClass psiClass = null;
        if (selection != null) {
            PsiMember psiMember = slots.get(selection);
            psiClass = psiMember.getContainingClass();
        }
        return psiClass;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

    private void findSlots() {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        PsiClass psiClass = JavaPsiFacade.getInstance(project)
                .findClass("com.gwtplatform.mvp.client.annotations.ContentSlot", scope);

        slots = new HashMap<String, PsiMember>();
        Query<PsiMember> query = AnnotatedMembersSearch.search(psiClass, GlobalSearchScope.allScope(project));
        query.forEach(new Processor<PsiMember>() {
            public boolean process(PsiMember psiMember) {
                slots.put(getSlot(psiMember), psiMember);

                return true;
            }
        });

        String[] listData = new String[slots.size()];
        Set<String> set = slots.keySet();
        int i=0;
        for (String item : set) {
            listData[i] = item;
            i++;
        }

        contentSlotsList.setListData(listData);
    }

    private String getSlot(PsiMember psiMember) {
        PsiClass container = psiMember.getContainingClass();
        String classname = container.getName();

        return classname + "." + psiMember.getName();
    }
}
