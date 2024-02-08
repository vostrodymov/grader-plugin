package ru.vostrodymov.grader.write;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import ru.vostrodymov.grader.core.datamodel.YamlExtractor;
import ru.vostrodymov.grader.core.generator.ModelGenerator;
import ru.vostrodymov.grader.core.write.ClassWriter;

import java.io.IOException;

public class ClassWriterTest {

    @Test
    public void write() {
        var writer = new ClassWriter();
        writer.writePackage("ru.vostrodymov.grader");
        Assert.assertEquals(writer.toString(), "package ru.vostrodymov.grader;"
                + System.lineSeparator() + System.lineSeparator());
    }

    @Test
    public void modelTest() throws IOException {
        var writer = new ModelGenerator();
        var yamlExtractor = new YamlExtractor();
        var modelDM = yamlExtractor.take(this.getClass().getResource("/bills.gr").getFile());
        System.out.println(writer.run(modelDM.getModels().get("bill"), null));
    }

}
