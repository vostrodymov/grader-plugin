package ru.vostrodymov.grader.core.generator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.datamodel.PropertyDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassWithGenericDM;
import ru.vostrodymov.grader.core.write.ClassWriter;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class ModelFilterBuilderGenerator implements Generator<ModelDM> {
    private static final String FB_KEY = "namespace.filter-builder.filter-builder";
    private static final String TC_KEY = "namespace.filter-builder.type-converter";
    private static final String WD_KEY = "namespace.filter-builder.where-definition";
    private static final String CT_KEY = "namespace.filter-builder.compare-type";
    private static final String CS_KEY = "namespace.filter-builder.converter-store";
    private static final String SUFFIX = "FilterBuilder";

    public ClassDM getClassDm(ModelDM model) {
        return new ClassDM(model.getClazz().getPack(), model.getClazz().getName() + SUFFIX);
    }

    @Override
    public String run(ModelDM model) {
        var rb = ResourceBundle.getBundle("grader");
        var writer = new ClassWriter();

        var fClass = getClassDm(model);
        var qClazz = new ClassDM(model.getClazz().getPack(), "Q" + model.getClazz().getName());
        var bClass = new ClassWithGenericDM(rb.getString(FB_KEY), "FilterBuilder", model.getClazz());
        var tcClass = new ClassDM(rb.getString(TC_KEY), "TypeConverter");
        var wdClass = new ClassDM(rb.getString(WD_KEY), "WhereDefinition");
        var ctClass = new ClassDM(rb.getString(CT_KEY), "CompareType");
        var csClass = new ClassDM(rb.getString(CS_KEY), "ConverterStore");


        writer.writePackage(fClass.getPack());

        writer.writeImport("com.querydsl.core.types.dsl.BooleanExpression");
        writer.writeImport("com.querydsl.core.types.dsl.ComparablePath");
        writer.writeImport("com.querydsl.core.types.dsl.Expressions");
        writer.writeImport("com.querydsl.core.types.dsl.StringPath");
        writer.writeImport("org.springframework.stereotype.Component");
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
                .append(csClass.getName()).append(" ").append(csClass.getPropertyName()).append(")")
                .begin()
                .tab().append("super(").append(csClass.getPropertyName()).append(");").newLine()
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


    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    public static class Breadcrumbs {
        private Breadcrumbs parent;
        private String name;

        public Breadcrumbs(String name) {
            this(name, null);
        }

        public Breadcrumbs(String name, Breadcrumbs parent) {
            setName(name);
            setParent(parent);
        }

        public String getPath(String splitter) {
            return Optional.ofNullable(parent)
                    .map(q -> q.getPath(splitter))
                    .map(q -> q + splitter)
                    .orElse("") + name;
        }

        public String getWithoutRoot(String splitter) {
            return Optional.ofNullable(parent)
                    .filter(q -> q.getParent() != null)
                    .map(q -> q.getWithoutRoot(splitter))
                    .map(q -> q + splitter)
                    .orElse("") + name;
        }
    }
}
