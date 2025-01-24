package it.unicam.gutbrain.gut;

import lombok.SneakyThrows;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.Random;
import java.util.logging.Logger;

public class DietAgent implements Runnable {

    private static final Logger logger = Logger.getLogger(DietAgent.class.getName());
    private static final int MAX_SLEEP_TIME = 1300;
    private static final int DIGESTIVE_TIME = 500;

    private final Space space;
    private final Random random;

    public DietAgent(Space space) {
        this.space = space;
        this.random = new Random();
    }

    @Override
    @SneakyThrows
    public void run() {
        while (true) {
            Thread.sleep(getRandomSleepTime(MAX_SLEEP_TIME));

            Object[] diet = fetchDietFromSpace();
            int sugar = adjustSugar((int) diet[1]);
            int milk = adjustMilk((int) diet[2]);
            int salt = adjustSalt((int) diet[3]);

            updateBacteriaLevels(milk);

            Thread.sleep(getRandomSleepTime(DIGESTIVE_TIME)); // Simulate digestion
            replenishDiet(sugar, milk, salt);
        }
    }

    private int getRandomSleepTime(int maxTime) {
        return Math.abs(random.nextInt() % maxTime);
    }

    private Object[] fetchDietFromSpace() throws InterruptedException {
        return space.get(new ActualField("DIET"), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class));
    }

    private int adjustSugar(int sugar) {
        return sugar - Math.max(0, random.nextInt() % 15);
    }

    private int adjustMilk(int milk) {
        return milk - Math.max(0, random.nextInt() % 300);
    }

    private int adjustSalt(int salt) {
        return salt - Math.max(0, random.nextInt() % 2);
    }

    private void updateBacteriaLevels(int milk) throws InterruptedException {
        float goodBacteriaFactor = milk > 500 ? 0.5f : 1;
        float badBacteriaFactor = milk > 500 ? 1.5f : 1;

        updateBacteria(BacteriaStatus.GOOD, goodBacteriaFactor);
        updateBacteria(BacteriaStatus.PATHOGENIC, badBacteriaFactor);
    }

    private void updateBacteria(BacteriaStatus status, float factor) throws InterruptedException {
        Object[] bacteria = space.get(new ActualField("BACTERIA"), new ActualField(status), new FormalField(Integer.class));
        int updatedBacteria = Math.round((int) bacteria[2] * factor);
        space.put("BACTERIA", status, updatedBacteria);
        logger.info("BACTERIA " + status + ": " + updatedBacteria);
    }

    private void replenishDiet(int sugar, int milk, int salt) throws InterruptedException {
        int addSugar = Math.abs(random.nextInt() % 20);
        int addMilk = Math.abs(random.nextInt() % 350);
        int addSalt = Math.abs(random.nextInt() % 3);

        space.put("DIET", sugar + addSugar, milk + addMilk, salt + addSalt);
        logger.info("Mangiato " + addSugar + " " + addMilk + " " + addSalt);
    }
}
