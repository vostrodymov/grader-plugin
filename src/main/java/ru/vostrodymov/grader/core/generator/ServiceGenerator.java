package ru.vostrodymov.grader.core.generator;

import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.datamodel.PropertyDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassWithGenericDM;
import ru.vostrodymov.grader.core.props.GraderProperties;
import ru.vostrodymov.grader.core.write.ClassWriter;

import java.util.Map;
import java.util.stream.Collectors;

public class ServiceGenerator implements Generator<ModelDM> {
    private static final String BS_KEY = "service.base-service";
    private static final String QD_KEY = "service.query-definition";
    private static final String WD_KEY = "service.where-definition";
    private static final String CT_KEY = "service.compare-type";

    private static final String OVERRIDE_FIND_KEY = "service.override.find";
    private static final String OVERRIDE_FIND_BY_ID_KEY = "service.override.find-by-id";
    private static final String SUFFIX = "Service";

    @Override
    public String run(ModelDM model, GraderProperties props) {

        var sClass = getClassDm(model);
        var bsClass = new ClassWithGenericDM(props.get(BS_KEY), model.getClazz());
        var qdClass = new ClassDM(props.get(QD_KEY));
        var wdClass = new ClassDM(props.get(WD_KEY));
        var ctClass = new ClassDM(props.get(CT_KEY));

        var writer = new ClassWriter();

        writer.writePackage(sClass.getPack());

        writer.writeImport("lombok.RequiredArgsConstructor");
        writer.writeImport(qdClass);
        writer.writeImport(wdClass);
        writer.writeImport(ctClass);
        writer.writeImport(new ClassDM("java.util.List"));

        writer.newLine();

        writer.tab().append("@RequiredArgsConstructor").newLine();
        writer.writeClassName(sClass.getName(), bsClass);
        writer.begin();//classBegin
        writer.newLine();

        // find
        writeFind(writer, model, props, qdClass);
        writer.newLine();

        //findById
        writeFindById(writer, model, props, qdClass, ctClass);
        writer.newLine();

        writer.end(); // classEnd
        return writer.toString();
    }

    private void writeFind(ClassWriter writer, ModelDM model, GraderProperties props, ClassDM qdClass) {
        writer.writeJavadoc("Метод получения списка сущностей");
        if (props.getBool(OVERRIDE_FIND_KEY)) {
            writer.tab().append("@Override").newLine();
        }
        writer.tab().append("public List<").append(model.getClazz().getName()).append("> find(")
                .append(qdClass.getName()).append(" ").append("query)")
                .begin()
                .tab().append("return null;").newLine()
                .end();
    }

    private void writeFindById(ClassWriter writer, ModelDM model, GraderProperties props, ClassDM qdClass, ClassDM ctClass) {
        var idProps = model.getProperties().entrySet().stream().filter(q -> q.getValue().isIdentifier()).collect(Collectors.toSet());
        var idsString = idProps.stream().map(q -> q.getValue().getClazz().getName() + " " + q.getKey()).collect(Collectors.joining(", "));
        idProps.stream().map(Map.Entry::getValue).map(PropertyDM::getClazz).forEach(writer::writeImport);
        writer.writeJavadoc("Метод сущности по ее идентификатору");
        if (props.getBool(OVERRIDE_FIND_BY_ID_KEY)) {
            writer.tab().append("@Override").newLine();
        }
        writer.tab().append("public ").append(model.getClazz().getName()).append(" findById(").append(idsString).append(")")
                .begin()
                .tab().append("final var query = new ").append(qdClass.getName()).append("();").newLine()
                .tab().append("query.setWhere(List.of(").newLineAndTab();
        for (var el : idProps) {
            writer.tab().append("new WhereDefinition(\"").append(el.getKey()).append("\", ").append(ctClass.getName()).append(".EQUAL, ").append(el.getKey()).append(")").newLine();
        }
        writer.tab().append("));").revAndNewLine()
                .tab().append("return null;").newLine()
                .end();
    }


    public ClassDM getClassDm(ModelDM model) {
        return new ClassDM(model.getClazz().getPack(), model.getClazz().getName() + SUFFIX);
    }
}
