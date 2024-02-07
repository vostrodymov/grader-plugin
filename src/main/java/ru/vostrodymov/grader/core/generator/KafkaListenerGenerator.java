package ru.vostrodymov.grader.core.generator;

import ru.vostrodymov.grader.core.datamodel.KafkaDM;
import ru.vostrodymov.grader.core.props.GraderProperties;
import ru.vostrodymov.grader.core.write.ClassWriter;

@Deprecated
public class KafkaListenerGenerator implements Generator<KafkaDM> {
    @Override
    public String run(KafkaDM listener, GraderProperties props) {
        var writer = new ClassWriter();

        writer.writePackage(listener.getListener().getPack());

        writer.writeImport("lombok.RequiredArgsConstructor");
        writer.writeImport("lombok.extern.slf4j.Slf4j");
        writer.writeImport("org.apache.kafka.clients.consumer.ConsumerRecord");
        writer.writeImport("org.springframework.kafka.annotation.KafkaListener");
        writer.writeImport("org.springframework.stereotype.Service");
        writer.writeImport(listener.getRecordKey());
        writer.writeImport(listener.getRecordValue());

        writer.append("@Slf4j").newLine();
        writer.append("@Service").newLine();
        writer.append("@RequiredArgsConstructor").newLine();
        writer.writeClassName(listener.getListener().getName(), null);
        writer.begin();//classBegin
        writer.writeProperty(listener.getService(), listener.getService().getPropertyName());
        writer.newLine();

        writer.tab().append("@KafkaListener(topics = \"").append(listener.getListener().getName()).append(".topic\",").newLineAndTab()
                .tab().append("properties = {").newLineAndTab()
                .tab().append("\"spring.json.value.default.type:").append(listener.getRecordValue().getFullName()).append("\",").newLine()
                .tab().append("\"spring.json.use.type.headers:false\"").newLine()
                .tab().append("})").revAndNewLine();
        writer.tab().append("public void listen(ConsumerRecord<")
                .append(listener.getRecordKey().getName()).append(", ").append(listener.getRecordValue().getName())
                .append("> record)")
                .begin()
                .tab().append("log.trace(\"Получено сообщение topic: {}, partition:{}, key: {}\", ").newLineAndTab()
                .append("record.topic(), ").newLine()
                .append("record.partition(), ").newLine()
                .append("record.key());").revAndNewLine()
                .tab().append(listener.getService().getPropertyName()).append(".apply(record.value());").newLine()
                .end();

        writer.end(); // classEnd
        return writer.toString();
    }
}
