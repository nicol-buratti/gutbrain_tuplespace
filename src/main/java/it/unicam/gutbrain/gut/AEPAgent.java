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
            Object[] changes = space.getp(new ActualField("CHANGE"), new FormalField(AEPState.class));
            if (changes != null && changes[1] != state) {
                state = (AEPState) changes[1];
                logger.info("AEP cambia stato in: " + state);
            }
            if (this.state == AEPState.ACTIVE && Math.random() < 0.4
                    || this.state == AEPState.HYPERACTIVE && Math.random() < 0.8) {
                Object[] protein = space.get(new ActualField("PROTEIN"), new FormalField(ProteinType.class),
                        new ActualField(ProteinStatus.NORMAL), new FormalField(Integer.class));
                if ((int) protein[3] == 0) {
                    space.put(protein[0], protein[1], protein[2], protein[3]);
                    continue;
                }
                logger.info("AEP Proteina presa: " + Arrays.toString(protein));

                if (protein[1] == ProteinType.ALPHA) {
                    Object[] createTuple = space.getp(new ActualField("CREATE"),
                            new ActualField("CLEAVED_ALPHA_PROTEIN"), new FormalField(Integer.class));
                    if (createTuple == null)
                        space.put("CREATE", "CLEAVED_ALPHA_PROTEIN", 1);
                    else
                        space.put("CREATE", "CLEAVED_ALPHA_PROTEIN", (int) createTuple[2] + 1);
                } else {
                    Object[] createTuple = space.getp(new ActualField("CREATE"),
                            new ActualField("CLEAVED_TAU_PROTEIN"), new FormalField(Integer.class));
                    if (createTuple == null)
                        space.put("CREATE", "CLEAVED_TAU_PROTEIN", 1);
                    else
                        space.put("CREATE", "CLEAVED_TAU_PROTEIN", (int) createTuple[2] + 1);
                }

                space.put(protein[0], protein[1], protein[2], (int) protein[3] - 1);
            }
        }
    }
}
