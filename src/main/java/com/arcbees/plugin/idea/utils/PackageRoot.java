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

package com.arcbees.plugin.idea.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiPackage;

public class PackageRoot {
    private final Module module;
    private final VirtualFile root;
    private final PsiPackage psiPackage;

    public PackageRoot(Module module, VirtualFile root, PsiPackage psiPackage) {
        this.module = module;
        this.root = root;
        this.psiPackage = psiPackage;
    }

    public Module getModule() {
        return module;
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
        if (o == null || getClass() != o.getClass()) return false;

        PackageRoot that = (PackageRoot) o;

        if (module != null ? !module.equals(that.module) : that.module != null) return false;
        return !(psiPackage != null ? !psiPackage.equals(that.psiPackage) : that.psiPackage != null);

    }

    @Override
    public int hashCode() {
        int result = module != null ? module.hashCode() : 0;
        result = 31 * result + (psiPackage != null ? psiPackage.hashCode() : 0);
        return result;
    }
}
