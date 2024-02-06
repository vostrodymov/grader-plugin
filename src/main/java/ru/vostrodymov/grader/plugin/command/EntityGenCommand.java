package ru.vostrodymov.grader.plugin.command;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.generator.ModelFilterBuilderGenerator;
import ru.vostrodymov.grader.core.parser.PsiClassExtractor;

import java.util.ArrayList;
import java.util.List;

public class EntityGenCommand extends BaseCommand {
    private final ModelFilterBuilderGenerator filterBuilderGenerator = new ModelFilterBuilderGenerator();


    public EntityGenCommand(Project project, PsiDirectory rootDirectory) {
        super(project, rootDirectory);
    }

    public void run(VirtualFile vFile) {
        final PsiManager psiMgr = PsiManager.getInstance(getProject());
        final PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(getProject());
        final var psiFileFactory = PsiFileFactory.getInstance(getProject());

        var classExtractor = new PsiClassExtractor();
        PsiFile file = psiMgr.findFile(vFile);
        if ((file instanceof PsiJavaFile)) {
            var jel = (PsiJavaFile) file;

            for (var cel : jel.getClasses()) {
                var model = classExtractor.take(jel.getPackageName(), cel);
                if (model != null) {

                    CommandProcessor.getInstance().executeCommand(getProject(), () -> {
                        List<PsiFile> createdFiles = new ArrayList<>();
                        createdFiles.add(createFilterBuilder(model, psiFileFactory));

                        for (var eFile : createdFiles) {
                            var document = psiDocumentManager.getDocument(eFile);
                            psiDocumentManager.commitDocument(document);
                        }

                    }, "namr", "ru.namer");

                }
            }
        }
    }


    private PsiFile createFilterBuilder(ModelDM model, PsiFileFactory fileFactory) {
        final String java = filterBuilderGenerator.run(model);
        return createFile(java, filterBuilderGenerator.getClassDm(model), fileFactory);
    }


}
