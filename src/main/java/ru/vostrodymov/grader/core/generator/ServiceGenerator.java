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
    private static final String OVERRIDE_CREATE_KEY = "service.override.create";
    private static final String OVERRIDE_UPDATE_KEY = "service.override.update";
    private static final String OVERRIDE_DELETE_KEY = "service.override.delete";
    private static final String WRITE_FIND_KEY = "service.write.find";
    private static final String WRITE_FIND_BY_ID_KEY = "service.write.find-by-id";
    private static final String WRITE_CREATE_KEY = "service.write.create";
    private static final String WRITE_UPDATE_KEY = "service.write.update";
    private static final String WRITE_DELETE_KEY = "service.write.delete";
    private static final String SUFFIX = "Service";

    @Override
    public String run(ModelDM model, GraderProperties props) {

        var sClass = getClassDm(model);
        var bsClass = new ClassWithGenericDM(props.get(BS_KEY), model.getClazz());
        var qdClass = new ClassDM(props.get(QD_KEY));
        var wdClass = new ClassDM(props.get(WD_KEY));
        var ctClass = new ClassDM(props.get(CT_KEY));
        var qbClass = new ClassDM(model.getClazz() + QueryGenerator.SUFFIX);

        var writer = new ClassWriter();

        writer.writePackage(sClass.getPack());

        writer.writeImport("lombok.RequiredArgsConstructor");
        writer.writeImport(qdClass);
        writer.writeImport(wdClass);
        writer.writeImport(ctClass);
        writer.writeImport(qbClass);
        writer.writeImport("org.springframework.stereotype.Service");
        writer.writeImport(new ClassDM("java.util.List"));

        writer.newLine();

        writer.writeJavadoc("Сервис для работы с сущностью " + model.getClazz().getName());
        writer.tab().append("@Service").newLine();
        writer.tab().append("@RequiredArgsConstructor").newLine();
        writer.writeClassName(sClass.getName(), bsClass);
        writer.begin();//classBegin
        writer.newLine();
        writer.writeProperty(qbClass, qbClass.getPropertyName(), true);

        // find
        writeFind(writer, model, props, qdClass, qbClass);

        //findById
        writeFindById(writer, model, props, qdClass, ctClass, qbClass);

        //create
        writeCreate(writer, model, props, qdClass, qbClass);

        //update
        writeUpdate(writer, model, props, qdClass, ctClass, qbClass);

        //delete
        writeDelete(writer, model, props, qdClass, ctClass, qbClass);

        writer.end(); // classEnd
        return writer.toString();
    }

    private void writeFind(ClassWriter writer, ModelDM model, GraderProperties props, ClassDM qdClass, ClassDM fbClass) {
        if (!props.getBool(WRITE_FIND_KEY)) {
            return;
        }
        writer.writeJavadoc("Метод получения списка сущностей");
        if (props.getBool(OVERRIDE_FIND_KEY)) {
            writer.tab().append("@Override").newLine();
        }
        writer.tab().append("public List<").append(model.getClazz().getName()).append("> find(")
                .append(qdClass.getName()).append(" ").append("query)")
                .begin()
                .tab().append("return ").append(fbClass.getPropertyName()).append(".find(query);").newLine()
                .end()
                .newLine();
    }

    private void writeFindById(ClassWriter writer, ModelDM model, GraderProperties props, ClassDM qdClass, ClassDM ctClass, ClassDM fbClass) {
        if (!props.getBool(WRITE_FIND_BY_ID_KEY)) {
            return;
        }
        var idProps = model.getProperties().entrySet().stream().filter(q -> q.getValue().isIdentifier()).collect(Collectors.toSet());
        var idsString = idProps.stream().map(q -> q.getValue().getClazz().getName() + " " + q.getKey()).collect(Collectors.joining(", "));
        idProps.stream().map(Map.Entry::getValue).map(PropertyDM::getClazz).forEach(writer::writeImport);
        writer.writeJavadoc("Метод получения сущности по ее идентификатору");
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
                .tab().append("return ").append(fbClass.getPropertyName()).append(".findSingle(query);").newLine()
                .end()
                .newLine();
    }

    private void writeCreate(ClassWriter writer, ModelDM model, GraderProperties props, ClassDM qdClass, ClassDM fbClass) {
        if (!props.getBool(WRITE_CREATE_KEY)) {
            return;
        }
        writer.writeJavadoc("Метод создания новой сущности");
        if (props.getBool(OVERRIDE_CREATE_KEY)) {
            writer.tab().append("@Override").newLine();
        }
        writer.tab().append("public void create(")
                .append(model.getClazz().getName()).append(" ").append(model.getClazz().getPropertyName()).append(")")
                .begin()
                .end()
                .newLine();
    }

    private void writeUpdate(ClassWriter writer, ModelDM model, GraderProperties props, ClassDM qdClass, ClassDM ctClass, ClassDM fbClass) {
        if (!props.getBool(WRITE_UPDATE_KEY)) {
            return;
        }
        var idProps = model.getProperties().entrySet().stream().filter(q -> q.getValue().isIdentifier()).collect(Collectors.toSet());
        var idsString = idProps.stream().map(q -> q.getValue().getClazz().getName() + " " + q.getKey()).collect(Collectors.joining(", "));
        idProps.stream().map(Map.Entry::getValue).map(PropertyDM::getClazz).forEach(writer::writeImport);
        writer.writeJavadoc("Метод обновляет сущность по ее идентификатору");
        if (props.getBool(OVERRIDE_UPDATE_KEY)) {
            writer.tab().append("@Override").newLine();
        }
        writer.tab().append("public void update(").append(idsString).append(")")
                .begin()
                .end()
                .newLine();
    }

    private void writeDelete(ClassWriter writer, ModelDM model, GraderProperties props, ClassDM qdClass, ClassDM ctClass, ClassDM fbClass) {
        if (!props.getBool(WRITE_DELETE_KEY)) {
            return;
        }
        var idProps = model.getProperties().entrySet().stream().filter(q -> q.getValue().isIdentifier()).collect(Collectors.toSet());
        var idsString = idProps.stream().map(q -> q.getValue().getClazz().getName() + " " + q.getKey()).collect(Collectors.joining(", "));
        idProps.stream().map(Map.Entry::getValue).map(PropertyDM::getClazz).forEach(writer::writeImport);
        writer.writeJavadoc("Метод обновляет сущность по ее идентификатору");
        if (props.getBool(OVERRIDE_DELETE_KEY)) {
            writer.tab().append("@Override").newLine();
        }
        writer.tab().append("public void delete(").append(idsString).append(")")
                .begin()
                .end()
                .newLine();
    }

    public ClassDM getClassDm(ModelDM model) {
        return new ClassDM(model.getClazz().getPack(), model.getClazz().getName() + SUFFIX);
    }
}
