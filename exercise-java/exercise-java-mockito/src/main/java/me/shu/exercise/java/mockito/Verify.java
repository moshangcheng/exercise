package me.shu.exercise.java.mockito;

import java.util.List;

import static org.mockito.Mockito.*;

public class Verify {

    public static void main(String[] args) {

        // mock creation
        List mockedList = mock(List.class);

        // using mock object - it does not throw any "unexpected interaction" exception
        mockedList.add("one");
        mockedList.clear();

        // selective, explicit, highly readable verification
        verify(mockedList).add("one");
        verify(mockedList).clear();
    }
}
