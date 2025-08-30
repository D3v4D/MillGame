package org.controller;

import org.view.swing.SwingComponentGenerator;

import java.util.concurrent.Executors;

public class MillGame {
    public static void main(String[] args) {
        new Initializer(new SwingComponentGenerator(Executors.newFixedThreadPool(2)));
    }
}