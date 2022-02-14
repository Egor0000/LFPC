package md.utm.isa.utils;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

public class FileUtil {

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

        File file = new File(pathName);

        return file;
    }
}
