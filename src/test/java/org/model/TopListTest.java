package org.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TopListTest {
    TopList topList;
    @BeforeEach
    void setUp() {
        topList = new TopList();
        try {

            topList.addPlayer("Steve", 64);
        }catch (Exception e){

        }
    }

    @Test
    void getList() {
        assertEquals("Steve", topList.getList().get(0).getName());
        assertEquals(64, topList.getList().get(0).getScore());
    }

    @Test
    void addWin() {
        topList.addWin("Steve");
        assertEquals("Steve", topList.getList().get(0).getName());
        assertEquals(65, topList.getList().get(0).getScore());
    }

    @Test
    void addPlayer() {
        try{
            topList.addPlayer("Alex", 32);
            assertEquals("Alex", topList.getList().get(1).getName());
            assertEquals(32, topList.getList().get(1).getScore());
            assertThrows(Exception.class, () -> topList.addPlayer("Steve", 69));
        }catch (Exception e){

        }

    }
}