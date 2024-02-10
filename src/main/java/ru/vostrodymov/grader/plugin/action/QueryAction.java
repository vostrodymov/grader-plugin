package ru.vostrodymov.grader.plugin.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;
import ru.vostrodymov.grader.plugin.action.base.FromModelAction;
import ru.vostrodymov.grader.plugin.command.QueryCommand;

import java.util.Set;

public class QueryAction extends FromModelAction {

    @Override
    protected void apply(AnActionEvent e, Set<VirtualFile> items) {
        var project = e.getProject();
        var dataContext = e.getDataContext();
        var command = new QueryCommand(project, takeRootDirectory(project, dataContext));
        for (var el : items) {
            command.run(el);
        }
    }
}
