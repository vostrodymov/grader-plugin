package ru.vostrodymov.grader.plugin.command;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;
import ru.vostrodymov.grader.core.parser.PsiClassExtractor;

import java.io.File;
import java.util.ResourceBundle;

@Getter
public abstract class BaseCommand {
    private final Project project;
    private final PsiDirectory rootDirectory;
    private final PsiManager psiMgr;
    private final PsiDocumentManager psiDocumentManager;
    private final PsiFileFactory psiFileFactory;

    public BaseCommand(Project project, PsiDirectory rootDirectory) {
        this.project = project;
        this.rootDirectory = rootDirectory;
        this.psiMgr = PsiManager.getInstance(getProject());
        this.psiDocumentManager = PsiDocumentManager.getInstance(getProject());
        this.psiFileFactory = PsiFileFactory.getInstance(getProject());
        System.out.println(project.getBasePath());
        takeProjectConfig();
    }

    public void run(VirtualFile vFile) {


        final var classExtractor = new PsiClassExtractor();
        PsiFile file = psiMgr.findFile(vFile);
        if ((file instanceof PsiJavaFile)) {
            final var jel = (PsiJavaFile) file;

            CommandProcessor.getInstance().executeCommand(getProject(), () -> {

                for (var cel : jel.getClasses()) {
                    var model = classExtractor.take(jel.getPackageName(), cel);
                    if (model != null) {
                        doRun(model);
                    }
                }
            }, "namr", "ru.namer");
        }
    }

    protected PsiFile createFile(String java, ClassDM clazz, PsiFileFactory fileFactory) {
        final String fileName = clazz.getName() + ".java";
        try {
            final var packDirectory = takeDirectoryForPackage(clazz.getPack());

            final PsiFile classFile = fileFactory.createFileFromText(fileName, JavaFileType.INSTANCE, java);
            CodeStyleManager.getInstance(classFile.getProject()).reformat(classFile);
            JavaCodeStyleManager.getInstance(classFile.getProject()).optimizeImports(classFile);

            final PsiFile created = (PsiFile) packDirectory.add(classFile);

            var document = getPsiDocumentManager().getDocument(created);
            getPsiDocumentManager().commitDocument(document);
            return created;
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка создания нового файла", ex);
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

    protected void takeProjectConfig() {


    }

    protected abstract void doRun(ModelDM model);
}
