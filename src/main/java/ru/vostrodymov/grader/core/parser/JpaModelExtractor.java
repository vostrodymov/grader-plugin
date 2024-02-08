package ru.vostrodymov.grader.core.parser;

import com.intellij.psi.PsiClass;
import com.intellij.psi.util.PsiTypesUtil;
import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.datamodel.PropertyDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;

import java.util.*;

public class JpaModelExtractor {
    private static final String COLUMN_ID_ANN = "javax.persistence.Id";
    private static final String COLUMN_PROP_ANN = "javax.persistence.Column";
    private final Set<String> navAnnotations = Set.of("javax.persistence.OneToMany", "javax.persistence.ManyToOne");
    private final Set<String> propAnnotations = Set.of(COLUMN_PROP_ANN, COLUMN_ID_ANN);

    public ModelDM take(String packageName, PsiClass clazz) {
        ModelDM model = new ModelDM();
        model.setClazz(new ClassDM(packageName, clazz.getName()));
        model.setProperties(new HashMap<>());
        model.setProperties(takeProperties(clazz));
        return model;
    }

    public Map<String, PropertyDM> takeProperties(PsiClass clazz) {
        var map = new HashMap<String, PropertyDM>();
        for (var fel : clazz.getAllFields()) {
            var prop = new PropertyDM(new ClassDM(fel.getType().getCanonicalText()), null);

            Optional.of(fel.getAnnotations()).map(Arrays::stream)
                    .filter(q -> q.anyMatch(r -> r.hasQualifiedName(COLUMN_ID_ANN)))
                    .ifPresent(q -> prop.setIdentifier(true));

            if (Arrays.stream(fel.getAnnotations()).anyMatch(q -> navAnnotations.contains(q.getQualifiedName()))) {
                var fieldClass = PsiTypesUtil.getPsiClass(fel.getType());
                var innerProps = takeProperties(fieldClass);
                prop.setProperties(innerProps);
            }

            if (prop.isObject() ||
                    Arrays.stream(fel.getAnnotations()).anyMatch(q -> propAnnotations.contains(q.getQualifiedName()))) {
                map.put(fel.getName(), prop);
            }
        }
        return map;
    }

}
