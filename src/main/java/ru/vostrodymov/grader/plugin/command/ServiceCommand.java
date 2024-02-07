package ru.vostrodymov.grader.plugin.command;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.generator.ServiceGenerator;
import ru.vostrodymov.grader.core.props.GraderProperties;

public class ServiceCommand extends BaseCommand {
    private final ServiceGenerator serviceGenerator = new ServiceGenerator();

    public ServiceCommand(Project project, PsiDirectory rootDirectory) {
        super(project, rootDirectory);
    }

    @Override
    protected void doRun(ModelDM model) {
        final var props = new GraderProperties(getProject());
        final String java = serviceGenerator.run(model, props);
        createFile(java, serviceGenerator.getClassDm(model), getPsiFileFactory());
    }
}
