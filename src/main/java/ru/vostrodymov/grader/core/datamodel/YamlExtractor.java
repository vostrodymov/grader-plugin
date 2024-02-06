package ru.vostrodymov.grader.core.datamodel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public class YamlExtractor {
    private final Pattern MODEL_PATTERN = Pattern.compile("\\$M\\{\\w*}");

    public Datamodel take(String filepath) throws IOException {
        var om = new ObjectMapper(new YAMLFactory());
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        File file = new File(filepath);
        var model = om.readValue(file, Datamodel.class);
        reloadLinks(model);
        return model;
    }

    private void reloadLinks(Datamodel datamodel) {
        var models = datamodel.getModels();
        if (datamodel.getProcessors() != null) {
            for (var el : datamodel.getProcessors().values()) {
                if (el.getKafka() != null) {
                    buildKafka(el.getKafka(), models);
                }
            }
        }
    }

    /**
     * Подстановка моделей в кафка процессоры
     */
    private void buildKafka(KafkaDM el, Map<String, ModelDM> models) {
        if (MODEL_PATTERN.matcher(el.getRecordKey().getFullName()).find()) {
            el.setRecordKey(takeClass(el.getRecordKey().getFullName(), models));
        }
        if (MODEL_PATTERN.matcher(el.getRecordValue().getFullName()).find()) {
            el.setRecordValue(takeClass(el.getRecordValue().getFullName(), models));
        }
    }

    private ClassDM takeClass(String link, Map<String, ModelDM> records) {
        var key = link.substring(3);
        key = key.substring(0, key.length() - 1);
        return records.get(key).getClazz();
    }

}
