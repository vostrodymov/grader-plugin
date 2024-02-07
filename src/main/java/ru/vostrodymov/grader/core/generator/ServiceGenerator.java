package ru.vostrodymov.grader.core.generator;

import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;
import ru.vostrodymov.grader.core.props.GraderProperties;
import ru.vostrodymov.grader.core.write.ClassWriter;

public class ServiceGenerator implements Generator<ModelDM> {
    private static final String BS_KEY = "namespace.service.base-service";
    private static final String SUFFIX = "Service";

    @Override
    public String run(ModelDM model, GraderProperties props) {


        var sClass = getClassDm(model);
        var bsClass = new ClassDM(props.get(BS_KEY));

        var writer = new ClassWriter();

        writer.writePackage(sClass.getPack());

        writer.writeImport("lombok.RequiredArgsConstructor");

        writer.newLine();

        writer.tab().append("@RequiredArgsConstructor").newLine();
        writer.writeClassName(sClass.getName(), bsClass);
        writer.begin();//classBegin

        writer.end(); // classEnd
        return writer.toString();
    }


    public ClassDM getClassDm(ModelDM model) {
        return new ClassDM(model.getClazz().getPack(), model.getClazz().getName() + SUFFIX);
    }
}
