package it.unicam.gutbrain.gut;

import lombok.SneakyThrows;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.Arrays;
import java.util.logging.Logger;

enum AEPState {
    ACTIVE,
    HYPERACTIVE
}

public class AEPAgent implements Runnable {

    private static final Logger logger = Logger.getLogger(AEPAgent.class.getName());
    private final Space space;
    private final AEPState state;

    @SneakyThrows
    public AEPAgent(Space space) {
        this.space = space;
        this.state = AEPState.ACTIVE;
        Object[] tuple = this.space.getp(new ActualField("AEP"), new FormalField(Integer.class));
        // creates the AEP tuple if it not exist, otherwise updates the count
        if (tuple == null)
            this.space.put("AEP", 0);
        else
            this.space.put("AEP", (int) tuple[1] + 1);
    }

    @Override
    @SneakyThrows
    public void run() {
        while (true) {
            Object[] protein = space.get(new ActualField("PROTEIN"), new FormalField(ProteinType.class),
                    new FormalField(ProteinStatus.class), new FormalField(Integer.class));
            if (this.state == AEPState.ACTIVE && Math.random() < 0.4
                    || this.state == AEPState.HYPERACTIVE && Math.random() < 0.8) {
                logger.info("AEP Proteina presa: " + Arrays.toString(protein));
                if (protein[1] == ProteinType.ALPHA)
                    space.put("CREATE", "CLEAVED_ALPHA_PROTEIN");
                else
                    space.put("CREATE", "CLEAVED_TAU_PROTEIN");
            }
        }
    }
}
