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

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;

import java.util.HashMap;
import java.util.Map;

public class PackageHierarchyElement {
    private final VirtualFile root;
    private final String packageElementName;
    private final PsiPackage packageFragment;
    private final HashMap<String, PsiClass> units;

    public PackageHierarchyElement(VirtualFile root, String packageName, PsiPackage packageFragment) {
        this.root = root;
        this.packageFragment = packageFragment;
        this.packageElementName = packageName;

        units = new HashMap<String, PsiClass>();
    }

    public String getPackageElementName() {
        return packageElementName;
    }

    public PsiPackage getPackageFragment() {
        return packageFragment;
    }

    public void addUnit(PsiClass unit) {
        units.put(unit.getName(), unit);
    }

    public Map<String, PsiClass> getUnits() {
        return units;
    }

    public void add(PsiClass[] units) {
        if (units == null) {
            return;
        }

        for (PsiClass unit : units) {
            addUnit(unit);
        }
    }

    public VirtualFile getRoot() {
        return root;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageElementName == null) ? 0 : packageElementName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PackageHierarchyElement other = (PackageHierarchyElement) obj;
        if (packageElementName == null) {
            if (other.packageElementName != null)
                return false;
        } else if (!packageElementName.equals(other.packageElementName))
            return false;
        return true;
    }
}
