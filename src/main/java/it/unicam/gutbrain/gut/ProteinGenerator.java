package it.unicam.gutbrain.gut;

import lombok.SneakyThrows;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.Random;
import java.util.logging.Logger;

public class ProteinGenerator implements Runnable {

    private static final Logger logger = Logger.getLogger(ProteinGenerator.class.getName());

    private final Space space;


    public ProteinGenerator(Space space) {
        this.space = space;
    }

    @Override
    @SneakyThrows
    public void run() {
        Random random = new Random();
        while (true) {
                Thread.sleep(Math.abs(random.nextLong() % 500));

                extracted(ProteinType.TAU, "TAU created");

                extracted(ProteinType.ALPHA, "ALPHA created");
        }
    }

    private void extracted(ProteinType proteinType, String message) throws InterruptedException {
        Object[] protein = space.get(new ActualField("PROTEIN"), new ActualField(proteinType),
                new ActualField(ProteinStatus.NORMAL), new FormalField(Integer.class));
        if ((int) protein[3] < 50)
            space.put(protein[0], protein[1], protein[2], ((int) protein[3] + 600));
        else
            space.put(protein[0], protein[1], protein[2], ((int) protein[3] + 250));
        logger.info(message);
    }
}
