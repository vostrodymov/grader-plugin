package ru.vostrodymov.grader.plugin.command;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;

@Getter
@RequiredArgsConstructor
public abstract class BaseCommand {
    private final Project project;
    private final PsiDirectory rootDirectory;

    protected PsiFile createFile(String java, ClassDM clazz, PsiFileFactory fileFactory) {
        final String fileName = clazz.getName() + ".java";
        try {
            final var packDirectory = takeDirectoryForPackage(clazz.getPack());

            final PsiFile classFile = fileFactory.createFileFromText(fileName, JavaFileType.INSTANCE, java);
            CodeStyleManager.getInstance(classFile.getProject()).reformat(classFile);
            JavaCodeStyleManager.getInstance(classFile.getProject()).optimizeImports(classFile);

            final PsiFile created = (PsiFile) packDirectory.add(classFile);
            return created;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create new class from JSON", ex);
        }
    }

    protected PsiDirectory takeDirectoryForPackage(String pack) {
        var dirs = pack.split("\\.");
        var parent = this.getRootDirectory();
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
