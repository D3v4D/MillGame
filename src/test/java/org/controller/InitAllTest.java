package org.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InitAllTest {
    InitAll initAll;
    @BeforeEach
    void setUp() {
        initAll = new InitAll();
    }

    @Test
    void testCase1(){
        initAll.loadMap();
    }

    @Test
    void testCase2(){
        initAll.loadGame();
    }

    @Test
    void testCase3(){
        initAll.saveGame("/dev/null");
    }
    @Test
    void testCase4(){
        initAll.getCurrentSave();
    }
    @Test
    void testCase5(){
        initAll.getCurrentMap();
    }
}