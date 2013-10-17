package com.arcbees.plugin.idea.utils;

import com.intellij.ide.projectView.ViewSettings;

public class ProjectViewSettings implements ViewSettings {
    @Override
    public boolean isShowMembers() {
        return false;
    }

    @Override
    public boolean isStructureView() {
        return false;
    }

    @Override
    public boolean isShowModules() {
        return false;
    }

    @Override
    public boolean isFlattenPackages() {
        return false;
    }

    @Override
    public boolean isAbbreviatePackageNames() {
        return false;
    }

    @Override
    public boolean isHideEmptyMiddlePackages() {
        return false;
    }

    @Override
    public boolean isShowLibraryContents() {
        return false;
    }
}
