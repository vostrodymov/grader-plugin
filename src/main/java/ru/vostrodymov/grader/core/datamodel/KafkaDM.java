package ru.vostrodymov.grader.core.datamodel;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;

@Getter
@Setter
public class KafkaDM {
    @JsonAlias("listener")
    private ClassDM listener;
    @JsonAlias("service")
    private ClassDM service;
    @JsonAlias("key")
    private ClassDM recordKey;
    @JsonAlias("value")
    private ClassDM recordValue;
}
