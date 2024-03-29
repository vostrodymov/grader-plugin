package ru.vostrodymov.grader.plugin.command;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.generator.QueryGenerator;
import ru.vostrodymov.grader.core.props.GraderProperties;

public class QueryCommand extends BaseCommand {
    private final QueryGenerator filterBuilderGenerator = new QueryGenerator();

    public QueryCommand(Project project, PsiDirectory rootDirectory) {
        super(project, rootDirectory);
    }

    @Override
    protected void doRun(ModelDM model) {
        final var props = new GraderProperties(getProject());
        final var java = filterBuilderGenerator.run(model, props);
        createFile(java.getCode(), java.getClazz(), getPsiFileFactory());
    }


}
