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

    public AEPAgent(Space space) {
        this.space = space;
        this.state = AEPState.ACTIVE;
    }

    @Override
    @SneakyThrows
    public void run() {
        while (true) {
            updateState();
            if (shouldProcessProtein()) {
                processProtein();
            }
        }
    }

    private boolean shouldProcessProtein() {
        double chance = this.state == AEPState.ACTIVE ? 0.4 : 0.8;
        return Math.random() < chance;
    }

    @SneakyThrows
    private void processProtein() {
        Object[] protein = space.get(
                new ActualField("PROTEIN"),
                new FormalField(ProteinType.class),
                new ActualField(ProteinStatus.NORMAL),
                new FormalField(Integer.class)
        );

        if ((int) protein[3] == 0) {
            space.put(protein[0], protein[1], protein[2], protein[3]);
            return;
        }

        logger.info("AEP Protein processed: " + Arrays.toString(protein));
        createCleavedProtein(protein);
        space.put(protein[0], protein[1], protein[2], (int) protein[3] - 1);
    }

    @SneakyThrows
    private void createCleavedProtein(Object[] protein) {
        ProteinType type = (ProteinType) protein[1];
        String cleavedKey = type == ProteinType.ALPHA ? "CLEAVED_ALPHA_PROTEIN" : "CLEAVED_TAU_PROTEIN";

        updateCleavedProteinCount(cleavedKey);
        incrementCleavedProteinSpace(type);
    }

    @SneakyThrows
    private void updateCleavedProteinCount(String key) {
        Object[] createTuple = space.getp(new ActualField("CREATE"), new ActualField(key), new FormalField(Integer.class));
        int count = createTuple == null ? 0 : (int) createTuple[2];
        space.put("CREATE", key, count + 1);
    }

    @SneakyThrows
    private void incrementCleavedProteinSpace(ProteinType type) {
        Object[] cleavedProteins = space.get(
                new ActualField("PROTEIN"),
                new ActualField(type),
                new ActualField(ProteinStatus.CLEAVED),
                new FormalField(Integer.class)
        );
        space.put(cleavedProteins[0], cleavedProteins[1], cleavedProteins[2], (int) cleavedProteins[3] + 1);
    }

    @SneakyThrows
    private void updateState() {
        Object[] changes = space.getp(new ActualField("CHANGE"), new FormalField(AEPState.class), new FormalField(Integer.class));
        if (changes == null) return;

        int remaining = (int) changes[2];
        if (remaining == 0) {
            space.put(changes);
        } else if (changes[1] != state) {
            logger.info("AEP state changed to: " + changes[1]);

            // Update current state counters
            updateStateCounters(state, -1);
            state = (AEPState) changes[1];
            updateStateCounters(state, 1);

            space.put(changes[0], changes[1], remaining - 1);
        }
    }

    @SneakyThrows
    private void updateStateCounters(AEPState targetState, int delta) {
        Object[] aeps = space.get(
                new ActualField("AEP"),
                new ActualField(targetState),
                new FormalField(Integer.class)
        );
        space.put(aeps[0], aeps[1], (int) aeps[2] + delta);
    }
}
