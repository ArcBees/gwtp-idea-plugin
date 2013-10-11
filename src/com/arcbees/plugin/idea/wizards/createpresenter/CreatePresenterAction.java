package com.arcbees.plugin.idea.wizards.createpresenter;

import com.arcbees.plugin.idea.icons.PluginIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class CreatePresenterAction extends AnAction {
    public CreatePresenterAction() {
        super("Create Presenter", "Create GWTP Presenter", PluginIcons.GWTP_ICON_16x16);
    }

    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        // 15:20 part 1 youtube http://www.youtube.com/watch?v=-ZmQD6Fr6KE&feature=player_detailpage#t=935

        CreatePresenterForm dialog = new CreatePresenterForm(project, e);
        dialog.show();


        // TODO ...
    }
}
