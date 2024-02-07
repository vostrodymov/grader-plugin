package ru.vostrodymov.grader.plugin.action.base;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Базовы класс акшина для выполнения действий над .java файлами
 */
public abstract class FromModelAction extends BaseAction {
    private static final String EXTENSION = "java";

    @Override
    public void update(@NotNull AnActionEvent e) {
        var dataContext = e.getDataContext();
        VirtualFile[] files = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
        if (files != null) {
            final List<VirtualFile> filesList = Arrays.asList(files);
            var flag = filesList.stream().anyMatch(q -> EXTENSION.equals(q.getExtension()));
            e.getPresentation().setEnabled(flag);
            e.getPresentation().setVisible(flag);
        } else {
            e.getPresentation().setVisible(false);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var dataContext = e.getDataContext();
        VirtualFile[] files = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
        if (files != null) {
            final List<VirtualFile> filesList = Arrays.asList(files);
            var items = filesList.stream()
                    .filter(q -> EXTENSION.equals(q.getExtension()))
                    .collect(Collectors.toSet());

            apply(e, items);
        }
    }

    protected abstract void apply(AnActionEvent e, Set<VirtualFile> items);
}
