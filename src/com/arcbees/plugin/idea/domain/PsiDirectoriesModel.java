package com.arcbees.plugin.idea.domain;

import com.intellij.psi.PsiDirectory;

public class PsiDirectoriesModel {
    private PsiDirectory[] psiDirectories;

    public PsiDirectory[] getPsiDirectories() {
        return psiDirectories;
    }

    public void setPsiDirectories(PsiDirectory[] psiDirectories) {
        this.psiDirectories = psiDirectories;
    }
}
