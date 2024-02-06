package ru.vostrodymov.grader.plugin.command;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import ru.vostrodymov.grader.core.datamodel.KafkaDM;
import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.datamodel.YamlExtractor;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;
import ru.vostrodymov.grader.core.generator.KafkaListenerGenerator;
import ru.vostrodymov.grader.core.generator.KafkaServiceGenerator;
import ru.vostrodymov.grader.core.generator.ModelGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateCommand extends BaseCommand {
    private final YamlExtractor yamlExtractor = new YamlExtractor();
    private final ModelGenerator modelGenerator = new ModelGenerator();
    private final KafkaListenerGenerator listenerGenerator = new KafkaListenerGenerator();
    private final KafkaServiceGenerator serviceGenerator = new KafkaServiceGenerator();

    public GenerateCommand(Project project, PsiDirectory rootDirectory) {
        super(project, rootDirectory);
    }

    public void run(String agFile) throws Exception {
        final var psiFileFactory = PsiFileFactory.getInstance(getProject());
        final var psiDocumentManager = PsiDocumentManager.getInstance(getProject());

        final var modelDM = yamlExtractor.take(agFile);

        CommandProcessor.getInstance().executeCommand(getProject(), () -> {
            if (modelDM.getModels() != null) {
                for (var el : modelDM.getModels().entrySet()) {
                    var createdModel = createGenModel(el.getValue(), psiFileFactory);
                    var document = psiDocumentManager.getDocument(createdModel);
                    psiDocumentManager.commitDocument(document);
                }
            }
            if (modelDM.getProcessors() != null) {
                for (var el : modelDM.getProcessors().values()) {
                    List<PsiFile> createdFiles = new ArrayList<>();
                    if (el.getKafka() != null) {
                        createdFiles.addAll(genKafka(el.getKafka(), psiFileFactory));
                    }


                    for (var eFile : createdFiles) {
                        var document = psiDocumentManager.getDocument(eFile);
                        psiDocumentManager.commitDocument(document);
                    }
                }
            }

        }, "namr", "ru.namer");
    }

    private List<PsiFile> genKafka(KafkaDM kafka, PsiFileFactory psiFileFactory) {
        var createdListener = createGenListener(kafka, psiFileFactory);
        var createdService = createGenService(kafka, psiFileFactory);
        return Arrays.asList(createdService, createdListener);
    }

    private PsiFile createGenModel(ModelDM model, PsiFileFactory fileFactory) {
        final String java = modelGenerator.run(model);
        return createFile(java, model.getClazz(), fileFactory);
    }

    private PsiFile createGenListener(KafkaDM listener, PsiFileFactory fileFactory) {
        final String java = listenerGenerator.run(listener);
        return createFile(java, listener.getListener(), fileFactory);
    }

    private PsiFile createGenService(KafkaDM listener, PsiFileFactory fileFactory) {
        final String java = serviceGenerator.run(listener);
        return createFile(java, listener.getService(), fileFactory);
    }
}
