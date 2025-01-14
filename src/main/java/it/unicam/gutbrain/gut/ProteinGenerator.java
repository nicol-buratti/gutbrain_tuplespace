package it.unicam.gutbrain.gut;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.Random;

public class ProteinGenerator implements Runnable {

    private final Space space;


    public ProteinGenerator(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            try {
                Thread.sleep(random.nextLong() % 2000);

                Object[] tauProtein = space.get(new ActualField("PROTEIN"), new ActualField(ProteinType.TAU),
                        new ActualField(ProteinStatus.NORMAL), new FormalField(Integer.class));
                if ((int) tauProtein[3] < 10)
                    space.put(tauProtein[0], tauProtein[1], tauProtein[2], ((int) tauProtein[3] + 200) * 2);
                else
                    space.put(tauProtein[0], tauProtein[1], tauProtein[2], ((int) tauProtein[3] + 50) * 2);

                Object[] alphaProtein = space.get(new ActualField("PROTEIN"), new ActualField(ProteinType.ALPHA),
                        new ActualField(ProteinStatus.NORMAL), new FormalField(Integer.class));
                if ((int) alphaProtein[3] < 10)
                    space.put(alphaProtein[0], alphaProtein[1], alphaProtein[2], ((int) alphaProtein[3] + 200) * 2);
                else
                    space.put(alphaProtein[0], alphaProtein[1], alphaProtein[2], ((int) alphaProtein[3] + 50) * 2);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
