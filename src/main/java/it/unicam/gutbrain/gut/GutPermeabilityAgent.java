package it.unicam.gutbrain.gut;

import lombok.SneakyThrows;
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
    private final int permeabilityThreshold;

    public GutPermeabilityAgent(Space space) {
        this.space = space;
        this.microbiotaDiversityThreshold = env.get("microbiota_diversity_threshold");
        this.gutBarrierImpermeability = env.get("barrier_impermeability");
        this.permeabilityThreshold = env.get("permeability_threshold");
    }

    @Override
    @SneakyThrows
    public void run() {
        Random random = new Random();
        while (true) {
//                Thread.sleep(Math.abs(random.nextLong() % 50));
            //Object[] goodBacteria = space.query(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.GOOD), new FormalField(Integer.class));
            //Object[] badBacteria = space.query(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.PATHOGENIC), new FormalField(Integer.class));
            Object[] goodBacteria = space.get(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.GOOD), new FormalField(Integer.class));
            Object[] badBacteria = space.get(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.PATHOGENIC), new FormalField(Integer.class));
            int goodBacteriaCount = (int) goodBacteria[2];
            int badBacteriaCount = (int) badBacteria[2];

            Object[] gut = space.get(new ActualField("GUT"), new FormalField(Integer.class));
            int gutImpermeability = (int) gut[1];
            if (goodBacteriaCount - badBacteriaCount <= microbiotaDiversityThreshold) {
                int decreaseValue = gutBarrierImpermeability * (random.nextInt(6) + 1) / 100;
                int barrierImpermeability = Math.max(0, gutImpermeability - decreaseValue);
                space.put("GUT", barrierImpermeability);
                this.hyperactivateAEPs(decreaseValue);
            } else {
                space.put("GUT", gutImpermeability);
            }

            /*

            int gutPermeability = (int) gut[1];
            if (goodBacteriaCount - badBacteriaCount <= microbiotaDiversityThreshold) {
                // dysbiosis
                int decreaseValue = gutBarrierImpermeability * (random.nextInt(6)+1) / 100;
                int barrierPermeability = Math.max(0, gutPermeability - decreaseValue);
                space.put("GUT", barrierPermeability);
                this.hyperactivateAEPs(decreaseValue);
            } else if (gutPermeability < gutBarrierImpermeability) {
                int increaseValue = gutBarrierImpermeability * random.nextInt(4) / 100;
                if (gutPermeability + increaseValue <= gutBarrierImpermeability)
                    space.put("GUT", gutPermeability + increaseValue);
                else
                    space.put("GUT", gutPermeability);
            } else {
                space.put("GUT", gutPermeability);

            }
            */
            space.put(goodBacteria[0], goodBacteria[1], goodBacteria[2]);
            space.put(badBacteria[0], badBacteria[1], badBacteria[2]);
        }
    }

    private void hyperactivateAEPs(int aepToHyperactivate) throws InterruptedException {
        Object[] activeAEP = space.get(new ActualField("AEP"), new ActualField(AEPState.ACTIVE), new FormalField(Integer.class));
        int newActiveAEPNumber = (int) activeAEP[2] - aepToHyperactivate;
        if (newActiveAEPNumber < 0) {
            aepToHyperactivate += newActiveAEPNumber;
            newActiveAEPNumber = 0;
        }
        space.put("AEP", activeAEP[1], newActiveAEPNumber);
        logger.info("ACTIVE AEP: " + newActiveAEPNumber);

        Object[] hyperactiveAEP = space.get(new ActualField("AEP"), new ActualField(AEPState.HYPERACTIVE), new FormalField(Integer.class));
        int hyperactiveAEPNumber = (int) hyperactiveAEP[2];
        int newCount = hyperactiveAEPNumber + aepToHyperactivate;
        space.put("AEP", hyperactiveAEP[1], newCount);
        logger.info("HYPERACTIVE AEP: " + newCount);

        if (aepToHyperactivate == 0)
            return;
        Object[] changes = space.getp(new ActualField("CHANGE"), new FormalField(AEPState.class), new FormalField(Integer.class));

        if (changes == null)
            space.put("CHANGE", AEPState.HYPERACTIVE, 1);
        else if (newActiveAEPNumber >= aepToHyperactivate)
            space.put("CHANGE", AEPState.HYPERACTIVE, (int) changes[2] + aepToHyperactivate);
        else
            space.put("CHANGE", AEPState.HYPERACTIVE, changes[2]);


    }
}
