package it.unicam.gutbrain;

import org.jspace.SequentialSpace;
import org.jspace.Space;

public class Main {
    public static void main(String[] argv) throws InterruptedException {
        Space space = new SequentialSpace();
        Master master = new Master(space);

        for (int i = 0; i < 5; i++) {
            space.put("CREATE", "AEP");
        }


        for (int i = 0; i < 50; i++) {
            space.put("PROTEIN");
        }

        master.run();

    }
}
