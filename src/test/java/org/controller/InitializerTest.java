package org.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InitializerTest {
    Initializer initializer;
    @BeforeEach
    void setUp() {
        initializer = new Initializer();
    }

    @Test
    void testCase1(){
        initializer.loadMap();
    }

    @Test
    void testCase2(){
        initializer.loadGame();
    }

    @Test
    void testCase3(){
        initializer.saveGame("/dev/null");
    }
    @Test
    void testCase4(){
        initializer.getCurrentSave();
    }
    @Test
    void testCase5(){
        initializer.getCurrentMap();
    }
}