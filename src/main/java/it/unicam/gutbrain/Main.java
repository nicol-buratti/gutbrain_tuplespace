package it.unicam.gutbrain;

import org.jspace.SequentialSpace;
import org.jspace.Space;

public class Main {
    public static void main(String[] argv) throws InterruptedException {
        Space space = new SequentialSpace();
        space.put("GUT", 0); // impermeability
        //space.put("BRAIN")

        Master master = new Master(space);

        for (int i = 0; i < 5; i++) {
            space.put("CREATE", "AEP");
        }


        for (int i = 0; i < 50; i++) {
            space.put("PROTEIN", ProteinType.ALPHA, ProteinStatus.NORMAL, 0);
        }

        master.run();

    }
}
