package it.unicam.gutbrain;

import org.jspace.ActualField;
import org.jspace.Space;

import java.util.Random;
import java.util.logging.Logger;

public class CleavedProteinAgent implements Runnable {

    private static final Logger logger = Logger.getLogger(CleavedProteinAgent.class.getName());
    private final Space space;
    private final ProteinType proteinType;

    public CleavedProteinAgent(Space space, ProteinType proteinType) {
        this.space = space;
        this.proteinType = proteinType;
    }

    @Override
    public void run() {
        String proteinName = this.proteinType == ProteinType.ALPHA
                ? "CLEAVED_ALPHA_PROTEIN"
                : "CLEAVED_TAU_PROTEIN";

        String oligomerName = this.proteinType == ProteinType.ALPHA
                ? "ALPHA_OLIGOMER"
                : "TAU_OLIGOMER";

        try {
            while (true) {
                // TODO add random number protein aggregations
                int size = new Random().nextInt(3) + 2; // range 2 - 5
                for (int i = 0; i < size; i++)
                    space.get(new ActualField(proteinName));

                space.put("CREATE", oligomerName);
                logger.info("Creato Oligomer");
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
