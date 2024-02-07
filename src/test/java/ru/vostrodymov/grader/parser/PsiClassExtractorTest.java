package ru.vostrodymov.grader.parser;

import org.junit.jupiter.api.Test;
import ru.vostrodymov.grader.core.generator.types.Breadcrumbs;

public class PsiClassExtractorTest {

    @Test
    public void testBreadcrumbs() {
        var breadcrumbs1 = new Breadcrumbs("qUnitEntity");
        var breadcrumbs2 = new Breadcrumbs("value", breadcrumbs1);
        var breadcrumbs3 = new Breadcrumbs("id", breadcrumbs2);

        System.out.println(breadcrumbs3.getPath("_"));
    }

}
