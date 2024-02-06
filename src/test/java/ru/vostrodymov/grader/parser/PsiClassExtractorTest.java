package ru.vostrodymov.grader.parser;

import org.junit.jupiter.api.Test;
import ru.vostrodymov.grader.core.generator.ModelFilterBuilderGenerator;

public class PsiClassExtractorTest {

    @Test
    public void testBreadcrumbs() {
        var breadcrumbs1 = new ModelFilterBuilderGenerator.Breadcrumbs("qUnitEntity");
        var breadcrumbs2 = new ModelFilterBuilderGenerator.Breadcrumbs("value", breadcrumbs1);
        var breadcrumbs3 = new ModelFilterBuilderGenerator.Breadcrumbs("id", breadcrumbs2);

        System.out.println(breadcrumbs3.getPath("_"));
    }

}
