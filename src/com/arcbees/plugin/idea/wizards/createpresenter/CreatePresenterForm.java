package com.arcbees.plugin.idea.wizards.createpresenter;

import com.arcbees.plugin.idea.domain.PresenterConfigModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

public class CreatePresenterForm extends DialogWrapper {
  private final PresenterConfigModel presenterConfigModel;

  private JPanel contentPane;
  private JTextField name;
  private JTextField packageName;
  private JRadioButton btnNestedPresenter;
  private JRadioButton btnPresenterWidget;
  private JRadioButton btnPopupPresenter;
  private JPanel buttonPanel;
  private JPanel chooseTypesPanel;
  private JTabbedPane tabbedPanel;
  private JRadioButton btnRevealrootcontentevent;
  private JRadioButton btnRevealcontentevent;
  private JTextField contentSlot;
  private JButton btnSelectContentSlot;
  private JRadioButton btnRevealrootlayoutcontentevent;
  private JCheckBox btnIsAPlace;
  private JTextField nameToken;
  private JCheckBox btnIsCrawlable;
  private JCheckBox btnCodesplit;
  private JCheckBox btnAddUihandlers;
  private JCheckBox btnAddOnbind;
  private JCheckBox btnUseManualReveal;
  private JCheckBox btnPrepareFromRequest;
  private JCheckBox btnIsASingleton;
  private JCheckBox btnIsASingleton2;
  private JCheckBox btnOverrideDefaultPopup;
  private JCheckBox btnAddOnhide;
  private JCheckBox btnAddOnreset;
  private JCheckBox btnAddOnunbind;
  private JLabel lblQuerystring;

  public CreatePresenterForm(Project project) {
    super(project);
    init();

    setTitle("Create GWTP Presenter");

    presenterConfigModel = new PresenterConfigModel();

    //initHandlers();
  }

  private void initHandlers() {
    btnNestedPresenter.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    });

    btnPresenterWidget.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    });
    btnPopupPresenter.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    });

    tabbedPanel.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        int index = tabbedPanel.getSelectedIndex();
        setPresenterType(index);
        if (index == 0) {
          btnNestedPresenter.setSelected(true);
          btnPresenterWidget.setSelected(false);
          btnPopupPresenter.setSelected(false);
        } else if (index == 1) {
          btnNestedPresenter.setSelected(false);
          btnPresenterWidget.setSelected(true);
          btnPopupPresenter.setSelected(false);
        } else if (index == 2) {
          btnNestedPresenter.setSelected(false);
          btnPresenterWidget.setSelected(false);
          btnPopupPresenter.setSelected(true);
        }
      }
    });
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return contentPane;
  }

  private void createUIComponents() {
    // TODO: place custom component creation code here
  }

  private void setPresenterType(int index) {
    if (index == 0) {
      presenterConfigModel.setNestedPresenter(true);
      presenterConfigModel.setPresenterWidget(false);
      presenterConfigModel.setPopupPresenter(false);

      if (lblQuerystring != null) {
        lblQuerystring.setVisible(true);
      }
      if (btnPrepareFromRequest != null) {
        btnPrepareFromRequest.setVisible(true);
      }
    } else if (index == 1) {
      presenterConfigModel.setNestedPresenter(false);
      presenterConfigModel.setPresenterWidget(true);
      presenterConfigModel.setPopupPresenter(false);

      if (lblQuerystring != null) {
        lblQuerystring.setVisible(false);
      }
      if (btnPrepareFromRequest != null) {
        btnPrepareFromRequest.setVisible(false);
      }
    } else if (index == 2) {
      presenterConfigModel.setNestedPresenter(false);
      presenterConfigModel.setPresenterWidget(false);
      presenterConfigModel.setPopupPresenter(true);

      if (lblQuerystring != null) {
        lblQuerystring.setVisible(false);
      }
      if (btnPrepareFromRequest != null) {
        btnPrepareFromRequest.setVisible(false);
      }
    }
  }
}
