package it.unicam.gutbrain.gut;

import lombok.SneakyThrows;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.Arrays;
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
    @SneakyThrows
    public void run() {
        Random random = new Random();
        int size = random.nextInt(3) + 2; // range 2 - 5

        // checks if it should be destroyed
        Object[] destroy = space.getp(new ActualField("DESTROY"), new ActualField(proteinType), new ActualField(ProteinStatus.CLEAVED), new FormalField(Integer.class));
        if (destroy == null)
            space.put("DESTROY", proteinType, ProteinStatus.CLEAVED, 0);
        else if ((int) destroy[3] > 0) {
            space.put(destroy[0], destroy[1], destroy[2], (int) destroy[3] - 1);
            return;
        } else {
            space.put(destroy[0], destroy[1], destroy[2], destroy[3]);
        }


        while (true) {
            Object[] cleavedProteins = space.get(
                    new ActualField("PROTEIN"),
                    new ActualField(proteinType),
                    new ActualField(ProteinStatus.CLEAVED),
                    new FormalField(Integer.class));
            // creates the oligomer
            if ((int) cleavedProteins[3] >= size) {
                space.put(cleavedProteins[0], cleavedProteins[1], cleavedProteins[2], (int) cleavedProteins[3] - size);
                Object[] oligomers = space.get(new ActualField("OLIGOMER"), new ActualField(proteinType), new FormalField(Integer.class));
                space.put(oligomers[0], oligomers[1], (int) oligomers[2] + 1);
                logger.info("Oligomer created: " + Arrays.toString(oligomers));

                Object[] destroyTuple = space.get(new ActualField("DESTROY"), new ActualField(proteinType), new ActualField(ProteinStatus.CLEAVED), new FormalField(Integer.class));
                space.put(destroyTuple[0], destroyTuple[1], destroyTuple[2], (int) destroyTuple[3] + size);
                break;
            }
            // protein failed the creation and check if it should be destroyed
            space.put(cleavedProteins[0], cleavedProteins[1], cleavedProteins[2], cleavedProteins[3]);
            Object[] destroyTuple = space.get(new ActualField("DESTROY"), new ActualField(proteinType), new ActualField(ProteinStatus.CLEAVED), new FormalField(Integer.class));
            if ((int) destroyTuple[3] > 0) {
                space.put(destroyTuple[0], destroyTuple[1], destroyTuple[2], (int) destroyTuple[3] - 1);
                break;
            }
            space.put(destroyTuple[0], destroyTuple[1], destroyTuple[2], destroyTuple[3]);
        }
    }
}
