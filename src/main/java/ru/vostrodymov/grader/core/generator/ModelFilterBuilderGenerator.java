package ru.vostrodymov.grader.core.generator;

import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.datamodel.PropertyDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassWithGenericDM;
import ru.vostrodymov.grader.core.generator.types.Breadcrumbs;
import ru.vostrodymov.grader.core.props.GraderProperties;
import ru.vostrodymov.grader.core.write.ClassWriter;

import java.util.Locale;
import java.util.Map;

public class ModelFilterBuilderGenerator implements Generator<ModelDM> {
    private static final String FB_KEY = "filter-builder.filter-builder";
    private static final String TC_KEY = "filter-builder.type-converter";
    private static final String WD_KEY = "filter-builder.where-definition";
    private static final String CT_KEY = "filter-builder.compare-type";
    private static final String CS_KEY = "filter-builder.converter-store";
    private static final String SUFFIX = "FilterBuilder";

    public ClassDM getClassDm(ModelDM model) {
        return new ClassDM(model.getClazz().getPack(), model.getClazz().getName() + SUFFIX);
    }

    @Override
    public String run(ModelDM model, GraderProperties props) {
        var writer = new ClassWriter();

        var fClass = getClassDm(model);
        var qClazz = new ClassDM(model.getClazz().getPack(), "Q" + model.getClazz().getName());
        var bClass = new ClassWithGenericDM(props.get(FB_KEY), model.getClazz());
        var tcClass = new ClassDM(props.get(TC_KEY));
        var wdClass = new ClassDM(props.get(WD_KEY));
        var ctClass = new ClassDM(props.get(CT_KEY));
        var csClass = new ClassDM(props.get(CS_KEY));

        var emClass = new ClassDM("javax.persistence", "EntityManager");

        writer.writePackage(fClass.getPack());

        writer.writeImport("com.querydsl.core.types.dsl.BooleanExpression");
        writer.writeImport("com.querydsl.core.types.dsl.ComparablePath");
        writer.writeImport("com.querydsl.core.types.dsl.Expressions");
        writer.writeImport("com.querydsl.core.types.dsl.StringPath");
        writer.writeImport("org.springframework.stereotype.Component");
        writer.writeImport(emClass);
        writer.writeImport("lombok.Getter");
        writer.writeImport("lombok.Setter");

        writer.writeImport(bClass)
                .writeImport(tcClass)
                .writeImport(csClass)
                .writeImport(ctClass)
                .writeImport(wdClass);

        writer.writeImport("java.util.Locale");

        writer.append("@Component").newLine();
        writer.writeClassName(fClass.getName(), bClass);
        writer.begin();//classBegin
        //Props
        writer.tab().append("@Getter").newLine()
                .tab().append("@Setter").newLine();
        writer.writeProperty(qClazz, qClazz.getPropertyName(), true);
        writer.newLine();

        //Ctr
        writer.tab().append("public ").append(fClass.getName()).append("(")
                .append(csClass.getName()).append(" ").append(csClass.getPropertyName()).append(", ")
                .append(emClass.getName()).append(" ").append(emClass.getPropertyName())
                .append(")")
                .begin()
                .tab().append("super(").append(csClass.getPropertyName()).append(", ").append(emClass.getPropertyName()).append(");").newLine()
                .tab().append("this.").append(qClazz.getPropertyName()).append(" = ").append(qClazz.getName()).append(".").append(model.getClazz().getPropertyName()).append(";").newLine()
                .end()
                .newLine();
        writer.newLine();

        writer.tab().append("public BooleanExpression toExpression(WhereDefinition where)")
                .begin()
                .tab().append("return switch (where.getField().toLowerCase(Locale.ROOT)) ")
                .begin();// beginReturn
        writeFilterProperties(writer, qClazz, model.getProperties(), new Breadcrumbs(qClazz.getPropertyName()));

        writer.append("default -> Expressions.ONE.eq(Expressions.TWO);").newLine()
                .endAndSymbol(";") // endReturn
                .end();

        writer.end(); // classEnd
        return writer.toString();
    }

    private void writeFilterProperties(ClassWriter writer, ClassDM qClazz, Map<String, PropertyDM> properties, Breadcrumbs breadcrumbs) {
        for (var el : properties.entrySet()) {
            var elBreadcrumbs = new Breadcrumbs(el.getKey(), breadcrumbs);
            if (!el.getValue().isObject()) {
                writer.writeImport(el.getValue().getClazz());
                writer.tab().append("case \"").append(elBreadcrumbs.getWithoutRoot(".").toLowerCase(Locale.ROOT)).append("\" -> ");
                if (el.getValue().getClazz().isString()) {
                    writer.append("takeStringExpression(");
                } else {
                    writer.append("takeComparableExpression(");
                }
                writer
                        .append("where.getCompare(), ")
                        .append(elBreadcrumbs.getPath(".")).append(", ")
                        .append("where.getValue(), ").append(el.getValue().getClazz().getName()).append(".class);").newLine();
            } else {
                writeFilterProperties(writer, qClazz, el.getValue().getProperties(), elBreadcrumbs);
            }
        }
    }
}
