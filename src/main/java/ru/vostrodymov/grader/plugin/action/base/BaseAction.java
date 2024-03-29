package ru.vostrodymov.grader.plugin.action.base;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiDirectoryImpl;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public abstract class BaseAction extends AnAction {

    protected PsiDirectory takeRootDirectory(Project project, DataContext dataContext) {
        var vfile = dataContext.getData(CommonDataKeys.VIRTUAL_FILE);
        if (project == null || vfile == null) {
            System.out.println("Ошибка, не нйден файл иои проект");
            throw new RuntimeException("Ошибка, не нйден файл иои проект");
        }

        var module = LangDataKeys.MODULE.getData(dataContext);
        var patter2 = Pattern.compile("src/main/java");
        AtomicReference<PsiDirectory> rootDir = new AtomicReference<>();

        ModuleRootManager root = ModuleRootManager.getInstance(module);
        for (VirtualFile file : root.getSourceRoots()) {
            System.out.println(file.getFileType().getName() + "   " + file);
            if (patter2.matcher(file.getPath()).find()) {
                rootDir.set(new PsiDirectoryImpl(((PsiManagerImpl) PsiManager.getInstance(project)), file));
            }
        }

        return rootDir.get();
    }

    protected PsiDirectory takeDirectoryForPackage(PsiDirectory root, String pack) {
        var dirs = pack.split("\\.");
        var parent = root;
        for (var dir : dirs) {
            var current = parent.findSubdirectory(dir);
            if (current == null) {
                current = parent.createSubdirectory(dir);
            }
            parent = current;
        }
        return parent;
    }
}
