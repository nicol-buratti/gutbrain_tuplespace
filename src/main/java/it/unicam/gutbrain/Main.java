package it.unicam.gutbrain;

import it.unicam.gutbrain.gut.Master;
import it.unicam.gutbrain.gut.ProteinStatus;
import it.unicam.gutbrain.gut.ProteinType;
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

        space.put("PROTEIN", ProteinType.ALPHA, ProteinStatus.NORMAL, 50);


        space.put("PROTEIN", ProteinType.TAU, ProteinStatus.NORMAL, 50);


        master.run();

    }
}
