package com.arcbees.plugin.idea.wizards.createpresenter;

import com.arcbees.plugin.idea.icons.PluginIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import java.awt.*;

public class CreatePresenterAction extends AnAction {
  public CreatePresenterAction() {
    super("Create Presenter", "Create GWTP Presenter", PluginIcons.GWTP_ICON_16x16);
  }

  public void actionPerformed(AnActionEvent e) {
    Project project = e.getProject();

    CreatePresenterForm dialog = new CreatePresenterForm(project);
    dialog.show();

    // TODO ...
  }
}
