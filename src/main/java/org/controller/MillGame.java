package org.controller;

import org.view.InitGameScreen;
import org.view.MainMenuScreen;
import org.view.base.ComponentGenerator;
import org.view.swing.SwingComponentGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MillGame {
    public static void main(String[] args) {
        ComponentGenerator componentGenerator = new SwingComponentGenerator();

        ExecutorService backendPool = Executors.newFixedThreadPool(4);

        Initializer initializer = new Initializer(componentGenerator, backendPool);
    }
}