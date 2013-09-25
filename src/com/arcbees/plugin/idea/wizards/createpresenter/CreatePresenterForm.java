package com.arcbees.plugin.idea.wizards.createpresenter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.arcbees.plugin.idea.domain.PresenterConfigModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

public class CreatePresenterForm extends DialogWrapper {
  private final PresenterConfigModel presenterConfigModel;

  private JPanel contentPane;
  private JTextField name;
  private JTextField packageName;
  private JRadioButton radioNestedPresenter;
  private JRadioButton radioPresenterWidget;
  private JRadioButton radioPopupPresenter;
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

    // model object to transfer vars to the ide-templates
    presenterConfigModel = new PresenterConfigModel();

    initHandlers();
    setDefaults();
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return contentPane;
  }

  private void initHandlers() {
    initRadioHandlers();
    initTabFoldersHandlers();
    initContentEventHandlers();
    initPlaceHandlers();
  }

  private void setDefaults() {
    nameToken.setEnabled(false);
    btnIsCrawlable.setEnabled(false);
    nameToken.grabFocus();
  }

  private void initContentEventHandlers() {
    btnRevealrootcontentevent.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        btnRevealcontentevent.setSelected(false);
        btnRevealrootcontentevent.setSelected(true);
        btnRevealrootlayoutcontentevent.setSelected(false);
        contentSlot.setEnabled(false);
        btnSelectContentSlot.setEnabled(false);

        presenterConfigModel.setRevealInRoot(true);
        presenterConfigModel.setRevealInRootLayout(false);
        presenterConfigModel.setRevealInSlot(false);
      }
    });

    btnRevealrootlayoutcontentevent.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        btnRevealcontentevent.setSelected(false);
        btnRevealrootcontentevent.setSelected(false);
        btnRevealrootlayoutcontentevent.setSelected(true);
        contentSlot.setEnabled(false);
        btnSelectContentSlot.setEnabled(false);

        presenterConfigModel.setRevealInRoot(false);
        presenterConfigModel.setRevealInRootLayout(true);
        presenterConfigModel.setRevealInSlot(false);
      }
    });

    btnRevealcontentevent.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        btnRevealcontentevent.setSelected(true);
        btnRevealrootcontentevent.setSelected(false);
        btnRevealrootlayoutcontentevent.setSelected(false);
        contentSlot.setEnabled(true);
        btnSelectContentSlot.setEnabled(true);

        presenterConfigModel.setRevealInRoot(false);
        presenterConfigModel.setRevealInRootLayout(false);
        presenterConfigModel.setRevealInSlot(true);
      }
    });
  }

  private void initPlaceHandlers() {
    btnIsAPlace.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = btnIsAPlace.isSelected();
        if (selected) {
          nameToken.setEnabled(true);
          btnIsCrawlable.setEnabled(true);
          nameToken.grabFocus();
        } else {
          nameToken.setEnabled(false);
          btnIsCrawlable.setEnabled(false);
          nameToken.setText("");
        }
      }
    });
  }

  private void initRadioHandlers() {
    radioNestedPresenter.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = radioNestedPresenter.isSelected();
        if (selected) {
          tabbedPanel.setSelectedIndex(0);
          setPresenterType(0);
        }
      }
    });

    radioPresenterWidget.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = radioPresenterWidget.isSelected();
        if (selected) {
          tabbedPanel.setSelectedIndex(1);
          setPresenterType(1);
        }
      }
    });

    radioPopupPresenter.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = radioPopupPresenter.isSelected();
        if (selected) {
          tabbedPanel.setSelectedIndex(2);
          setPresenterType(2);
        }
      }
    });
  }

  private void initTabFoldersHandlers() {
    tabbedPanel.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        int selectedIndex = tabbedPanel.getSelectedIndex();

        setPresenterType(selectedIndex);

        if (selectedIndex == 0) {
          radioNestedPresenter.setSelected(true);
          radioPresenterWidget.setSelected(false);
          radioPopupPresenter.setSelected(false);
        } else if (selectedIndex == 1) {
          radioNestedPresenter.setSelected(false);
          radioPresenterWidget.setSelected(true);
          radioPopupPresenter.setSelected(false);
        } else if (selectedIndex == 2) {
          radioNestedPresenter.setSelected(false);
          radioPresenterWidget.setSelected(false);
          radioPopupPresenter.setSelected(true);
        }
      }
    });
  }

  private void setPresenterType(int selectedIndex) {
    if (selectedIndex == 0) {
      presenterConfigModel.setNestedPresenter(true);
      presenterConfigModel.setPresenterWidget(false);
      presenterConfigModel.setPopupPresenter(false);

      if (lblQuerystring != null) {
        lblQuerystring.setVisible(true);
      }

      if (btnPrepareFromRequest != null) {
        btnPrepareFromRequest.setVisible(true);
      }
    } else if (selectedIndex == 1) {
      presenterConfigModel.setNestedPresenter(false);
      presenterConfigModel.setPresenterWidget(true);
      presenterConfigModel.setPopupPresenter(false);

      if (lblQuerystring != null) {
        lblQuerystring.setVisible(false);
      }

      if (btnPrepareFromRequest != null) {
        btnPrepareFromRequest.setVisible(false);
      }
    } else if (selectedIndex == 2) {
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
