package ru.vostrodymov.grader.core.datamodel;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ModelDM {
    @JsonAlias("class")
    private ClassDM clazz;
    private Map<String, PropertyDM> properties;
}
