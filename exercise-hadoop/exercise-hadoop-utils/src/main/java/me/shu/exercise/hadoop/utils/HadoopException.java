package me.shu.exercise.hadoop.utils;

/**
 * Created by moshangcheng on 2015/6/14.
 */
public class HadoopException extends Exception {

    public HadoopException() {
        super();
    }

    public HadoopException(String message) {
        super(message);
    }

    public HadoopException(String message, Exception e) {
        super(message, e);
    }

}
