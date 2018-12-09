package f18a14c09s.generation.alexa.music.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AskMusicDocBasedJavaFileGenerator {
    public static void main(String... args) throws IOException {
        inferClasses().entrySet().forEach(clazz -> {
            String className = clazz.getKey();
            String sourceCode = clazz.getValue();
            File testSources = new File(
                    "C:\\Users\\fjohnson\\code\\amazon-integration\\alexa-music-skill-model-4j\\src\\main\\java");
            File packageDir =
                    new File(testSources, className.substring(0, className.lastIndexOf('.')).replaceAll("\\.", "\\\\"));
            packageDir.mkdirs();
            File javaFile =
                    new File(packageDir, String.format("%s.java", className.substring(className.lastIndexOf('.') + 1)));
            try (FileWriter fileWriter = new FileWriter(javaFile)) {
                fileWriter.write(sourceCode);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        });
    }

    public static Map<String, String> inferClasses() throws IOException {
        String implPackageName = "f18a14c09s.integration.alexa.music.data";
        AskMusicDocBasedJavaGenerator generator = new AskMusicDocBasedJavaGenerator();
        AskMusicModelDocAnalyzer.getComponentModel().forEach(classInfo -> {
            generator.addClass(implPackageName, classInfo);
        });
        AskMusicModelDocAnalyzer.getMessageModel().forEach(classInfo -> {
            generator.addClass(implPackageName, classInfo.getRequestInfo());
            generator.addClass(implPackageName, classInfo.getResponseInfo());
        });
        return generator.getSupportingClasses();
    }
}
