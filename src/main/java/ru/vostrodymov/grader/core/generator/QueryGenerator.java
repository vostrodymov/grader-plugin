package ru.vostrodymov.grader.core.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.datamodel.PropertyDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassWithGenericDM;
import ru.vostrodymov.grader.core.generator.types.Breadcrumbs;
import ru.vostrodymov.grader.core.generator.types.JavaCode;
import ru.vostrodymov.grader.core.props.GraderProperties;
import ru.vostrodymov.grader.core.write.ClassWriter;

import java.util.*;
import java.util.stream.Collectors;

public class QueryGenerator extends JavaGenerator<ModelDM> {
    private static final String PACK_MASK_KEY = "query.package-mask";
    private static final String ANN_COMPONENT_KEY = "query.annotation-component";
    private static final String BQ_KEY = "query.base-query";
    private static final String TC_KEY = "query.type-converter";
    private static final String QD_KEY = "query.query-definition";
    private static final String WD_KEY = "query.where-definition";
    private static final String OD_KEY = "service.order-definition";
    private static final String CT_KEY = "query.compare-type";
    private static final String CS_KEY = "query.converter-store";
    public static final String SUFFIX = "Query";

    public ClassDM getClassDm(ModelDM model, GraderProperties props) {
        return getClassDm(
                model.getClazz().getName() + SUFFIX,
                model.getClazz().getPack(),
                Optional.ofNullable(props.get(PACK_MASK_KEY)).orElse(""));
    }

    @Override
    public JavaCode run(ModelDM model, GraderProperties props) {
        var writer = new ClassWriter();

        var fClass = getClassDm(model, props);
        var qClazz = new ClassDM(model.getClazz().getPack(), "Q" + model.getClazz().getName());
        var bqClass = new ClassWithGenericDM(props.get(BQ_KEY), model.getClazz());
        var tcClass = new ClassDM(props.get(TC_KEY));
        var qdClass = new ClassDM(props.get(QD_KEY));
        var wdClass = new ClassDM(props.get(WD_KEY));
        var ctClass = new ClassDM(props.get(CT_KEY));
        var csClass = new ClassDM(props.get(CS_KEY));
        var odClass = new ClassDM(props.get(OD_KEY));

        var emClass = new ClassDM("javax.persistence", "EntityManager");
        var bpClass = new ClassWithGenericDM("com.querydsl.core.types.dsl.EntityPathBase", model.getClazz());


        writer.writePackage(fClass.getPack());

        writer.writeImport("com.querydsl.core.types.dsl.BooleanExpression");
        writer.writeImport("com.querydsl.core.types.OrderSpecifier");
        writer.writeImport("com.querydsl.core.types.dsl.ComparablePath");
        writer.writeImport("com.querydsl.core.types.dsl.Expressions");
        writer.writeImport("com.querydsl.core.types.dsl.StringPath");
        writer.writeImport("org.springframework.stereotype.Component");
        writer.writeImport(emClass);
        writer.writeImport("lombok.Getter");
        writer.writeImport("lombok.Setter");
        writer.writeImport(bpClass);
        writer.writeImport("java.util.List");

        writer.writeImport(bqClass)
                .writeImport(tcClass)
                .writeImport(qdClass)
                .writeImport(csClass)
                .writeImport(ctClass)
                .writeImport(wdClass)
                .writeImport(odClass);

        writer.writeImport("java.util.Locale");

        if (props.getBool(ANN_COMPONENT_KEY)) {
            writer.append("@Component").newLine();
        }
        writer.writeClassName(fClass.getName(), bqClass);
        writer.begin();//classBegin
        //Const
        var consts = new ArrayList<PropertyConst>();
        writeConstPropertyNames(writer, model.getProperties(), new Breadcrumbs(qClazz.getPropertyName()), consts);
        writer.newLine();

        //Props
        writer.tab().append("@Getter").newLine();
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

        //Filter
        writeFilterExpression(writer, consts);

        writeFinById(writer, model.getClazz(), qdClass, wdClass, ctClass, consts);

        //Order
        writeOrderExpression(writer, consts, odClass);

        //BasePath
        writeBasePath(writer, bpClass, qClazz);

        writer.end(); // classEnd
        return new JavaCode(fClass, writer.toString());
    }

    private void writeConstPropertyNames(ClassWriter writer, Map<String, PropertyDM> properties,
                                         Breadcrumbs breadcrumbs, List<PropertyConst> result) {
        for (var el : properties.entrySet()) {
            var elBreadcrumbs = new Breadcrumbs(el.getKey(), breadcrumbs);
            if (!el.getValue().isObject()) {
                var pPath = elBreadcrumbs.getWithoutRoot(".").toLowerCase(Locale.ROOT);
                var pName = elBreadcrumbs.getWithoutRoot("_").toUpperCase(Locale.ROOT) + "_KEY";
                result.add(new PropertyConst(pName, el.getValue().getClazz(), elBreadcrumbs, el.getValue().isIdentifier()));

                writer.writeImport(el.getValue().getClazz());
                writer.tab().append("private static final String ").append(pName).append(" = \"").append(pPath).append("\";").newLine();
            } else {
                writeConstPropertyNames(writer, el.getValue().getProperties(), elBreadcrumbs, result);
            }
        }
    }

    private void writeFilterExpression(ClassWriter writer, List<PropertyConst> properties) {
        writer.tab().append("@Override").newLine();
        writer.tab().append("protected BooleanExpression toExpression(WhereDefinition where)")
                .begin()
                .tab().append("return switch (where.getField().toLowerCase(Locale.ROOT)) ")
                .begin();// beginReturn

        for (var el : properties) {
            writer.tab().append("case ").append(el.getName()).append(" -> ");
            if (el.getClazz().isString()) {
                writer.append("takeStringExpression(");
            } else {
                writer.append("takeComparableExpression(");
            }
            writer
                    .append("where.getCompare(), ")
                    .append(el.getBreadcrumbs().getPath(".")).append(", ")
                    .append("where.getValue(), ").append(el.getClazz().getName()).append(".class);").newLine();
        }

        writer.append("default -> Expressions.ONE.eq(Expressions.TWO);").newLine()
                .endAndSymbol(";") // endReturn
                .end();

        writer.newLine();
    }

    private void writeOrderExpression(ClassWriter writer, List<PropertyConst> properties, ClassDM odClass) {
        writer.tab().append("@Override").newLine();
        writer.tab().append("protected OrderSpecifier<?> toOrderExpression(").append(odClass.getName()).append(" order)")
                .begin()
                .tab().append("return switch (order.getField().toLowerCase(Locale.ROOT)) ")
                .begin();// beginReturn

        for (var el : properties) {
            writer.writeImport(el.getClazz());
            writer.tab().append("case ").append(el.getName()).append(" -> ");
            writer.append("toOrderSpecifier(");
            if (el.getClazz().isEnum()) {
                writer.newLine().append("orderCase(").append(el.getBreadcrumbs().getPath(".")).append(",").newLine()
                        .append(el.getClazz().getName()).append(".class), ").newLine()
                        .append("order.getDirection());").newLine();
            } else {
                writer.append(el.getBreadcrumbs().getPath(".")).append(", order.getDirection());").newLine();
            }
        }

        writer.append("default -> null;").newLine()
                .endAndSymbol(";") // endReturn
                .end();

        writer.newLine();
    }

    private void writeFinById(ClassWriter writer, ClassDM mClass, ClassDM qdClass, ClassDM wdClass, ClassDM ctClass, List<PropertyConst> properties) {
        var ids = properties.stream().filter(PropertyConst::isIdentifier).collect(Collectors.toList());
        writer.tab().append("public ").append(mClass.getName()).append(" findById(")
                .append(ids.stream().map(PropertyConst::getClazz).map(q -> q.getName() + " " + q.getPropertyName()).collect(Collectors.joining(", ")))
                .append(")")
                .begin()
                .tab().append("final var query = new ").append(qdClass.getName()).append("();").newLine()
                .tab().append("query.setWhere(List.of(").newLineAndTab();
        for (var el : ids) {
            writer.tab().append("new ").append(wdClass.getName()).append("(").append(el.getName()).append(", ").append(ctClass.getName()).append(".EQUAL, ").append(el.getClazz().getPropertyName()).append(")").newLine();
        }
        writer.tab().append("));").revAndNewLine()
                .tab().append("return this.findSingle(query);").newLine()
                .end();
    }


    private void writeBasePath(ClassWriter writer, ClassDM bpClass, ClassDM qClass) {
        writer.tab().append("@Override").newLine();
        writer.tab().append("protected ").append(bpClass.getName()).append(" getEntityPath()")
                .begin()
                .tab().append("return this.").append(qClass.getPropertyName()).append(";").newLine()
                .end();

    }

    @AllArgsConstructor
    @Getter
    private static class PropertyConst {
        private String name;
        private ClassDM clazz;
        private Breadcrumbs breadcrumbs;
        private boolean identifier;

    }
}
