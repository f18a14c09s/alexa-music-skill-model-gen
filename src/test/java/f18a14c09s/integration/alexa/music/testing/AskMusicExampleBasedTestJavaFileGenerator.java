package f18a14c09s.integration.alexa.music.testing;

import f18a14c09s.generation.alexa.music.data.AskMusicModelDocAnalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AskMusicExampleBasedTestJavaFileGenerator {
    public static void main(String... args) throws IOException {
//        inferClasses().entrySet().forEach(clazz -> System.out.printf("%s:%n\t%s%n", clazz.getKey(), clazz.getValue()));
        inferClasses().entrySet().forEach(clazz -> {
            String className = clazz.getKey();
            String sourceCode = clazz.getValue();
            File testSources = new File(
                    "C:\\Users\\fjohnson\\code\\amazon-integration\\alexa-music-skill-model-4j\\src\\test\\java");
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
        Map<String, String> retval = new HashMap<>();
        String packageName = "f18a14c09s.integration.alexa.music.data";
        AskMusicModelDocAnalyzer.getComponentModel().forEach(classInfo -> {
            try {
                retval.putAll(new AskMusicExampleBasedTestJavaGenerator().generateTestClasses(packageName,
                        classInfo));
                retval.putAll(new AskMusicExampleBasedTestJavaGenerator().generateTestClasses(packageName,
                        classInfo));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        });
        AskMusicModelDocAnalyzer.getMessageModel().forEach(messageInfo -> {
            try {
                retval.putAll(new AskMusicExampleBasedTestJavaGenerator().generateTestClasses(packageName,
                        messageInfo.getRequestInfo()));
                retval.putAll(new AskMusicExampleBasedTestJavaGenerator().generateTestClasses(packageName,
                        messageInfo.getResponseInfo()));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        });
        return retval;
    }
}
