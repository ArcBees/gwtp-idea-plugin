package com.arcbees.plugin.idea.domain;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiPackage;

public class PresenterConfigModel {
    private final Project project;

    private PsiPackage selectedPackage;

    private String name;
    private String path;
    private boolean nestedPresenter;
    private boolean presenterWidget;
    private boolean popupPresenter;

    // nested
    private boolean place;
    private String nameToken;
    private boolean revealInRoot;
    private boolean revealInRootLayout;
    private boolean revealInSlot;

    // popup
    private boolean useOverrideDefaultPopup;

    // extra
    private boolean useManualReveal;
    private boolean usePrepareFromRequest;
    private String gatekeeper;
    private String packageName;
    private String contentSlot;
    private boolean usePlace;
    private boolean useCrawlable;
    private boolean useCodesplit;
    private boolean useSingleton;

    private boolean useAddUihandlers;
    private boolean useAddOnbind;
    private boolean useAddOnhide;
    private boolean useAddOnreset;
    private boolean useAddOnunbind;

    public PresenterConfigModel(Project project) {
        this.project = project;

        // default settings
        nestedPresenter = true;
        revealInRoot = true;
    }

    public String getProjectName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean getNestedPresenter() {
        return nestedPresenter;
    }

    public void setNestedPresenter(boolean nestedPresenter) {
        this.nestedPresenter = nestedPresenter;
    }

    public boolean getPresenterWidget() {
        return presenterWidget;
    }

    public void setPresenterWidget(boolean presenterWidget) {
        this.presenterWidget = presenterWidget;
    }

    public boolean getPopupPresenter() {
        return popupPresenter;
    }

    public void setPopupPresenter(boolean popupPresenter) {
        this.popupPresenter = popupPresenter;
    }

    public boolean getPlace() {
        return place;
    }

    public void setPlace(boolean place) {
        this.place = place;
    }

    public String getNameToken() {
        return nameToken;
    }

    public String getNameTokenMethodName() {
        return "get" + nameToken.substring(0, 1).toUpperCase() + nameToken.substring(1);
    }

    public void setNameToken(String nameToken) {
        this.nameToken = nameToken;
    }

    public boolean getUseManualReveal() {
        return useManualReveal;
    }

    public void setUseManualReveal(boolean useManualReveal) {
        this.useManualReveal = useManualReveal;
    }

    public boolean getUsePrepareFromRequest() {
        return usePrepareFromRequest;
    }

    public void setUsePrepareFromRequest(boolean usePrepareFromRequest) {
        this.usePrepareFromRequest = usePrepareFromRequest;
    }

    public String getGatekeeper() {
        return gatekeeper;
    }

    public void setGatekeeper(String gatekeeper) {
        this.gatekeeper = gatekeeper;
    }

    public boolean getRevealInRoot() {
        return revealInRoot;
    }

    public void setRevealInRoot(boolean revealInRoot) {
        this.revealInRoot = revealInRoot;
    }

    public boolean getRevealInRootLayout() {
        return revealInRootLayout;
    }

    public void setRevealInRootLayout(boolean revealInRootLayout) {
        this.revealInRootLayout = revealInRootLayout;
    }

    public boolean getRevealInSlot() {
        return revealInSlot;
    }

    public void setRevealInSlot(boolean revealInSlot) {
        this.revealInSlot = revealInSlot;
    }

    public Project getProject() {
        return this.project;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public String getContentSlot() {
        return contentSlot;
    }

    public void setContentSlot(final String contentSlot) {
        this.contentSlot = contentSlot;
    }

    public boolean isUsePlace() {
        return usePlace;
    }

    public void setUsePlace(final boolean usePlace) {
        this.usePlace = usePlace;
    }

    public boolean isUseCrawlable() {
        return useCrawlable;
    }

    public void setUseCrawlable(final boolean useCrawlable) {
        this.useCrawlable = useCrawlable;
    }

    public boolean isUseCodesplit() {
        return useCodesplit;
    }

    public void setUseCodesplit(final boolean useCodesplit) {
        this.useCodesplit = useCodesplit;
    }

    public boolean isUseSingleton() {
        return useSingleton;
    }

    public void setUseSingleton(final boolean useSingleton) {
        this.useSingleton = useSingleton;
    }

    public boolean isUseOverrideDefaultPopup() {
        return useOverrideDefaultPopup;
    }

    public void setUseOverrideDefaultPopup(final boolean useOverrideDefaultPopup) {
        this.useOverrideDefaultPopup = useOverrideDefaultPopup;
    }

    public boolean isUseAddUihandlers() {
        return useAddUihandlers;
    }

    public void setUseAddUihandlers(final boolean useAddUihandlers) {
        this.useAddUihandlers = useAddUihandlers;
    }

    public boolean isUseAddOnbind() {
        return useAddOnbind;
    }

    public void setUseAddOnbind(final boolean useAddOnbind) {
        this.useAddOnbind = useAddOnbind;
    }

    public boolean isUseAddOnhide() {
        return useAddOnhide;
    }

    public void setUseAddOnhide(final boolean useAddOnhide) {
        this.useAddOnhide = useAddOnhide;
    }

    public boolean isUseAddOnreset() {
        return useAddOnreset;
    }

    public void setUseAddOnreset(final boolean useAddOnreset) {
        this.useAddOnreset = useAddOnreset;
    }

    public boolean isUseAddOnunbind() {
        return useAddOnunbind;
    }

    public void setUseAddOnunbind(final boolean useAddOnunbind) {
        this.useAddOnunbind = useAddOnunbind;
    }

    public PsiPackage getSelectedPackage() {
        return selectedPackage;
    }

    public void setSelectedPackage(PsiPackage selectedPackage) {
        this.selectedPackage = selectedPackage;
    }
}
