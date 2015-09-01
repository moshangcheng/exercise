package me.shu.exercise.hadoop.utils;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by moshangcheng on 2015/6/14.
 */
public class Utils {

    public static void initializeHadoop() throws HadoopException {

        String hdfsRoot = System.getProperty("user.dir") + "/hdfs-root";
        String hiveRoot = hdfsRoot + "/hive";

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                String hadoopPath = null;
                if (System.getProperty("sun.arch.data.model").equalsIgnoreCase("32")) {
                    hadoopPath = getFileInClasspath("Win32");
                } else {
                    hadoopPath = getFileInClasspath("Win64");
                }
                System.setProperty("hadoop.home.dir", hadoopPath);
                System.setProperty("hadoop.tmp.dir", hdfsRoot + "/tmp");
                System.setProperty("fs.file.impl", "me.shu.exercise.hadoop.utils.WinLocalFileSystem");
                System.setProperty("java.library.path", hadoopPath + File.separator + "bin;" + System.getProperty("java.library.path"));
                System.load(hadoopPath + File.separator + "bin" + File.separator + "hadoop.dll");


                System.setProperty("hive.metastore.warehouse.dir", hiveRoot + "/warehouse");
                System.setProperty("hive.exec.scratchdir", hiveRoot + "/scratchdir");
                System.setProperty("javax.jdo.option.ConnectionURL", "jdbc:derby:;databaseName=" + hiveRoot + "/metadb;create=true");

            } catch (URISyntaxException e) {
                throw new HadoopException("Failed to initialize hadoop", e);
            }
        }
    }

    public static String getFileInClasspath(String path) throws URISyntaxException {
        return new File(Utils.class.getClassLoader().getResource(path).toURI()).getAbsolutePath();
    }
}
