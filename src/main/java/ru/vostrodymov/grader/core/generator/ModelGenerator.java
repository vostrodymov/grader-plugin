package ru.vostrodymov.grader.core.generator;


import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.props.GraderProperties;
import ru.vostrodymov.grader.core.write.ClassWriter;

@Deprecated
public class ModelGenerator implements Generator<ModelDM> {

    @Override
    public String run(ModelDM model, GraderProperties props) {
        var writer = new ClassWriter();

        writer.writePackage(model.getClazz().getPack());

        writer.writeImport("lombok.Getter")
                .writeImport("lombok.Setter");

        writer.append("@Getter").newLine();
        writer.append("@Setter").newLine();
        writer.writeClassName(model.getClazz().getName(), null);
        writer.begin();//classBegin

        if (model.getProperties() != null) {
            for (var el : model.getProperties().entrySet()) {
                writer.writeJavadoc(el.getValue().getDescription());
                writer.writeProperty(el.getValue().getClazz(), el.getKey());
            }
        }

        writer.end(); // classEnd
        return writer.toString();
    }
}
