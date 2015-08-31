package me.shu.exercise.java.mockito;

import org.mockito.ArgumentMatcher;

import java.util.List;

import static org.mockito.Mockito.*;


/**
 * Created by sheng.hu on 2015/8/31.
 */
public class ArgMatcher {

    public static void main(String[] args) {

        List mockedList = mock(List.class);

        //stubbing using built-in anyInt() argument matcher
        when(mockedList.get(anyInt())).thenReturn("element");

        //following prints "element"
        System.out.println(mockedList.get(999));

        //you can also verify using an argument matcher
        verify(mockedList).get(anyInt());
    }
}
