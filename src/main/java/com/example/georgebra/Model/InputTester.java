package com.example.georgebra.Model;

import java.util.Scanner;

public class InputTester {
    public static void main(String[] args) {
        String text;
        MetroSystem msys;

        Scanner s = new Scanner(System.in);
        s.useDelimiter("\\A"); //start

        if (s.hasNext()) {
            text = s.next();
            System.out.println("input text:\n" + text);

            InputReader reader = new InputReader("text");
            msys = reader.getMetroSystem();

            //TODO: print msys? etc
        } else System.out.println("EMPTY");

    }
}
