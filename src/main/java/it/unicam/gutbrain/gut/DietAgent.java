package it.unicam.gutbrain.gut;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.Random;
import java.util.logging.Logger;

public class DietAgent implements Runnable {

    private static final Logger logger = Logger.getLogger(DietAgent.class.getName());
    private final Space space;

    public DietAgent(Space space) {
        this.space = space;
    }

    @Override
    public void run() {

        Random random = new Random();
        while (true) {
            try {
                Thread.sleep(Math.abs(random.nextLong() % 100));
                Object[] diet = space.get(new ActualField("DIET"), new FormalField(Integer.class),
                        new FormalField(Integer.class), new FormalField(Integer.class));

                float goodBacteria = 1;
                float badBacteria = 1;

                int sugar = (int) diet[1];
                int milk = (int) diet[2];
                int salt = (int) diet[3];

                sugar -= Math.max(0, random.nextInt() % 15);
                milk -= Math.max(0, random.nextInt() % 300);
                salt -= Math.max(0, random.nextInt() % 2);


                if (milk > 500) {
                    goodBacteria -= 0.5;
                    badBacteria += 0.5;
                }

                Object[] bacteria = space.get(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.GOOD), new FormalField(Integer.class));
                int updatedGoodBacteria = (int) ((int) bacteria[2] * goodBacteria);
                space.put("BACTERIA", BacteriaStatus.GOOD, updatedGoodBacteria);
                logger.info("BACTERIA GOOD: " + updatedGoodBacteria);

                bacteria = space.get(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.PATHOGENIC), new FormalField(Integer.class));
                int updatedPathogenicBacteria = (int) ((int) bacteria[2] * badBacteria);
                space.put("BACTERIA", BacteriaStatus.PATHOGENIC, updatedPathogenicBacteria);
                logger.info("BACTERIA PATHOGENIC: " + updatedPathogenicBacteria);


                Thread.sleep(Math.abs(random.nextLong() % 500)); // digestive time
                int addSugar = Math.abs(random.nextInt() % 20);
                int addMilk = Math.abs(random.nextInt() % 350);
                int addSalt = Math.abs(random.nextInt() % 3);
                space.put("DIET", sugar + addSugar, milk + addMilk, salt + addSalt);
                logger.info("Mangiato " + addSugar + " " + addMilk + " " + addSalt);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
