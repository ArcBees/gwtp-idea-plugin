package com.arcbees.plugin.idea.wizards.createpresenter;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;

public class CreatePresenterForm extends DialogWrapper {
  private JPanel contentPane;

  public CreatePresenterForm(Project project) {
    super(project);
    init();

    setTitle("Create GWTP Presenter");
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return contentPane;
  }
}
