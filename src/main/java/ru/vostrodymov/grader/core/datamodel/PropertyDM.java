package ru.vostrodymov.grader.core.datamodel;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class PropertyDM {
    /**
     * Тип
     */
    @JsonAlias("class")
    private ClassDM clazz;
    /**
     * Описание
     */
    @JsonAlias("description")
    private String description;

    private Map<String, PropertyDM> properties;

    public PropertyDM(ClassDM clazz, String description) {
        this.clazz = clazz;
        this.description = description;
    }

    public boolean isObject() {
        return Optional.ofNullable(properties)
                .map(q -> !q.isEmpty())
                .orElse(false);
    }
}
