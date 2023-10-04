package main.java.test;

import main.java.greeter.Greeter;

import org.joda.time.LocalTime;

public class TestGradle {
    public static void main(String[] args) {
        System.out.println( "Current local time: " + (new LocalTime()) );

        Greeter.greet("Trung Dep Trai");
    }
}