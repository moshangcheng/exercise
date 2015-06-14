package me.shu.exercise.hadoop.utils;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by moshangcheng on 2015/6/14.
 */
public class Utils {

    public static void initializeHadoop() throws HadoopException {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                String hadoopPath = null;
                if (System.getProperty("sun.arch.data.model").equalsIgnoreCase("32")) {
                    hadoopPath = getFileInClasspath("Win32");
                } else {
                    hadoopPath = getFileInClasspath("Win64");
                }
                System.setProperty("hadoop.home.dir", hadoopPath);
                System.setProperty("hadoop.tmp.dir", "D:/data/hadoop-tmp-dir");
                System.setProperty("java.library.path", hadoopPath + File.separator + "bin;" + System.getProperty("java.library.path"));
                System.load(hadoopPath + File.separator + "bin" + File.separator + "hadoop.dll");
            } catch (URISyntaxException e) {
                throw new HadoopException("Failed to initialize hadoop", e);
            }
        }
    }

    public static String getFileInClasspath(String path) throws URISyntaxException {
        return new File(Utils.class.getClassLoader().getResource(path).toURI()).getAbsolutePath();
    }
}
