package it.unicam.gutbrain.gut;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.Random;
import java.util.logging.Logger;

import static it.unicam.gutbrain.Config.env;

public class GutPermeabilityAgent implements Runnable {

    private static final Logger logger = Logger.getLogger(GutPermeabilityAgent.class.getName());
    private final Space space;
    private final int microbiotaDiversityThreshold;
    private final int gutBarrierImpermeability;

    public GutPermeabilityAgent(Space space) {
        this.space = space;
        this.microbiotaDiversityThreshold = env.get("microbiota_diversity_threshold");
        this.gutBarrierImpermeability = env.get("barrier_impermeability");
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            try {
                Thread.sleep(Math.abs(random.nextLong() % 50));
                Object[] goodBacteria = space.get(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.GOOD), new FormalField(Integer.class));
                Object[] badBacteria = space.get(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.PATHOGENIC), new FormalField(Integer.class));

                int goodBacteriaCount = (int) goodBacteria[2];
                int badBacteriaCount = (int) badBacteria[2];
                Object[] gut = space.get(new ActualField("GUT"), new FormalField(Integer.class));
                int gutCount = (int) gut[1];
                if (goodBacteriaCount - badBacteriaCount <= microbiotaDiversityThreshold) {
                    // dysbiosis
                    int decreaseValue = gutBarrierImpermeability * random.nextInt(5) / 100;
                    int barrierPermeability = Math.max(0, gutCount - decreaseValue);
                    space.put("GUT", barrierPermeability);
                    int aepToHyperactivate = decreaseValue;
                    // TODO update the numbero of aep to hyperactivate
                } else {
                    if (gutCount < gutBarrierImpermeability) {
                        int increaseValue = gutBarrierImpermeability * random.nextInt(3) / 100;
                        if (gutCount + increaseValue <= gutBarrierImpermeability)
                            space.put("GUT", gutCount + increaseValue);
                        else
                            space.put("GUT", gutCount);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
