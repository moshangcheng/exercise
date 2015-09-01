package me.shu.exercise.hadoop.hive;


import me.shu.exercise.hadoop.utils.Utils;

import java.sql.*;

/**
 * Created by sheng.hu on 2015/9/1.
 */
public class EmbeddedHive {

    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args) throws Exception {

        Utils.initializeHadoop();

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }

        // create embedded hive connection
        Connection conn = DriverManager.getConnection("jdbc:hive2://", "", "");

        Statement stmt = conn.createStatement();

        stmt.execute("create database if not exists local_test");
        stmt.execute("use local_test");
        stmt.execute("create table if not exists normal_table (id int, lang string) row format delimited fields terminated by ' '");
        stmt.execute(String.format("load data local inpath '%s' overwrite into table normal_table", Utils.getFileInClasspath("normal_table.csv")));

        stmt.close();
        conn.close();
    }
}

