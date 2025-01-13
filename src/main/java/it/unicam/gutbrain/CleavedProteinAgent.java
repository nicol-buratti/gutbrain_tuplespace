package it.unicam.gutbrain;

import lombok.SneakyThrows;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.Random;
import java.util.logging.Logger;

public class CleavedProteinAgent implements Runnable {

    private static final Logger logger = Logger.getLogger(CleavedProteinAgent.class.getName());
    private final Space space;
    private final ProteinType proteinType;

    @SneakyThrows
    public CleavedProteinAgent(Space space, ProteinType proteinType) {
        this.space = space;
        this.proteinType = proteinType;
        Object[] tuple = this.space.getp(new ActualField("PROTEIN"), new ActualField(proteinType), new FormalField(Integer.class));
        if (tuple == null)
            this.space.put("PROTEIN", proteinType, 0);
        else
            this.space.put("PROTEIN", proteinType, (int) tuple[2] + 1);
    }

    @Override
    @SneakyThrows
    public void run() {
        String proteinName = this.proteinType == ProteinType.ALPHA
                ? "CLEAVED_ALPHA_PROTEIN"
                : "CLEAVED_TAU_PROTEIN";
        Random random = new Random();
        while (true) {
            int size = random.nextInt(3) + 2; // range 2 - 5
            for (int i = 0; i < size; i++)
                space.get(new ActualField(proteinName));

            // get oligomers counter tuple and update it
            Object[] tuple = space.get(new ActualField("OLIGOMERS"), new ActualField(this.proteinType), new FormalField(Integer.class));
            tuple[2] = (int) tuple[2] + 1;
            space.put(tuple[0], tuple[1], tuple[2]);
            logger.info("Creato Oligomer");
        }

    }
}
