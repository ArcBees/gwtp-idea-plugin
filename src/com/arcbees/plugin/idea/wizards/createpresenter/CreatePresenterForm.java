package com.arcbees.plugin.idea.wizards.createpresenter;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;

public class CreatePresenterForm extends DialogWrapper {
  private JPanel contentPane;
  private JTextField name;
  private JTextField packageName;
  private JRadioButton btnNestedPresenter;
  private JRadioButton btnPresenterWidget;
  private JRadioButton btnPopupPresenter;
  private JPanel buttonPanel;
  private JPanel chooseTypesPanel;
  private JTabbedPane tabbedPane1;
  private JRadioButton radioRevealRoot;
  private JRadioButton radioRevealSlot;
  private JTextField contentSlot;
  private JButton btnSelectContentSlot;
  private JRadioButton radioRevealRootLayout;
  private JCheckBox isAPlabtnIsAPlace;
  private JTextField nameToken;
  private JCheckBox isCrawlableCheckBox;
  private JCheckBox codeSplitCheckBox;
  private JCheckBox addUiHandlersCheckBox;
  private JCheckBox addOnBindCheckBox;
  private JCheckBox useManualRevealCheckBox;
  private JCheckBox usePrepareFromRequestCheckBox;

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
