package com.arcbees.plugin.idea.wizards.createpresenter;

import com.arcbees.plugin.idea.domain.PresenterConfigModel;
import com.arcbees.plugin.idea.icons.PluginIcons;
import com.arcbees.plugin.idea.utils.PackageHierarchy;
import com.arcbees.plugin.idea.utils.PackageHierarchyElement;
import com.arcbees.plugin.idea.utils.PackageUtilExt;
import com.arcbees.plugin.template.create.place.CreateNameTokens;
import com.arcbees.plugin.template.domain.place.CreatedNameTokens;
import com.arcbees.plugin.template.domain.place.NameTokenOptions;
import com.arcbees.plugin.template.domain.presenter.RenderedTemplate;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;

import java.util.List;
import java.util.logging.Logger;

public class CreatePresenterAction extends AnAction {
    public final static Logger logger = Logger.getLogger(CreatePresenterAction.class.getName());

    private PresenterConfigModel presenterConfigModel;
    private Project project;
    private PackageHierarchy packageHierarchy;
    private PsiPackage createdNameTokensPackage;

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

//        try {
//            fetchTemplatesNameTokens();
//        } catch (Exception e) {
//            warn("Could not fetch NameTokens templates: Error: " + e.toString());
//            e.printStackTrace();
//            return;
//        }

//        try {
//            fetchPresenterTemplates();
//        } catch (Exception e) {
//            warn("Could not fetch the ntested presenter templates: Error: " + e.toString());
//            e.printStackTrace();
//            return;
//        }

//        createNameTokensFieldAndMethods();
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

        PsiDirectory[] createdNameTokensPackageDirectories = createdNameTokensPackage.getDirectories();
        PsiFile element = PsiFileFactory.getInstance(project).createFileFromText(nameFile, JavaFileType.INSTANCE, contents);
        PsiElement createdNameTokensClass = createdNameTokensPackageDirectories[0].add(element);

        return (PsiClass) createdNameTokensClass;
    }

}
