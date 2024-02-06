package ru.vostrodymov.grader.plugin.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import ru.vostrodymov.grader.plugin.command.EntityGenCommand;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilterGenAction extends BaseAction {
    private static final String EXTENSION = "java";

    @Override
    public void update(@NotNull AnActionEvent e) {
        var dataContext = e.getDataContext();
        VirtualFile[] files = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
        if (files != null) {
            final List<VirtualFile> filesList = Arrays.asList(files);
            var flag = filesList.stream().anyMatch(q -> EXTENSION.equals(q.getExtension()));
            e.getPresentation().setEnabled(flag);
        }
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        var dataContext = e.getDataContext();

        VirtualFile[] files = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
        if (files != null) {
            final List<VirtualFile> filesList = Arrays.asList(files);
            var items = filesList.stream()
                    .filter(q -> EXTENSION.equals(q.getExtension()))
                    .collect(Collectors.toSet());


            var command = new EntityGenCommand(project, takeRootDirectory(project, dataContext));
            for (var el : items) {
                command.run(el);
            }
        }
    }

}
