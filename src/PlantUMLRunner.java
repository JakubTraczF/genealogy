import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PlantUMLRunner {
    private static String plantUMLJarPath;

    public static void setPlantUMLJarPath(String path) {
        plantUMLJarPath = path;
    }

    public static void generateDiagram(String umlData, String outputDirectory, String outputFileName) throws IOException, InterruptedException {
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String umlFilePath = outputDirectory + "/" + outputFileName + ".puml";
        try (FileWriter writer = new FileWriter(umlFilePath)) {
            writer.write(umlData);
        }

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", plantUMLJarPath, umlFilePath);
        processBuilder.directory(new File(outputDirectory));
        Process process = processBuilder.start();
        process.waitFor();
    }
}
