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


package com.arcbees.plugin.idea.wizards.createpresenter;

import com.arcbees.plugin.idea.domain.CreatedPsiClass;
import com.arcbees.plugin.idea.domain.PresenterConfigModel;
import com.arcbees.plugin.idea.icons.PluginIcons;
import com.arcbees.plugin.idea.utils.PackageHierarchy;
import com.arcbees.plugin.idea.utils.PackageHierarchyElement;
import com.arcbees.plugin.idea.utils.PackageUtilExt;
import com.arcbees.plugin.template.create.place.CreateNameTokens;
import com.arcbees.plugin.template.create.presenter.CreateNestedPresenter;
import com.arcbees.plugin.template.create.presenter.CreatePopupPresenter;
import com.arcbees.plugin.template.create.presenter.CreatePresenterWidget;
import com.arcbees.plugin.template.domain.place.CreatedNameTokens;
import com.arcbees.plugin.template.domain.place.NameToken;
import com.arcbees.plugin.template.domain.place.NameTokenOptions;
import com.arcbees.plugin.template.domain.presenter.CreatedNestedPresenter;
import com.arcbees.plugin.template.domain.presenter.CreatedPopupPresenter;
import com.arcbees.plugin.template.domain.presenter.CreatedPresenterWidget;
import com.arcbees.plugin.template.domain.presenter.NestedPresenterOptions;
import com.arcbees.plugin.template.domain.presenter.PopupPresenterOptions;
import com.arcbees.plugin.template.domain.presenter.PresenterOptions;
import com.arcbees.plugin.template.domain.presenter.PresenterWidgetOptions;
import com.arcbees.plugin.template.domain.presenter.RenderedTemplate;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CreatePresenterAction extends AnAction {
    public final static Logger logger = Logger.getLogger(CreatePresenterAction.class.getName());

    private PresenterConfigModel presenterConfigModel;
    private Project project;
    private PackageHierarchy packageHierarchy;
    private PsiPackage createdNameTokensPackage;

    private CreatedNameTokens createdNameTokenTemplates;
    private CreatedNestedPresenter createdNestedPresenterTemplates;
    private CreatedPopupPresenter createdPopupPresenterTemplates;
    private CreatedPresenterWidget createdPresenterWidgetTemplates;

    public CreatePresenterAction() {
        super("Create Presenter", "Create GWTP Presenter", PluginIcons.GWTP_ICON_16x16);
    }

    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        presenterConfigModel = new PresenterConfigModel(project);

        CreatePresenterForm dialog = new CreatePresenterForm(presenterConfigModel, e);
        if (!dialog.showAndGet()) {
            return;
        }

        // update the model with the input data from the form
        dialog.getData(presenterConfigModel);

        run();
    }

    private void run() {
        logger.info("Creating presenter started...");

        createPackageHierachyIndex();

        createNameTokensPackage();
        try {
            createNametokensFile();
        } catch (Exception e) {
            // TODO
            //warn("Could not create or find the name tokens file 'NameTokens.java': Error: " + e.toString());
            e.printStackTrace();
            return;
        }

        try {
            fetchTemplatesNameTokens();
        } catch (Exception e) {
            // TODO
            //warn("Could not fetch NameTokens templates: Error: " + e.toString());
            e.printStackTrace();
            return;
        }

        try {
            fetchPresenterTemplates();
        } catch (Exception e) {
            // TODO
            //warn("Could not fetch the ntested presenter templates: Error: " + e.toString());
            e.printStackTrace();
            return;
        }

        createNameTokensFieldAndMethods();
//        createPresenterPackage();
//        createPresenterModule();
//        createPresenterModuleLinkForGin();
//        createPresenter();
//        createPresenterUiHandlers();
//        createPresenterView();
//        createPresenterViewUi();
//        createLinkPresenterWidgetToPanel();

        // TODO focus on new presenter package and open it up

        logger.info("...Creating presenter finished.");
    }

    /**
     * create name tokens class, if it doesn't exist
     */
    private void createNameTokensFieldAndMethods() {
        if (!presenterConfigModel.isUsePlace()) {
            return;
        }
        PsiClass unitNameTokens = presenterConfigModel.getNameTokenPsiClass();
        if (unitNameTokens == null) {
            logger.info("createNameTokensFieldAndMethods: skipping creating nametokens methods.");
            return;
        }

        addMethodsToNameTokens(unitNameTokens);
    }

    private void addMethodsToNameTokens(PsiClass nameTokensPsiClass)  {
        // find existing method
        PsiMethod[] existingMethods = nameTokensPsiClass.getMethods();
        for (PsiMethod psiMethod : existingMethods) {
            // does the method already exist
            if (psiMethod.getName().equals(presenterConfigModel.getNameTokenMethodName())) {
                // TODO
                //warn("FYI: the method in nameTokens already exists." + method.toString());
                return;
            }
        }

        // get items from template
        List<String> fields = createdNameTokenTemplates.getFields();
        List<String> methods = createdNameTokenTemplates.getMethods();
        String fieldSource = fields.get(0);
        String methodSource = methods.get(0);

        // add contents to class
        PsiElementFactory elementFactory = PsiElementFactory.SERVICE.getInstance(project);
        elementFactory.createFieldFromText(fieldSource, nameTokensPsiClass);
        elementFactory.createMethodFromText(methodSource, nameTokensPsiClass);
    }

    private void fetchPresenterTemplates() throws Exception {
        PresenterOptions presenterOptions = new PresenterOptions();
        presenterOptions.setPackageName(presenterConfigModel.getSelectedPackageAndNameAsSubPackage());
        presenterOptions.setName(presenterConfigModel.getName());
        presenterOptions.setOnbind(presenterConfigModel.isUseAddOnbind());
        presenterOptions.setOnhide(presenterConfigModel.isUseAddOnhide());
        presenterOptions.setOnreset(presenterConfigModel.isUseAddOnreset());
        presenterOptions.setOnunbind(presenterConfigModel.isUseAddOnunbind());
        presenterOptions.setManualreveal(presenterConfigModel.getUseManualReveal());
        presenterOptions.setPrepareFromRequest(presenterConfigModel.getUsePrepareFromRequest());
        presenterOptions.setUihandlers(presenterConfigModel.isUseAddUihandlers());

        // TODO future
        //presenterOptions.setGatekeeper(presenterConfigModel.getGatekeeper());

        if (presenterConfigModel.getNestedPresenter()) {
            fetchNestedTemplate(presenterOptions);
        } else if (presenterConfigModel.getPresenterWidget()) {
            fetchPresenterWidgetTemplate(presenterOptions);
        } else if (presenterConfigModel.getPopupPresenter()) {
            fetchPopupPresenterTemplate(presenterOptions);
        }
    }

    private void fetchPopupPresenterTemplate(PresenterOptions presenterOptions) throws Exception {
        PopupPresenterOptions presenterWidgetOptions = new PopupPresenterOptions();
        presenterWidgetOptions.setSingleton(presenterConfigModel.isUseSingleton());
        presenterWidgetOptions.setCustom(presenterConfigModel.isUseOverrideDefaultPopup());

        try {
            createdPopupPresenterTemplates = CreatePopupPresenter.run(presenterOptions, presenterWidgetOptions, true);
        } catch (Exception e) {
            throw e;
        }
    }

    private void fetchPresenterWidgetTemplate(PresenterOptions presenterOptions) throws Exception {
        PresenterWidgetOptions presenterWidgetOptions = new PresenterWidgetOptions();
        presenterWidgetOptions.setSingleton(presenterConfigModel.isUseSingleton());

        try {
            createdPresenterWidgetTemplates = CreatePresenterWidget.run(presenterOptions, presenterWidgetOptions, true);
        } catch (Exception e) {
            throw e;
        }
    }

    private void fetchNestedTemplate(PresenterOptions presenterOptions) throws Exception {
        NestedPresenterOptions nestedPresenterOptions = new NestedPresenterOptions();
        nestedPresenterOptions.setPlace(presenterConfigModel.isUsePlace());
        nestedPresenterOptions.setNameToken(presenterConfigModel.getNameToken());
        nestedPresenterOptions.setCrawlable(presenterConfigModel.isUseCrawlable());
        nestedPresenterOptions.setCodeSplit(presenterConfigModel.isUseCodesplit());
        nestedPresenterOptions.setNameToken(presenterConfigModel.getNameTokenWithClass());
        nestedPresenterOptions.setNameTokenImport(presenterConfigModel.getNameTokenUnitImport());
        nestedPresenterOptions.setContentSlotImport(presenterConfigModel.getContentSlotImport());

        if (presenterConfigModel.getRevealInRoot()) {
            nestedPresenterOptions.setRevealType("Root");
        } else if (presenterConfigModel.getRevealInRootLayout()) {
            nestedPresenterOptions.setRevealType("RootLayout");
        } else if (presenterConfigModel.getPopupPresenter()) {
            nestedPresenterOptions.setRevealType("RootPopup");
        } else if (presenterConfigModel.getRevealInSlot()) {
            nestedPresenterOptions.setRevealType(presenterConfigModel.getContentSlot());
        }

        try {
            createdNestedPresenterTemplates = CreateNestedPresenter.run(presenterOptions, nestedPresenterOptions, true);
        } catch (Exception e) {
            throw e;
        }
    }

    private void fetchTemplatesNameTokens() throws Exception {
        if (!presenterConfigModel.isUsePlace()) {
            return;
        }

        NameToken token = new NameToken();
        token.setCrawlable(presenterConfigModel.isUseCrawlable());
        token.setToken(presenterConfigModel.getNameToken());

        List<NameToken> nameTokens = new ArrayList<NameToken>();
        nameTokens.add(token);

        NameTokenOptions nameTokenOptions = new NameTokenOptions();
        nameTokenOptions.setPackageName(createdNameTokensPackage.getQualifiedName());
        nameTokenOptions.setNameTokens(nameTokens);

        boolean processFileOnly = false;
        try {
            createdNameTokenTemplates = CreateNameTokens.run(nameTokenOptions, true, processFileOnly);
        } catch (Exception e) {
            throw e;
        }
    }

    private void createPackageHierachyIndex() {
        packageHierarchy = new PackageHierarchy(presenterConfigModel);
        packageHierarchy.run();
    }

    private void createNameTokensPackage() {
        if (!presenterConfigModel.isUsePlace()) {
            return;
        }

        PsiPackage selectedPackage = presenterConfigModel.getSelectedPackage();
        String selectedPackageString = selectedPackage.getQualifiedName();
        PackageHierarchyElement clientPackage = packageHierarchy.findParentClient(selectedPackageString);
        String clientPackageString = clientPackage.getPackageFragment().getQualifiedName();
        PsiDirectory baseDir = PsiManager.getInstance(presenterConfigModel.getProject()).findDirectory(clientPackage.getRoot());

        // name tokens package ...client.place.NameTokens
        clientPackageString += ".place";

        PackageHierarchyElement nameTokensPackageExists = packageHierarchy.find(clientPackageString);

        if (nameTokensPackageExists != null && nameTokensPackageExists.getPackageFragment() != null) {
            createdNameTokensPackage = nameTokensPackageExists.getPackageFragment();
        } else {
            createdNameTokensPackage = createPackage(baseDir, clientPackageString);
        }
    }

    private PsiPackage createPackage(PsiDirectory baseDir, String packageName) {
        Module module = presenterConfigModel.getModule();
        PsiDirectory psiDir = PackageUtilExt.findOrCreateDirectoryForPackage(module, packageName, baseDir, false, false);
        return JavaDirectoryService.getInstance().getPackage(psiDir);
    }

    private void createNametokensFile() throws Exception {
        if (!presenterConfigModel.isUsePlace()) {
            return;
        }

        // look for existing name tokens first.
        List<PsiClass> foundNameTokens = packageHierarchy.findClassName("NameTokens");

        PsiClass unitNameTokens = null;
        if (foundNameTokens != null && foundNameTokens.size() > 0) {
            unitNameTokens = foundNameTokens.get(0);
        } else {
            unitNameTokens = createNewNameTokensFile();
        }

        if (unitNameTokens == null) {
            // TODO
            //warn("Could not create NameTokens.java");
            return;
        }

        // used for import string
        presenterConfigModel.setNameTokenPsiClass(unitNameTokens);
    }

    private PsiClass createNewNameTokensFile() throws Exception {
        boolean processFileOnly = true;
        NameTokenOptions nameTokenOptions = new NameTokenOptions();
        nameTokenOptions.setPackageName(createdNameTokensPackage.getQualifiedName());
        CreatedNameTokens createdNameToken;
        try {
            createdNameToken = CreateNameTokens.run(nameTokenOptions, true, processFileOnly);
        } catch (Exception e) {
            throw e;
        }

        RenderedTemplate rendered = createdNameToken.getNameTokensFile();
        String nameFile = rendered.getNameAndNoExt();
        String contents = rendered.getContents();

        final PsiDirectory[] createdNameTokensPackageDirectories = createdNameTokensPackage.getDirectories();
        final PsiFile element = PsiFileFactory.getInstance(project).createFileFromText(nameFile, JavaFileType.INSTANCE, contents);

        final CreatedPsiClass createdPsiClass = new CreatedPsiClass();
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                PsiElement createdNameTokensClass = createdNameTokensPackageDirectories[0].add(element);
                PsiJavaFile javaFile = (PsiJavaFile) createdNameTokensClass;
                PsiClass[] clazzes = javaFile.getClasses();

                createdPsiClass.setPsiClass(clazzes[0]);
            }
        });

        return createdPsiClass.getPsiClass();
    }

}
