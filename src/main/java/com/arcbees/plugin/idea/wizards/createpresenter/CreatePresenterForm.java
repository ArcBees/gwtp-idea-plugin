package com.arcbees.plugin.idea.wizards.createpresenter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

import org.jetbrains.annotations.Nullable;

import com.arcbees.plugin.idea.dialogs.ContentSlotDialog;
import com.arcbees.plugin.idea.domain.PresenterConfigModel;
import com.arcbees.plugin.idea.domain.PresenterType;
import com.arcbees.plugin.idea.domain.RevealLocation;
import com.arcbees.plugin.idea.utils.PackageUtilExt;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;

public class CreatePresenterForm extends DialogWrapper {
    private final PresenterConfigModel presenterConfigModel;
    private final AnActionEvent sourceEvent;

    private JPanel contentPane;
    private JTextField name;
    private JTextField packageName;
    private JRadioButton radioNestedPresenter;
    private JRadioButton radioPresenterWidget;
    private JRadioButton radioPopupPresenter;
    private JPanel chooseTypesPanel;
    private JTabbedPane tabbedPanel;
    private JRadioButton radioContentRoot;
    private JRadioButton radioContentSlot;
    private JTextField contentSlot;
    private JButton selectContentSlot;
    private JRadioButton radioContentRootLayout;
    private JCheckBox usePlace;
    private JTextField nameToken;
    private JCheckBox useCrawlable;
    private JCheckBox useCodesplit;
    private JCheckBox useAddUihandlers;
    private JCheckBox useManualReveal;
    private JCheckBox useSingleton;
    private JCheckBox useSingleton2;
    private JCheckBox useOverrideDefaultPopup;

    public CreatePresenterForm(PresenterConfigModel presenterConfigModel, AnActionEvent sourceEvent) {
        super(presenterConfigModel.getProject());
        init();

        this.presenterConfigModel = presenterConfigModel;
        this.sourceEvent = sourceEvent;

        setTitle("Create GWTP Presenter");
        hideTabsHeader();

        initHandlers();
        setDefaults();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return name;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (!packageName.getText().contains(".client")) {
            return new ValidationInfo("Select a package that has .client in it.", packageName);
        }

        if (StringUtil.isEmptyOrSpaces(name.getText())) {
            return new ValidationInfo("Presenter name can't be empty", name);
        }

        // TODO add more complex validations

        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    private void hideTabsHeader() {
        tabbedPanel.setUI(new MetalTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                return 0;
            }

            @Override
            protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
            }
        });
    }

    private void initHandlers() {
        initRadioHandlers();
        initContentEventHandlers();
        initPlaceHandlers();
        initButtonHandlers();
    }

    private void setDefaults() {
        nameToken.setEnabled(false);
        useCrawlable.setEnabled(false);
        nameToken.grabFocus();

        PsiElement psiElement = sourceEvent.getData(LangDataKeys.PSI_ELEMENT);
        PsiPackage selectedPackageRoot =
                PackageUtilExt.getSelectedPackageRoot(presenterConfigModel.getProject(), psiElement);
        packageName.setText(selectedPackageRoot.getQualifiedName());
    }

    private void initButtonHandlers() {
        selectContentSlot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showContentSlotDialog();
            }
        });
    }

    private void initContentEventHandlers() {
        radioContentRoot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentSlot.setEnabled(false);
                selectContentSlot.setEnabled(false);
                presenterConfigModel.setRevealLocation(RevealLocation.ROOT);
            }
        });

        radioContentRootLayout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentSlot.setEnabled(false);
                selectContentSlot.setEnabled(false);
                presenterConfigModel.setRevealLocation(RevealLocation.ROOT_LAYOUT);
            }
        });

        radioContentSlot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentSlot.setEnabled(true);
                selectContentSlot.setEnabled(true);
                presenterConfigModel.setRevealLocation(RevealLocation.SLOT);
            }
        });
    }

    private void initPlaceHandlers() {
        usePlace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = usePlace.isSelected();

                nameToken.setEnabled(selected);
                useCrawlable.setEnabled(selected);

                if (selected) {
                    nameToken.grabFocus();
                } else {
                    nameToken.setText("");
                }
            }
        });
    }

    private void initRadioHandlers() {
        radioNestedPresenter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (radioNestedPresenter.isSelected()) {
                    tabbedPanel.setSelectedIndex(0);

                    presenterConfigModel.setSelectedPresenter(PresenterType.NESTED_PRESENTER);
                }
            }
        });

        radioPresenterWidget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (radioPresenterWidget.isSelected()) {
                    tabbedPanel.setSelectedIndex(1);

                    presenterConfigModel.setSelectedPresenter(PresenterType.PRESENTER_WIDGET);
                }
            }
        });

        radioPopupPresenter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (radioPopupPresenter.isSelected()) {
                    tabbedPanel.setSelectedIndex(2);

                    presenterConfigModel.setSelectedPresenter(PresenterType.POPUP_PRESENTER);
                }
            }
        });
    }

    private void showContentSlotDialog() {
        Module module = presenterConfigModel.getModule();

        ContentSlotDialog dialog = new ContentSlotDialog(module, true, sourceEvent);
        dialog.show();

        String contentSlotSelection = dialog.getContentSlot();
        contentSlot.setText(contentSlotSelection);

        presenterConfigModel.setContentSlot(contentSlotSelection);
        presenterConfigModel.setContentSlotClass(dialog.getContentSlotPsiClass());
    }

    public void setData(PresenterConfigModel data) {
        name.setText(data.getName());
        packageName.setText(data.getPackageName());
        contentSlot.setText(data.getContentSlot());
        usePlace.setSelected(data.isUsePlace());
        nameToken.setText(data.getNameToken());
        useCrawlable.setSelected(data.isUseCrawlable());
        useCodesplit.setSelected(data.isUseCodesplit());
        useSingleton.setSelected(data.isUseSingleton());
        useSingleton2.setSelected(data.isUseSingleton());
        useOverrideDefaultPopup.setSelected(data.isUseOverrideDefaultPopup());
        useAddUihandlers.setSelected(data.isUseAddUihandlers());
        useManualReveal.setSelected(data.getUseManualReveal());
    }

    public void getData(PresenterConfigModel data) {
        data.setName(name.getText());
        String packageName = this.packageName.getText();
        data.setPackageName(packageName);
        data.setContentSlot(contentSlot.getText());
        data.setUsePlace(usePlace.isSelected());
        data.setNameToken(nameToken.getText());
        data.setUseCrawlable(useCrawlable.isSelected());
        data.setUseCodesplit(useCodesplit.isSelected());
        data.setUseSingleton(useSingleton.isSelected());
        data.setUseSingleton2(useSingleton2.isSelected());
        data.setUseOverrideDefaultPopup(useOverrideDefaultPopup.isSelected());
        data.setUseAddUihandlers(useAddUihandlers.isSelected());
        data.setUseManualReveal(useManualReveal.isSelected());
    }

    public boolean isModified(PresenterConfigModel data) {
        if (name.getText() != null ? !name.getText().equals(data.getName()) : data.getName() != null) {
            return true;
        }
        if (packageName.getText() != null ? !packageName.getText().equals(
                data.getPackageName()) : data.getPackageName() != null) {
            return true;
        }
        if (contentSlot.getText() != null ? !contentSlot.getText().equals(
                data.getContentSlot()) : data.getContentSlot() != null) {
            return true;
        }
        if (usePlace.isSelected() != data.isUsePlace()) {
            return true;
        }
        if (nameToken.getText() != null ? !nameToken.getText().equals(
                data.getNameToken()) : data.getNameToken() != null) {
            return true;
        }
        if (useCrawlable.isSelected() != data.isUseCrawlable()) {
            return true;
        }
        if (useCodesplit.isSelected() != data.isUseCodesplit()) {
            return true;
        }
        if (useSingleton.isSelected() != data.isUseSingleton()) {
            return true;
        }
        if (useSingleton2.isSelected() != data.isUseSingleton()) {
            return true;
        }
        if (useOverrideDefaultPopup.isSelected() != data.isUseOverrideDefaultPopup()) {
            return true;
        }
        if (useAddUihandlers.isSelected() != data.isUseAddUihandlers()) {
            return true;
        }
        if (useManualReveal.isSelected() != data.getUseManualReveal()) {
            return true;
        }
        return false;
    }
}
