package ru.vostrodymov.grader.core.generator;

import ru.vostrodymov.grader.core.datamodel.KafkaDM;
import ru.vostrodymov.grader.core.write.ClassWriter;

public class KafkaServiceGenerator implements Generator<KafkaDM> {
    @Override
    public String run(KafkaDM listener) {
        var writer = new ClassWriter();

        writer.writePackage(listener.getService().getPack());

        writer.writeImport(listener.getRecordValue());

        writer.writeInterfaceName(listener.getService().getName())
                .begin()//interfaceBegin
                .newLine();

        writer.tab().append("void apply(").append(listener.getRecordValue().getName()).append(" data);").newLine();

        writer.end(); // interfaceEnd
        return writer.toString();
    }
}
