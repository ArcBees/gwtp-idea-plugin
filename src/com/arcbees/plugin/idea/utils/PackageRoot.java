package com.arcbees.plugin.idea.utils;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiPackage;

public class PackageRoot {
    private VirtualFile root;
    private PsiPackage psiPackage;

    public PackageRoot(VirtualFile root, PsiPackage psiPackage) {
        this.root = root;
        this.psiPackage = psiPackage;
    }

    public VirtualFile getRoot() {
        return root;
    }

    public PsiPackage getPackage() {
        return psiPackage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackageRoot)) return false;

        PackageRoot that = (PackageRoot) o;

        if (psiPackage != null ? !psiPackage.equals(that.psiPackage) : that.psiPackage != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return psiPackage != null ? psiPackage.hashCode() : 0;
    }
}
