package org.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentStackTest {
    ConcurrentStack<Integer> pressed;
    @BeforeEach
    void setUp() {
        pressed = new ConcurrentStack<>();
        pressed.list = new ArrayList<Integer>(){{
           add(1);
           add(5);
           add(9);
        }};
    }

    @Test
    void reload() {
        pressed.reload();
        try {

            assertEquals(1, pressed.pop());
            assertEquals(5, pressed.pop());
            assertEquals(9, pressed.pop());

        }
        catch (Exception e){

        }
    }

    @Test
    void clear() {
        pressed.clear();
        assertEquals(new ArrayList<Integer>(), pressed.list);
    }

    @Test
    void push() {
        pressed.push(3);
        try {
            assertEquals(3,pressed.pop());
        }
        catch (Exception e){

        }
    }

    @Test
    void pop() {
        pressed.push(9);
        try {
            assertEquals(9,pressed.pop());
            pressed.push(-69);
            assertThrows(InterruptedException.class, ()->pressed.pop());
            pressed.push(-420);
            assertThrows(InterruptedException.class, ()->pressed.pop());
        }
        catch (Exception e){

        }
    }

    @Test
    void getLatest() {
        try{
            pressed.push(3);
            pressed.pop();
            assertEquals(3, pressed.getLatest());
        }catch (Exception e){

        }
    }
}