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
import java.util.ArrayList;

public class ContentSlotDialog extends DialogWrapper {

    private final Project project;
    private final AnActionEvent sourceEvent;

    private JPanel contentPanel;
    private JList contentSlotsList;

    private String selection;
    private ArrayList<String> slots;

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
                int selected = e.getFirstIndex();
                selection = slots.get(selected);
            }
        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

    private void findSlots() {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass("com.gwtplatform.mvp.client.annotations.ContentSlot", scope);

        slots = new ArrayList<String>();
        Query<PsiMember> query = AnnotatedMembersSearch.search(psiClass, GlobalSearchScope.allScope(project));
        query.forEach(new Processor<PsiMember>() {
            public boolean process(PsiMember psiMember) {
                PsiClass container = psiMember.getContainingClass();
                String classname = container.getName();
                slots.add(classname + "." + psiMember.getName());
                return true;
            }
        });

        String[] listData = new String[slots.size()];
        slots.toArray(listData);
        contentSlotsList.setListData(listData);
    }

    public String getContentSlot() {
        return selection;
    }
}
