package ru.vostrodymov.grader.core;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import ru.vostrodymov.grader.core.datamodel.YamlExtractor;

import java.io.IOException;

public class ExtractorTest {

    @Test
    public void yamlTest() throws IOException {
        var extractor = new YamlExtractor();
        var model = extractor.take(this.getClass().getResource("/bills.gr").getFile());
        Assert.assertNotNull(model);
    }

}
