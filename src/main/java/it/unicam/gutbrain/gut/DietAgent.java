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
                Thread.sleep(random.nextLong() % 100);
                Object[] diet = space.get(new ActualField("DIET"), new FormalField(Integer.class),
                        new FormalField(Integer.class), new FormalField(Integer.class));

                float goodBacteria = 0;
                float badBacteria = 0;

                int sugar = (int) diet[1];
                int milk = (int) diet[2];
                int salt = (int) diet[3];

                if (milk > 500) {
                    goodBacteria -= 0.5;
                    badBacteria += 1.5;
                }

                Object[] bacteria = space.get(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.GOOD), new FormalField(Integer.class));
                space.put("BACTERIA", BacteriaStatus.GOOD, (int) bacteria[2] * goodBacteria);

                bacteria = space.get(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.PATHOGENIC), new FormalField(Integer.class));
                space.put("BACTERIA", BacteriaStatus.PATHOGENIC, (int) bacteria[2] * badBacteria);

                Thread.sleep(random.nextLong() % 1000);
                sugar = random.nextInt() % 20;
                milk = random.nextInt() % 300;
                salt = random.nextInt() % 10;
                space.put("DIET", sugar, milk, salt);
                logger.info("Mangiato " + sugar + " " + milk + " " + salt);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
