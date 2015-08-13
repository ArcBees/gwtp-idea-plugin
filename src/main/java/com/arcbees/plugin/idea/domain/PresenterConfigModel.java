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

package com.arcbees.plugin.idea.domain;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;

public class PresenterConfigModel {
    private final Project project;

    private String name;
    private String path;
    private PresenterType selectedPresenter;

    // nested
    private String nameToken;
    private RevealLocation revealLocation;

    // popup
    private boolean useOverrideDefaultPopup;

    // extra
    private boolean useManualReveal;
    private String gatekeeper;
    private String packageName;
    private String contentSlot;
    private boolean usePlace;
    private boolean useCrawlable;
    private boolean useCodesplit;
    private boolean useSingleton;

    private boolean useAddUihandlers;
    private Module module;
    private PsiDirectory baseDir;
    private PsiClass nameTokenPsiClass;
    private PsiClass contentSlotClass;
    private boolean useSingleton2;

    public PresenterConfigModel(Project project) {
        this.project = project;

        // default settings
        selectedPresenter = PresenterType.NESTED_PRESENTER;
        revealLocation = RevealLocation.ROOT;
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

    public String getGatekeeper() {
        return gatekeeper;
    }

    public void setGatekeeper(String gatekeeper) {
        this.gatekeeper = gatekeeper;
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

    public void setUseSingleton2(boolean useSingleton2) {
        this.useSingleton2 = useSingleton2;
    }

    public boolean isUseSingleton2() {
        return useSingleton2;
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

    public void setModule(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public void setNameTokenPsiClass(PsiClass nameTokenPsiClass) {
        this.nameTokenPsiClass = nameTokenPsiClass;
    }

    public PsiClass getNameTokenPsiClass() {
        return nameTokenPsiClass;
    }

    public String getNameTokenWithClass() {
        if (nameTokenPsiClass == null) {
            return "";
        }

        return nameTokenPsiClass.getName().replace(".java", "") + "." + nameToken;
    }

    public String getNameTokenUnitImport() {
        if (nameTokenPsiClass == null) {
            return "";
        }

        final StringModel stringModel = new StringModel();
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                String qualifiedClassName = nameTokenPsiClass.getQualifiedName();
                stringModel.set(qualifiedClassName);
            }
        });

        return "import " + stringModel.get() + ";";
    }

    public String getContentSlotImport() {
        if (contentSlotClass == null) {
            return "";
        }

        final StringModel stringModel = new StringModel();
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                String qualifiedClassName = contentSlotClass.getQualifiedName();
                stringModel.set(qualifiedClassName);
            }
        });

        return "import " + stringModel.get() + ";";
    }

    public void setContentSlotClass(PsiClass contentSlotClass) {
        this.contentSlotClass = contentSlotClass;
    }

    public PresenterType getSelectedPresenter() {
        return selectedPresenter;
    }

    public void setSelectedPresenter(PresenterType selectedPresenter) {
        this.selectedPresenter = selectedPresenter;
    }

    public RevealLocation getRevealLocation() {
        return revealLocation;
    }

    public void setRevealLocation(RevealLocation revealLocation) {
        this.revealLocation = revealLocation;
    }
}
