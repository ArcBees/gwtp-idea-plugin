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
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CreatePresenterAction extends AnAction {
    public final static Logger logger = Logger.getLogger(CreatePresenterAction.class.getName());

    // project model settings
    private PresenterConfigModel presenterConfigModel;
    private Project project;
    private PackageHierarchy packageHierarchy;

    // created elements
    private PsiPackage createdNameTokensPackage;
    private PsiPackage createdPresenterPackage;
    private PsiClass createdPresenterPsiClass;
    private PsiClass createdModulePsiClass;

    // templates
    private CreatedNameTokens createdNameTokenTemplates;
    private CreatedNestedPresenter createdNestedPresenterTemplates;
    private CreatedPopupPresenter createdPopupPresenterTemplates;
    private CreatedPresenterWidget createdPresenterWidgetTemplates;

    private boolean failedStep;

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

    /**
     * TODO run async command
     * TODO progress monitor spinning
     */
    private void run() {
        logger.info("Creating presenter started...");

        createPackageHierachyIndex();
        createNameTokensPackage();

        createNametokensClassTask();
        if (failedStep) {
            return;
        }

        fetchTemplatesNameTokensTask();
        if (failedStep) {
            return;
        }

        fetchPresenterTemplatesTask();
        if (failedStep) {
            return;
        }

        createNameTokensFieldAndMethods();
        createPresenterPackage();
        createPresenterModule();
        createPresenter();
        createPresenterUiHandlers();
        createPresenterView();
        createPresenterViewUi();
        createPresenterModuleLinkForGin();

        logger.info("...Creating presenter finished.");
    }

    private void fetchPresenterTemplatesTask() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            fetchPresenterTemplates();
                        } catch (Exception e) {
                            // TODO
                            //warn("Could not fetch the nested presenter templates: Error: " + e.toString());
                            failedStep = true;
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void fetchTemplatesNameTokensTask() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            fetchTemplatesNameTokens();
                        } catch (Exception e) {
                            // TODO
                            //warn("Could not fetch NameTokens templates: Error: " + e.toString());
                            failedStep = true;
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void createNametokensClassTask() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            createNametokensClass();
                        } catch (Exception e) {
                            // TODO
                            //warn("Could not create or find the name tokens file 'NameTokens.java': Error: " + e.toString());
                            failedStep = true;
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * TODO extraction possibly down the road.
     */
    private void createPresenterModuleLinkForGin() {
        // 1. first search parent
        PsiClass unit = packageHierarchy.findInterfaceTypeInParentPackage(
                presenterConfigModel.getSelectedPackageRoot(), "GinModule");

        // 2. next check if the parent is client and if so, scan all packages for ginModule
        String selectedPackageElementName = presenterConfigModel.getSelectedPackageRoot().getQualifiedName();
        if (unit == null && packageHierarchy.isParentTheClientPackage(selectedPackageElementName)) {
            // first check for a gin package with GinModule
            PackageHierarchyElement hierarchyElement = packageHierarchy.findParentClientAndAddPackage(
                    selectedPackageElementName, "gin");
            if (hierarchyElement != null) {
                PsiPackage clienPackage = hierarchyElement.getPackageFragment();
                unit = packageHierarchy.findInterfaceTypeInParentPackage(clienPackage, "GinModule");
            }

            // If no gin package check for any existence of a GinModule
            // TODO could make this smarter in the future, this is a last resort, to install it somewhere.
            if (unit == null) {
                unit = packageHierarchy.findFirstInterfaceType("GinModule");
                logger.info("Warning: This didn't find a ideal place to put the gin install for the new presenter module");
            }
        }

        // 3. walk up next parent for and look for gin module
        if (unit == null) {
            if (selectedPackageElementName.contains("client")) {
                PackageHierarchyElement hierarchyElement = packageHierarchy.findParent(selectedPackageElementName);

                if (hierarchyElement.getPackageFragment() != null) {
                    PsiPackage parentParentPackage = hierarchyElement.getPackageFragment();
                    unit = packageHierarchy.findInterfaceTypeInParentPackage(parentParentPackage, "GinModule");
                }
            }
        }

        // 4. search all filter by GinModule interface, this would be easy
        // If no gin package check for any existence of a GinModule
        // TODO could make this smarter in the future, this is a last resort, to install it somewhere.
        if (unit == null) {
            unit = packageHierarchy.findFirstInterfaceType("GinModule");
            logger.info("Warning: This didn't find a ideal place to put the gin install for the new presenter module");
        }

        // (could do this next for ease)
        if (unit != null) {
            createPresenterGinlink(unit);
        } else {
            logger.warning("Error: Wasn't able to install Module");
            // TODO
            //warn("Could not create install module.");
        }
    }

    /**
     * TODO extract this possibly, but I think I'll wait till I get into slots before I do it see what is common.
     */
    private void createPresenterGinlink(final PsiClass parentModulePsiClass) {
        // find the configure method
        final PsiMethod method = findMethod(parentModulePsiClass, "configure");

        if (method == null) {
            // TODO
            //warn("Wasn't able to findMethod Configure in unit: " + unit.getElementName());
            logger.severe("createPresenterGinLink() unit did not have configure implementation.");
            return;
        }

        PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);

        // created module import
        final PsiImportStatement importStatement = factory.createImportStatement(createdModulePsiClass);

        // create configure method install(new Module());
        String moduleName = createdModulePsiClass.getName() + "()";
        String installModuleStatement = "install(new " + moduleName + ");";

        // module statement for configure method
        final PsiStatement installModuleStatementElement = factory.createStatementFromText(installModuleStatement, null);

        // write to configure method
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                PsiJavaFile parentmoduleFile = (PsiJavaFile) parentModulePsiClass.getContainingFile();
                PsiImportStatement[] importStatements = parentmoduleFile.getImportList().getImportStatements();
                parentmoduleFile.getImportList().addAfter(importStatement, importStatements[importStatements.length - 1]);

                // TODO add to top of install order
                method.getBody().add(installModuleStatementElement);

                CodeStyleManager.getInstance(project).reformat(parentModulePsiClass);
                JavaCodeStyleManager.getInstance(project).optimizeImports(parentmoduleFile);
            }
        });

        parentModulePsiClass.navigate(true);

        logger.info("Added presenter gin install into " + parentModulePsiClass.getQualifiedName() + " "
                + installModuleStatement);
    }

    private PsiMethod findMethod(PsiClass unit, String methodName) {
        PsiMethod[] methods = unit.getMethods();
        if (methods == null) {
            return null;
        }

        for (PsiMethod method : methods) {
            if (method.getName().toString().contains(methodName)) {
                return method;
            }
        }

        return null;
    }

    private void createPresenterViewUi() {
        RenderedTemplate renderedTemplate = null;
        if (presenterConfigModel.getNestedPresenter()) {
            renderedTemplate = createdNestedPresenterTemplates.getViewui();
        } else if (presenterConfigModel.getPresenterWidget()) {
            renderedTemplate = createdPresenterWidgetTemplates.getViewui();
        } else if (presenterConfigModel.getPopupPresenter()) {
            renderedTemplate = createdPopupPresenterTemplates.getViewui();
        }

        String className = renderedTemplate.getNameAndNoExt();
        String contents = renderedTemplate.getContents();

        final PsiDirectory[] directoriesInPackage = createdPresenterPackage.getDirectories();
        final PsiFile element = PsiFileFactory.getInstance(project).createFileFromText(
                className, XmlFileType.INSTANCE, contents);

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                directoriesInPackage[0].add(element);
            }
        });
    }

    private void createPresenter() {
        RenderedTemplate renderedTemplate = null;
        if (presenterConfigModel.getNestedPresenter()) {
            renderedTemplate = createdNestedPresenterTemplates.getPresenter();
        } else if (presenterConfigModel.getPresenterWidget()) {
            renderedTemplate = createdPresenterWidgetTemplates.getPresenter();
        } else if (presenterConfigModel.getPopupPresenter()) {
            renderedTemplate = createdPopupPresenterTemplates.getPresenter();
        }

        createdPresenterPsiClass = createPsiClass(createdPresenterPackage, renderedTemplate);
        createdPresenterPsiClass.navigate(true);
    }

    private void createPresenterUiHandlers() {
        if (!presenterConfigModel.isUseAddUihandlers()) {
            return;
        }

        RenderedTemplate renderedTemplate = null;
        if (presenterConfigModel.getNestedPresenter()) {
            renderedTemplate = createdNestedPresenterTemplates.getUihandlers();
        } else if (presenterConfigModel.getPresenterWidget()) {
            renderedTemplate = createdPresenterWidgetTemplates.getUihandlers();
        } else if (presenterConfigModel.getPopupPresenter()) {
            renderedTemplate = createdPopupPresenterTemplates.getUihandlers();
        }

        PsiClass createdPsiClass = createPsiClass(createdPresenterPackage, renderedTemplate);
        createdPsiClass.navigate(true);
    }

    private void createPresenterView() {
        RenderedTemplate renderedTemplate = null;
        if (presenterConfigModel.getNestedPresenter()) {
            renderedTemplate = createdNestedPresenterTemplates.getView();
        } else if (presenterConfigModel.getPresenterWidget()) {
            renderedTemplate = createdPresenterWidgetTemplates.getView();
        } else if (presenterConfigModel.getPopupPresenter()) {
            renderedTemplate = createdPopupPresenterTemplates.getView();
        }

        PsiClass createdPsiClass = createPsiClass(createdPresenterPackage, renderedTemplate);
        createdPsiClass.navigate(true);
    }

    private void createPresenterModule() {
        RenderedTemplate renderedTemplate = null;
        if (presenterConfigModel.getNestedPresenter()) {
            renderedTemplate = createdNestedPresenterTemplates.getModule();
        } else if (presenterConfigModel.getPresenterWidget()) {
            renderedTemplate = createdPresenterWidgetTemplates.getModule();
        } else if (presenterConfigModel.getPopupPresenter()) {
            renderedTemplate = createdPopupPresenterTemplates.getModule();
        }

        createdModulePsiClass = createPsiClass(createdPresenterPackage, renderedTemplate);
        createdModulePsiClass.navigate(true);
    }

    private PsiClass createPsiClass(PsiPackage createInPsiPackage, RenderedTemplate renderedTemplate) {
        String className = renderedTemplate.getNameAndNoExt();
        String contents = renderedTemplate.getContents();

        final PsiDirectory[] directoriesInPackage = createInPsiPackage.getDirectories();
        final PsiFile element = PsiFileFactory.getInstance(project).createFileFromText(
                className, JavaFileType.INSTANCE, contents);

        final CreatedPsiClass createdPsiClass = new CreatedPsiClass();
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                PsiElement createdElement = directoriesInPackage[0].add(element);
                PsiJavaFile createdJavaFile = (PsiJavaFile) createdElement;
                PsiClass[] createdClasses = createdJavaFile.getClasses();
                createdPsiClass.setPsiClass(createdClasses[0]);
                CodeStyleManager.getInstance(project).reformat(createdClasses[0]);
                JavaCodeStyleManager.getInstance(project).optimizeImports(createdClasses[0].getContainingFile());
            }
        });

        return createdPsiClass.getPsiClass();
    }

    private void createPresenterPackage() {
        PsiDirectory baseDir = getBaseDir();
        String presenterPackageName = presenterConfigModel.getSelectedPackageAndNameAsSubPackage();
        createdPresenterPackage = createPackage(baseDir, presenterPackageName);
        logger.info("Created Package: " + presenterPackageName);
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

    private void addMethodsToNameTokens(final PsiClass nameTokensPsiClass) {
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

        // creating field doesn't want a newline
        fieldSource = fieldSource.replaceAll("\n", "");

        // add contents to class
        PsiElementFactory elementFactory = PsiElementFactory.SERVICE.getInstance(project);
        final PsiField newField = elementFactory.createFieldFromText(fieldSource, null);
        final PsiMethod newMethod = elementFactory.createMethodFromText(methodSource, null);

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                nameTokensPsiClass.add(newField);
                nameTokensPsiClass.add(newMethod);

                CodeStyleManager.getInstance(project).reformat(nameTokensPsiClass);
            }
        });
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
        presenterWidgetOptions.setSingleton(presenterConfigModel.isUseSingleton2());
        presenterWidgetOptions.setCustom(presenterConfigModel.isUseOverrideDefaultPopup());

        createdPopupPresenterTemplates = CreatePopupPresenter.run(presenterOptions, presenterWidgetOptions, true);
    }

    private void fetchPresenterWidgetTemplate(PresenterOptions presenterOptions) throws Exception {
        PresenterWidgetOptions presenterWidgetOptions = new PresenterWidgetOptions();
        presenterWidgetOptions.setSingleton(presenterConfigModel.isUseSingleton());

        createdPresenterWidgetTemplates = CreatePresenterWidget.run(presenterOptions, presenterWidgetOptions, true);
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

        createdNestedPresenterTemplates = CreateNestedPresenter.run(presenterOptions, nestedPresenterOptions, true);
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

        createdNameTokenTemplates = CreateNameTokens.run(nameTokenOptions, true, processFileOnly);
    }

    private void createPackageHierachyIndex() {
        packageHierarchy = new PackageHierarchy(presenterConfigModel);
        packageHierarchy.run();
    }

    private void createNameTokensPackage() {
        if (!presenterConfigModel.isUsePlace()) {
            return;
        }

        PsiPackage selectedPackage = presenterConfigModel.getSelectedPackageRoot();
        String selectedPackageString = selectedPackage.getQualifiedName();
        PackageHierarchyElement clientPackage = packageHierarchy.findParentClient(selectedPackageString);
        String clientPackageString = clientPackage.getPackageFragment().getQualifiedName();
        PsiDirectory baseDir = getBaseDir();

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

    private void createNametokensClass() throws Exception {
        if (!presenterConfigModel.isUsePlace()) {
            return;
        }

        // look for existing name tokens first.
        List<PsiClass> foundNameTokens = packageHierarchy.findClassName("NameTokens");

        PsiClass nameTokensPsiClass;
        if (foundNameTokens != null && foundNameTokens.size() > 0) {
            nameTokensPsiClass = foundNameTokens.get(0);
        } else {
            nameTokensPsiClass = createNewNameTokensClass();
        }

        if (nameTokensPsiClass == null) {
            // TODO
            //warn("Could not create NameTokens.java");
            return;
        }

        // used for import string
        presenterConfigModel.setNameTokenPsiClass(nameTokensPsiClass);

        nameTokensPsiClass.navigate(true);
    }

    private PsiClass createNewNameTokensClass() throws Exception {
        boolean processFileOnly = true;
        NameTokenOptions nameTokenOptions = new NameTokenOptions();
        nameTokenOptions.setPackageName(createdNameTokensPackage.getQualifiedName());
        CreatedNameTokens createdNameToken;

        createdNameToken = CreateNameTokens.run(nameTokenOptions, true, processFileOnly);

        RenderedTemplate renderedTemplate = createdNameToken.getNameTokensFile();
        PsiClass createdPsiClass = createPsiClass(createdNameTokensPackage, renderedTemplate);

        return createdPsiClass;
    }

    private PsiDirectory getBaseDir() {
        PsiPackage selectedPackage = presenterConfigModel.getSelectedPackageRoot();
        String selectedPackageString = selectedPackage.getQualifiedName();
        PackageHierarchyElement clientPackage = packageHierarchy.findParentClient(selectedPackageString);
        PsiDirectory baseDir = PsiManager.getInstance(presenterConfigModel.getProject())
                .findDirectory(clientPackage.getRoot());

        return baseDir;
    }
}
