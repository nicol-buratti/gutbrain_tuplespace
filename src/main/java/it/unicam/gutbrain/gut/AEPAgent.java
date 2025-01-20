package it.unicam.gutbrain.gut;

import lombok.SneakyThrows;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.Arrays;
import java.util.logging.Logger;

public class AEPAgent implements Runnable {

    private static final Logger logger = Logger.getLogger(AEPAgent.class.getName());
    private final Space space;
    private AEPState state;

    @SneakyThrows
    public AEPAgent(Space space) {
        this.space = space;
        this.state = AEPState.ACTIVE;
    }

    @Override
    @SneakyThrows
    public void run() {
        while (true) {
            changeAEPState();
            if (this.state == AEPState.ACTIVE && Math.random() < 0.4
                    || this.state == AEPState.HYPERACTIVE && Math.random() < 0.8) {
                Object[] protein = space.get(new ActualField("PROTEIN"), new FormalField(ProteinType.class),
                        new ActualField(ProteinStatus.NORMAL), new FormalField(Integer.class));
                if ((int) protein[3] == 0) {
                    space.put(protein[0], protein[1], protein[2], protein[3]);
                    continue;
                }
                logger.info("AEP Proteina presa: " + Arrays.toString(protein));

                createCleavedProtein(protein);

                space.put(protein[0], protein[1], protein[2], (int) protein[3] - 1);
            }
        }
    }

    private void createCleavedProtein(Object[] protein) throws InterruptedException {
        if (protein[1] == ProteinType.ALPHA) {
            Object[] createTuple = space.getp(new ActualField("CREATE"),
                    new ActualField("CLEAVED_ALPHA_PROTEIN"), new FormalField(Integer.class));
            if (createTuple == null)
                space.put("CREATE", "CLEAVED_ALPHA_PROTEIN", 1);
            else
                space.put("CREATE", "CLEAVED_ALPHA_PROTEIN", (int) createTuple[2] + 1);
            Object[] cleavedProteins = space.get(new ActualField("PROTEIN"), new ActualField(ProteinType.ALPHA), new ActualField(ProteinStatus.CLEAVED), new FormalField(Integer.class));
            space.put(cleavedProteins[0], cleavedProteins[1], cleavedProteins[2], (int) cleavedProteins[3] + 1);
        } else {
            Object[] createTuple = space.getp(new ActualField("CREATE"),
                    new ActualField("CLEAVED_TAU_PROTEIN"), new FormalField(Integer.class));
            if (createTuple == null)
                space.put("CREATE", "CLEAVED_TAU_PROTEIN", 1);
            else
                space.put("CREATE", "CLEAVED_TAU_PROTEIN", (int) createTuple[2] + 1);
            Object[] cleavedProteins = space.get(new ActualField("PROTEIN"), new ActualField(ProteinType.TAU), new ActualField(ProteinStatus.CLEAVED), new FormalField(Integer.class));
            space.put(cleavedProteins[0], cleavedProteins[1], cleavedProteins[2], (int) cleavedProteins[3] + 1);
        }
    }

    private void changeAEPState() throws InterruptedException {
        Object[] changes = space.getp(new ActualField("CHANGE"), new FormalField(AEPState.class), new FormalField(Integer.class));
        if (changes != null && (int) changes[2] == 0)
            space.put(changes[0], changes[1], changes[2]);
        else if (changes != null && changes[1] != state) {
            logger.info("AEP cambia stato in: " + state);
            space.put(changes[0], changes[1], (int) changes[2] - 1);

            // update AEP tuple counter
            Object[] aeps = space.get(new ActualField("AEP"), new ActualField(state), new FormalField(Integer.class));
            space.put(aeps[0], aeps[1], (int) aeps[2] - 1);
            state = (AEPState) changes[1];
            aeps = space.get(new ActualField("AEP"), new ActualField(state), new FormalField(Integer.class));
            space.put(aeps[0], aeps[1], (int) aeps[2] + 1);

        }
    }
}
