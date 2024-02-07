package ru.vostrodymov.grader.core.props;

import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

public class GraderProperties {
    private static final String PROJECT_FILE_CONFIG_NAME = "grader.properties";

    private ResourceBundle defProps;
    private Properties prjProps;

    public GraderProperties(Project project) {
        this.prjProps = new Properties();
        this.defProps = ResourceBundle.getBundle("grader");
        loadProjProperties(project);
    }

    private void loadProjProperties(Project project) {
        var file = new File(project.getBasePath(), PROJECT_FILE_CONFIG_NAME);
        if (file.exists()) {
            try {
                prjProps.load(new FileInputStream(file));
            } catch (Exception e) {
                throw new RuntimeException("Ошибка чтения конфигурационного файла " + PROJECT_FILE_CONFIG_NAME, e);
            }
        }
    }

    public String get(String key) {
        var result = prjProps.getProperty(key);
        return Optional.ofNullable(result)
                .orElseGet(() -> defProps.getString(key));
    }

}
