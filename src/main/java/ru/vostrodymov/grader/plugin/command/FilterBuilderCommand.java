package ru.vostrodymov.grader.plugin.command;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.generator.ModelFilterBuilderGenerator;
import ru.vostrodymov.grader.core.props.GraderProperties;

public class FilterBuilderCommand extends BaseCommand {
    private final ModelFilterBuilderGenerator filterBuilderGenerator = new ModelFilterBuilderGenerator();

    public FilterBuilderCommand(Project project, PsiDirectory rootDirectory) {
        super(project, rootDirectory);
    }

    @Override
    protected void doRun(ModelDM model) {
        final var props = new GraderProperties(getProject());
        final String java = filterBuilderGenerator.run(model, props);
        createFile(java, filterBuilderGenerator.getClassDm(model), getPsiFileFactory());
    }


}
