package ru.vostrodymov.grader.core.datamodel;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Datamodel {
    private Map<String, ModelDM> models;
    private Map<String, ProcessorDM> processors;
}
