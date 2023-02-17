package gg.bear;


import gg.bear.modules.Leap;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {

        Leap.Initialize();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Leap.Destroy();
    }

}
