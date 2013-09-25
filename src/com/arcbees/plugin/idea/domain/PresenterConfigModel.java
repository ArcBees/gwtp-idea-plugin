package com.arcbees.plugin.idea.domain;

public class PresenterConfigModel {
    private String name;
    private String path;
    private boolean nestedPresenter;
    private boolean presenterWidget;
    private boolean popupPresenter;

    // nested
    private boolean place;
    private String nameToken;
    private boolean crawlable;
    private boolean codeSplit;
    private boolean revealInRoot;
    private boolean revealInRootLayout;
    private boolean revealInSlot;

    // popup
    private boolean overridePopup;
    private String popupPanel;

    // extra
    private boolean singleton;
    private boolean useUiHandlers;
    private boolean onBind;
    private boolean onHide;
    private boolean onReset;
    private boolean onUnbind;
    private boolean useManualReveal;
    private boolean usePrepareFromRequest;
    private String gatekeeper;

    public PresenterConfigModel() {
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

    public boolean getCrawlable() {
        return crawlable;
    }

    public void setCrawlable(boolean crawlable) {
        this.crawlable = crawlable;
    }

    public boolean getCodeSplit() {
        return codeSplit;
    }

    public void setCodeSplit(boolean codeSplit) {
        this.codeSplit = codeSplit;
    }

    public boolean getOverridePopup() {
        return overridePopup;
    }

    public void setOverridePopup(boolean overridePopup) {
        this.overridePopup = overridePopup;
    }

    public String getPopupPanel() {
        return popupPanel;
    }

    public void setPopupPanel(String popupPanel) {
        this.popupPanel = popupPanel;
    }

    public boolean getSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public boolean getUseUiHandlers() {
        return useUiHandlers;
    }

    public void setUseUiHandlers(boolean useUiHandlers) {
        this.useUiHandlers = useUiHandlers;
    }

    public boolean getOnBind() {
        return onBind;
    }

    public void setOnBind(boolean onBind) {
        this.onBind = onBind;
    }

    public boolean getOnHide() {
        return onHide;
    }

    public void setOnHide(boolean onHide) {
        this.onHide = onHide;
    }

    public boolean getOnReset() {
        return onReset;
    }

    public void setOnReset(boolean onReset) {
        this.onReset = onReset;
    }

    public boolean getOnUnbind() {
        return onUnbind;
    }

    public void setOnUnbind(boolean onUnbind) {
        this.onUnbind = onUnbind;
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

    @Override
    public String toString() {
        String s = "{ PresenterConfigModel: ";
        s += "name=" + name + " ";
        s += "path=" + path + " ";
        s += " }";
        return s;
    }
}
