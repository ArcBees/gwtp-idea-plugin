/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arcbees.plugin.idea.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.DirectoryChooserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.roots.ModulePackageIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ui.configuration.CommonContentEntriesEditor;
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.util.ActionRunner;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Query;

public class PackageUtilExt {
    private static final Logger LOG = Logger.getInstance("com.intellij.ide.util.PackageUtilExt");

    @Nullable
    public static PsiDirectory findOrCreateDirectoryForPackage(
            @NotNull Module module,
            String packageName,
            PsiDirectory baseDir,
            boolean askUserToCreate) throws IncorrectOperationException {
        final Project project = module.getProject();
        PsiDirectory psiDirectory = baseDir;

        if (psiDirectory == null) {
            if (!checkSourceRootsConfigured(module, askUserToCreate)) {
                return null;
            }
            final VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots();
            List<PsiDirectory> directoryList = new ArrayList<PsiDirectory>();
            for (VirtualFile sourceRoot : sourceRoots) {
                final PsiDirectory directory = PsiManager.getInstance(project).findDirectory(sourceRoot);
                if (directory != null) {
                    directoryList.add(directory);
                }
            }
            PsiDirectory[] sourceDirectories = directoryList.toArray(new PsiDirectory[directoryList.size()]);
            psiDirectory = DirectoryChooserUtil.selectDirectory(project, sourceDirectories, baseDir,
                    File.separatorChar + packageName.replace('.', File.separatorChar));
            if (psiDirectory == null) {
                return null;
            }
        }

        String restOfName = packageName;
        boolean askedToCreate = false;

        while (restOfName.length() > 0) {
            final String name = getLeftPart(restOfName);

            final PsiDirectory passDir = psiDirectory;
            PsiDirectory foundExistingDirectory = null;
            try {
                foundExistingDirectory = ActionRunner.runInsideWriteAction(
                        new ActionRunner.InterruptibleRunnableWithResult<PsiDirectory>() {
                            public PsiDirectory run() throws Exception {
                                return passDir.findSubdirectory(name);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (foundExistingDirectory == null) {
                if (!askedToCreate && askUserToCreate) {
                    if (!ApplicationManager.getApplication().isUnitTestMode()) {
                        int toCreate = Messages.showYesNoDialog(project,
                                IdeBundle.message("prompt.create.non.existing.package", packageName),
                                IdeBundle.message("title.package.not.found"),
                                Messages.getQuestionIcon());
                        if (toCreate != 0) {
                            return null;
                        }
                    }
                    askedToCreate = true;
                }

                final PsiDirectory psiDirectory1 = psiDirectory;
                try {
                    psiDirectory = ActionRunner.runInsideWriteAction(
                            new ActionRunner.InterruptibleRunnableWithResult<PsiDirectory>() {
                                public PsiDirectory run() throws Exception {
                                    return psiDirectory1.createSubdirectory(name);
                                }
                            });
                } catch (IncorrectOperationException e) {
                    throw e;
                } catch (IOException e) {
                    throw new IncorrectOperationException(e.toString(), e);
                } catch (Exception e) {
                    LOG.error(e);
                }
            } else {
                psiDirectory = foundExistingDirectory;
            }
            restOfName = cutLeftPart(restOfName);
        }
        return psiDirectory;
    }

    public static PsiPackage getSelectedPackageRoot(Project project, PsiElement e) {
        PsiPackage selectedPackage = null;
        if (e instanceof PsiClass) {
            PsiClass clazz = (PsiClass) e;
            PsiJavaFile javaFile = (PsiJavaFile) clazz.getContainingFile();
            selectedPackage =
                    JavaPsiFacade.getInstance(project).findPackage(javaFile.getPackageName());
        } else if (e instanceof PsiDirectory) {
            selectedPackage = JavaDirectoryService.getInstance().getPackage((PsiDirectory) e);
        }

        return selectedPackage;
    }

    private static boolean isWritablePackage(PsiPackage aPackage) {
        PsiDirectory[] directories = aPackage.getDirectories();
        for (PsiDirectory directory : directories) {
            if (directory.isValid() && directory.isWritable()) {
                return true;
            }
        }
        return false;
    }

    private static PsiDirectory getWritableModuleDirectory(@NotNull Query<VirtualFile> vFiles, @NotNull Module module,
            PsiManager manager) {
        for (VirtualFile vFile : vFiles) {
            if (ModuleUtil.findModuleForFile(vFile, module.getProject()) != module) {
                continue;
            }
            PsiDirectory directory = manager.findDirectory(vFile);
            if (directory != null && directory.isValid() && directory.isWritable()) {
                return directory;
            }
        }
        return null;
    }

    private static PsiPackage findLongestExistingPackage(Module module, String packageName) {
        final PsiManager manager = PsiManager.getInstance(module.getProject());

        String nameToMatch = packageName;
        while (true) {
            Query<VirtualFile> vFiles = ModulePackageIndex.getInstance(module).getDirsByPackageName(nameToMatch, false);
            PsiDirectory directory = getWritableModuleDirectory(vFiles, module, manager);
            if (directory != null) {
                return JavaDirectoryService.getInstance().getPackage(directory);
            }

            int lastDotIndex = nameToMatch.lastIndexOf('.');
            if (lastDotIndex >= 0) {
                nameToMatch = nameToMatch.substring(0, lastDotIndex);
            } else {
                return null;
            }
        }
    }

    private static String getLeftPart(String packageName) {
        int index = packageName.indexOf('.');
        return index > -1 ? packageName.substring(0, index) : packageName;
    }

    private static String cutLeftPart(String packageName) {
        int index = packageName.indexOf('.');
        return index > -1 ? packageName.substring(index + 1) : "";
    }

    public static boolean checkSourceRootsConfigured(final Module module) {
        return checkSourceRootsConfigured(module, true);
    }

    public static boolean checkSourceRootsConfigured(final Module module, final boolean askUserToSetupSourceRoots) {
        VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots();
        if (sourceRoots.length == 0) {
            if (!askUserToSetupSourceRoots) {
                return false;
            }

            Project project = module.getProject();
            Messages.showErrorDialog(project,
                    ProjectBundle.message("module.source.roots.not.configured.error", module.getName()),
                    ProjectBundle.message("module.source.roots.not.configured.title"));

            ProjectSettingsService.getInstance(project).showModuleConfigurationDialog(module.getName(),
                    CommonContentEntriesEditor.NAME);

            sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots();
            if (sourceRoots.length == 0) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    public static PsiDirectory findOrCreateSubdirectory(@NotNull PsiDirectory directory,
            @NotNull String directoryName) {
        PsiDirectory subDirectory = directory.findSubdirectory(directoryName);
        if (subDirectory == null) {
            subDirectory = directory.createSubdirectory(directoryName);
        }
        return subDirectory;
    }
}
