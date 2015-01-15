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

import com.arcbees.plugin.idea.domain.PresenterConfigModel;
import com.arcbees.plugin.idea.domain.PsiClassModel;
import com.arcbees.plugin.idea.domain.PsiClassesModel;
import com.intellij.ide.projectView.impl.nodes.PackageUtil;
import com.intellij.ide.projectView.impl.nodes.ProjectViewDirectoryHelper;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.util.containers.ContainerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class PackageHierarchy {
    private static final Logger logger = Logger.getLogger(PackageHierarchy.class.getName());

    private final PresenterConfigModel presenterConfigModel;
    private Map<String, PackageHierarchyElement> packagesIndex;

    public PackageHierarchy(PresenterConfigModel presenterConfigModel) {
        this.presenterConfigModel = presenterConfigModel;
    }

    public void run() {
        packagesIndex = new HashMap<String, PackageHierarchyElement>();

        logger.info("Creating package index.");

        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
                        startIndexing();
                    }
                });
            }
        }, ModalityState.NON_MODAL);

        logger.info("Finished package index.");
    }

    public PackageHierarchyElement find(String packageElementName) {
        return packagesIndex.get(packageElementName);
    }

    public PackageHierarchyElement findParent(String packageElementName) {
        if (!packageElementName.contains(".")) {
            return null;
        }

        String[] packageUnits = packageElementName.split("\\.");
        String parentPackageElementName = "";
        for (int i = 0; i < packageUnits.length - 1; i++) {
            parentPackageElementName += packageUnits[i];
            if (i < packageUnits.length - 2) {
                parentPackageElementName += ".";
            }
        }
        return find(parentPackageElementName);
    }

    public PackageHierarchyElement findParentClient(String packageElementName) {
        if (!packageElementName.contains(".")) {
            return null;
        }

        String parentPackageElementName = getClientPackageElementName(packageElementName);

        return find(parentPackageElementName);
    }

    public PackageHierarchyElement findParentClientAndAddPackage(String packageElementName, String andFindPackage) {
        if (!packageElementName.contains(".")) {
            return null;
        }

        String parentPackageElementName = getClientPackageElementName(packageElementName);
        parentPackageElementName += "." + andFindPackage;

        return find(parentPackageElementName);
    }

    /**
     * Takes the name like `tld.domain.project.client.child` and returns `tld.domain.project.client`
     */
    public String getClientPackageElementName(String packageElementName) {
        if (packageElementName.matches(".*client$")) {
            return packageElementName;
        }

        String[] packageUnits = packageElementName.split("\\.");
        String parentPackageElementName = "";

        for (int i = 0; i < packageUnits.length - 1; i++) {
            parentPackageElementName += packageUnits[i];
            if (i < packageUnits.length - 2) {
                parentPackageElementName += ".";
            }

            if (packageUnits[i].equals("client")) {
                break;
            }
        }

        if (parentPackageElementName.matches(".*\\.$")) {
            parentPackageElementName = parentPackageElementName.replaceAll("\\.$", "");
        }

        return parentPackageElementName;
    }

    public boolean isParentTheClientPackage(String packageElementName) {
        if (!packageElementName.contains(".")) {
            return false;
        }

        if (packageElementName.matches(".*client$")) {
            return true;
        } else {
            return false;
        }
    }

    public PsiClass findFirstInterfaceType(String findType) {
        PsiClass unit = null;
        for (String packageElementName : packagesIndex.keySet()) {
            unit = findFirstInterfaceTypeInPackage(packageElementName, findType);
            if (unit != null) {
                break;
            }
        }

        return unit;
    }

    public PsiClass findFirstInterfaceTypeInPackage(String packageElementName, String findTypeName) {
        PackageHierarchyElement hierarchyElement = packagesIndex.get(packageElementName);
        Map<String, PsiClass> units = hierarchyElement.getUnits();

        PsiClass foundUnit = null;
        for (String key : units.keySet()) {
            PsiClass unit = units.get(key);
            boolean hasType = findInterfaceUseInUnit(unit, findTypeName);
            if (hasType) {
                foundUnit = unit;
                break;
            }
        }

        return foundUnit;
    }

    public PsiClass findInterfaceTypeInParentPackage(final PsiPackage packageSelected, final String findTypeName) {
        final PsiClassModel psiClassModel = new PsiClassModel();
        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
                        PsiClass[] units = packageSelected.getClasses();

                        for (PsiClass unit : units) {
                            boolean found = findInterfaceUseInUnit(unit, findTypeName);
                            if (found) {
                                psiClassModel.set(unit);
                            }
                        }
                    }
                });
            }
        }, ModalityState.NON_MODAL);

        return psiClassModel.get();
    }

    private boolean findInterfaceUseInUnit(PsiClass unit, String findTypeName) {
        for (PsiClassType types : unit.getSuperTypes()) {
            PsiType[] superTypes = types.getSuperTypes();

            for (PsiType superType : superTypes) {
                if (superType.getCanonicalText().contains(findTypeName)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void startIndexing() {
        List<PackageRoot> packages = getTopLevelPackages();

        for (PackageRoot rootPackage : packages) {
            indexPackage(rootPackage.getRoot(), rootPackage.getPackage());
        }
    }

    private void indexPackage(VirtualFile root, PsiPackage packageFragment) {
        String packageName = packageFragment.getQualifiedName();
        PackageHierarchyElement packageIndex = new PackageHierarchyElement(root, packageName, packageFragment);
        packagesIndex.put(packageName, packageIndex);

        PsiClass[] classes = packageFragment.getClasses();
        packageIndex.add(classes);

        GlobalSearchScope scope = GlobalSearchScope.projectScope(presenterConfigModel.getProject());
        PsiPackage[] children = packageFragment.getSubPackages(scope);

        for (PsiPackage aChildren : children) {
            indexPackage(root, aChildren);
        }
    }

    public List<PsiClass> findClassName(final String name) {
        final PsiClassesModel psiClassesModel = new PsiClassesModel();
        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
                        GlobalSearchScope scope = GlobalSearchScope.allScope(presenterConfigModel.getProject());
                        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(presenterConfigModel.getProject());
                        PsiClass[] foundArray = shortNamesCache.getClassesByName(name, scope);
                        psiClassesModel.set(foundArray);
                    }
                });
            }
        }, ModalityState.NON_MODAL);

        return new ArrayList<PsiClass>(Arrays.asList(psiClassesModel.get()));
    }

    private List<PackageRoot> getTopLevelPackages() {
        Project myProject = presenterConfigModel.getProject();
        ProjectViewSettings viewSettings = new ProjectViewSettings();

        final List<VirtualFile> sourceRoots = new ArrayList<VirtualFile>();
        final ProjectRootManager projectRootManager = ProjectRootManager.getInstance(myProject);
        ContainerUtil.addAll(sourceRoots, projectRootManager.getContentSourceRoots());

        final PsiManager psiManager = PsiManager.getInstance(myProject);
        final List<AbstractTreeNode> children = new ArrayList<AbstractTreeNode>();
        final Set<PackageRoot> topLevelPackages = new HashSet<PackageRoot>();

        for (final VirtualFile root : sourceRoots) {
            final PsiDirectory directory = psiManager.findDirectory(root);
            if (directory == null) {
                continue;
            }
            final PsiPackage directoryPackage = JavaDirectoryService.getInstance().getPackage(directory);
            if (directoryPackage == null || PackageUtil.isPackageDefault(directoryPackage)) {
                // add subpackages
                final PsiDirectory[] subdirectories = directory.getSubdirectories();
                for (PsiDirectory subdirectory : subdirectories) {
                    final PsiPackage aPackage = JavaDirectoryService.getInstance().getPackage(subdirectory);
                    if (aPackage != null && !PackageUtil.isPackageDefault(aPackage)) {

                        PackageRoot packageRoot = new PackageRoot(root, aPackage);
                        topLevelPackages.add(packageRoot);
                    }
                }
                // add non-dir items
                children.addAll(ProjectViewDirectoryHelper.getInstance(myProject).getDirectoryChildren(directory, viewSettings, false));
            } else {
                // this is the case when a source root has package prefix assigned
                PackageRoot packageRoot = new PackageRoot(root, directoryPackage);
                topLevelPackages.add(packageRoot);
            }
        }

        return new ArrayList<PackageRoot>(topLevelPackages);
    }
}
