package md.utm.isa.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileUtil {

    /**
     * Input : String representing file path from resources. File path is taken in reference to resources package
     * Output: File object of input path
     */
    static public File getFileFromResources(String filePath) throws Exception{
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        URL url = classLoader.getResource(filePath);

        if (url == null){
            throw new Exception("URL not found");
        }

        String pathName = classLoader.getResource(filePath).getFile();

        if (pathName==null) {
            throw new Exception("PathName not found");
        }

        return new File(pathName);
    }

    static public void writeToFile(String filePath, String output) throws Exception{
        File targetFile = new File(filePath);

        targetFile.delete();

        Path newFilePath = Paths.get(filePath);
        File outputFile = Files.createFile(newFilePath).toFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.append(output);

        writer.close();
    }
}
