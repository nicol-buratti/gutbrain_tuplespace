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

    @SneakyThrows
    public CleavedProteinAgent(Space space, ProteinType proteinType) {
        this.space = space;
        this.proteinType = proteinType;
        Object[] tuple = this.space.getp(new ActualField("PROTEIN"), new ActualField(proteinType),
                new ActualField(ProteinStatus.CLEAVED), new FormalField(Integer.class));
        if (tuple == null)
            this.space.put("PROTEIN", proteinType, ProteinStatus.CLEAVED, 0);
        else
            this.space.put("PROTEIN", proteinType, ProteinStatus.CLEAVED, (int) tuple[3] + 1);
    }

    @Override
    @SneakyThrows
    public void run() {
        Random random = new Random();
        while (true) {
            int size = random.nextInt(3) + 2; // range 2 - 5
            int i = 0;
            while (i < size) {
                Object[] protein = space.get(new ActualField("PROTEIN"), new ActualField(proteinType),
                        new ActualField(ProteinStatus.CLEAVED), new FormalField(Integer.class));
                if ((int) protein[3] == 0) {
                    space.put(protein[0], protein[1], protein[2], protein[3]);
                    continue;
                }
                space.put(protein[0], protein[1], protein[2], (int) protein[3] - 1);
                i++;
            }

            // get oligomers counter tuple and update it
            Object[] tuple = space.get(new ActualField("OLIGOMER"), new ActualField(this.proteinType), new FormalField(Integer.class));
            space.put(tuple[0], tuple[1], (int) tuple[2] + 1);
            logger.info("Creato Oligomer" + Arrays.toString(tuple));
        }

    }
}
