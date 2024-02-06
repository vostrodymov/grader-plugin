package ru.vostrodymov.grader.plugin.action;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiDirectoryImpl;
import org.jetbrains.annotations.NotNull;
import ru.vostrodymov.grader.core.datamodel.ModelDM;
import ru.vostrodymov.grader.core.datamodel.PropertyDM;
import ru.vostrodymov.grader.core.datamodel.types.ClassDM;
import ru.vostrodymov.grader.core.generator.ModelGenerator;
import ru.vostrodymov.grader.plugin.command.GenerateCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ToolbarAction extends AnAction {
    private static final String EXTENSION = "gr";

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

            var command = new GenerateCommand(project, takeRootDirectory(project, dataContext));
            for (var el : items) {
                try {
                    command.run(el.getPath());
                } catch (Exception ex) {
                    throw new RuntimeException("Ошибка генерации класса", ex);
                }
            }
        }
    }

    private PsiDirectory takeRootDirectory(Project project, DataContext dataContext) {
        var vfile = dataContext.getData(CommonDataKeys.VIRTUAL_FILE);
        if (project == null || vfile == null) {
            System.out.println("Ошибка, не нйден файл иои проект");
            throw new RuntimeException("Ошибка, не нйден файл иои проект");
        }

        ModuleManager manager = ModuleManager.getInstance(project);
        com.intellij.openapi.module.Module[] modules = manager.getModules();
        var pattern = Pattern.compile(".*\\.main");
        var patter2 = Pattern.compile("src/main/java");
        AtomicReference<PsiDirectory> rootDir = new AtomicReference<>();
        for (com.intellij.openapi.module.Module module : modules) {
            ModuleRootManager root = ModuleRootManager.getInstance(module);
            if (pattern.matcher(root.getModule().getName()).find()) {
                for (VirtualFile file : root.getSourceRoots()) {
                    System.out.println(file.getFileType().getName() + "   " + file);
                    if (patter2.matcher(file.getPath()).find()) {
                        rootDir.set(new PsiDirectoryImpl(((PsiManagerImpl) PsiManager.getInstance(project)), file));
                    }
                }
            }
        }
        return rootDir.get();
    }

    private void run(AnActionEvent e) {
        var project = e.getProject();
        var vfile = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);

//        Language lang = e.getData(CommonDataKeys.PSI_FILE).getLanguage();
//        String languageTag = "+[" + lang.getDisplayName().toLowerCase() + "]";
//        System.out.println(languageTag);

        if (project == null || vfile == null) {
            System.out.println("Ошибка, не нйден файл иои проект");
            return;
        }

        ModuleManager manager = ModuleManager.getInstance(e.getProject());
        com.intellij.openapi.module.Module[] modules = manager.getModules();
        var pattern = Pattern.compile(".*\\.main");
        var patter2 = Pattern.compile("src/main/java");
        AtomicReference<PsiDirectory> rootDir = new AtomicReference<>();
        for (com.intellij.openapi.module.Module module : modules) {

            ModuleRootManager root = ModuleRootManager.getInstance(module);

            if (pattern.matcher(root.getModule().getName()).find()) {


                for (VirtualFile file : root.getSourceRoots()) {
                    System.out.println(file.getFileType().getName() + "   " + file);
                    if (patter2.matcher(file.getPath()).find()) {
                        rootDir.set(new PsiDirectoryImpl(((PsiManagerImpl) PsiManager.getInstance(project)), file));
                    }
                }
            }
        }


        var vdirectory = new PsiDirectoryImpl(((PsiManagerImpl) PsiManager.getInstance(project)), vfile.getParent());


        var psiFileFactory = PsiFileFactory.getInstance(project);
//        PsiFile file = psiFileFactory.createFileFromText("readme.md", PlainTextFileType.INSTANCE, "testing");


        var psiDocumentManager = PsiDocumentManager.getInstance(project);

//        var jfile = new com.intellij.psi.impl.PsiJavaParserFacadeImpl(project);
//        var type = jfile.createTypeFromText("StringUTL", file);


//        var r = QuickFixFactory.getInstance().createCreateClassOrInterfaceFix(file.getContext(), "User", true, null);
        JavaDirectoryService directoryService = JavaDirectoryService.getInstance();
//        final PsiPackage packageElement = directoryService.getPackage(vdirectory);
//        final PsiFile classFile = psiFileFactory.createFileFromText("User", JavaFileType.INSTANCE, "class a {}");
//        CodeStyleManager.getInstance(classFile.getProject()).reformat(classFile);
//        JavaCodeStyleManager.getInstance(classFile.getProject()).optimizeImports(classFile);
//        final PsiFile created = (PsiFile) vdirectory.add(classFile);
//        var document = psiDocumentManager.getDocument((PsiFile) created);
//        psiDocumentManager.commitDocument(document);

//        var savedPsiFile = vdirectory.add(file);
//        var document = psiDocumentManager.getDocument((PsiFile) savedPsiFile);
//        psiDocumentManager.commitDocument(document);


        final PsiPackage packageElement = directoryService.getPackage(rootDir.get());
        if (packageElement == null) {
            throw new RuntimeException("Target directory does not provide a package");
        }

//        final String fileName = Extensions.append(name, JavaFileType.INSTANCE);
//        final PsiFile found = directory.findFile(fileName);
//        if (found != null) {
//            throw new ClassAlreadyExistsException("Class '" + name + "'already exists in " + packageElement.getName());
//        }


        CommandProcessor.getInstance().executeCommand(project, () -> {
            final String packageName = packageElement.getQualifiedName();
            final String className = "ru.vostrodymov.grader";
            final String fileName = "User.java";
            try {
                final var packDirectory = takeDirectoryForPackage(rootDir.get(), className);
                final String java = takeUserCode();

                final PsiFile classFile = psiFileFactory.createFileFromText(fileName, JavaFileType.INSTANCE, java);
                CodeStyleManager.getInstance(classFile.getProject()).reformat(classFile);
                JavaCodeStyleManager.getInstance(classFile.getProject()).optimizeImports(classFile);

                final PsiFile created = (PsiFile) packDirectory.add(classFile);
                var document = psiDocumentManager.getDocument(created);
                psiDocumentManager.commitDocument(document);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create new class from JSON", ex);
            }
        }, "namr", "ru.namer");


//        Path path = Path.of(Objects.requireNonNull(project.getBasePath()), "readme.md");
//        Files.write(path, "test".getBytes());


//        var psiDirectory = new PsiDirectoryImpl(((PsiManagerImpl)PsiManager.getInstance(e.getProject())) , vfile);
//        var savedPsiFile = psiDirectory.add()
//        val psiDocumentManager = PsiDocumentManager.getInstance(project)
//        val document = psiDocumentManager.getDocument(savedPsiFile as PsiFile)
//        psiDocumentManager.commitDocument(document!!)

        System.out.println("RUN GrCodegen " + vfile.getPath() + "  " + e.getProject().getPresentableUrl());
    }


    private String takeUserCode() {
        var writer = new ModelGenerator();
        var modelDM = new ModelDM();
        modelDM.setProperties(Map.of(
                "id", new PropertyDM(new ClassDM("java.lang", "String"), null),
                "name", new PropertyDM(new ClassDM("java.lang", "String"), null)));
        modelDM.setClazz(new ClassDM("ru.vostrodymov.grader", "User"));
//        modelDM.setProperties();
        return writer.run(modelDM);
    }

    private PsiDirectory takeDirectoryForPackage(PsiDirectory root, String pack) {
        var dirs = pack.split("\\.");
        var parent = root;
        for (var dir : dirs) {
            var current = parent.findSubdirectory(dir);
            if (current == null) {
                current = parent.createSubdirectory(dir);
            }
            parent = current;
        }
        return parent;
    }


}
