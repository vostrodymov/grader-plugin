package ru.vostrodymov.grader.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;
import ru.vostrodymov.grader.core.generator.JavaGenerator;
import ru.vostrodymov.grader.core.generator.types.JavaCode;
import ru.vostrodymov.grader.core.props.GraderProperties;

public class JavaGeneratorTest {

    @Test
    public void packageMaskTest() {
        final var generator = new TestJavaGenerator();
        var classDm = generator.getClassDm();
        Assertions.assertEquals(classDm.getPack(), "ru.vostrodymov.grader.query");
    }

    private class TestJavaGenerator extends JavaGenerator<ModelDM> {

        @Override
        public JavaCode run(ModelDM data, GraderProperties props) {
            return null;
        }

        public ClassDM getClassDm() {
            return super.getClassDm("TestJavaQuery", "ru.vostrodymov.grader.model", "../query");
        }
    }
}
