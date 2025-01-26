package it.unicam.gutbrain.gut;

import lombok.SneakyThrows;
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
    @SneakyThrows
    public void run() {
        Random random = new Random();
        while (true) {
            Thread.sleep(random.nextInt(600)); // Random short sleep
            Object[] diet = space.get(new ActualField("DIET"), new FormalField(Integer.class),
                    new FormalField(Integer.class), new FormalField(Integer.class));

            float goodBacteria = 1;
            float badBacteria = 1;

            int sugar = Math.max(0, (int) diet[1] - random.nextInt() % 10);
            int milk = Math.max(0, (int) diet[2] - random.nextInt() % 200);
            int salt = Math.max(0, (int) diet[3] - random.nextInt() % 2);

            // Adjust bacteria levels
            goodBacteria = adjustGoodBacteria(goodBacteria, sugar, milk, salt);
            badBacteria = adjustBadBacteria(badBacteria, sugar, milk, salt);

            // Update bacteria values in the space
            Object[] goodBacteriaTuple = space.get(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.GOOD), new FormalField(Integer.class));
            int updatedGoodBacteria = (int) ((int) goodBacteriaTuple[2] * goodBacteria);
            space.put("BACTERIA", BacteriaStatus.GOOD, updatedGoodBacteria);
            logger.info("Updated Good Bacteria: " + updatedGoodBacteria);

            Object[] badBacteriaTuple = space.get(new ActualField("BACTERIA"), new ActualField(BacteriaStatus.PATHOGENIC), new FormalField(Integer.class));
            int updatedPathogenicBacteria = (int) ((int) badBacteriaTuple[2] * badBacteria);
            space.put("BACTERIA", BacteriaStatus.PATHOGENIC, updatedPathogenicBacteria);
            logger.info("Updated Pathogenic Bacteria: " + updatedPathogenicBacteria);

            // Digestive cycle
            Thread.sleep(200 + random.nextInt(300));

            // Update diet
            sugar = addNutrient(sugar, random.nextInt() % 20, 1000);
            milk = addNutrient(milk, random.nextInt() % 250, 2000);
            salt = addNutrient(salt, random.nextInt() % 3, 50);
            space.put("DIET", sugar, milk, salt);
            logger.info(String.format("Updated Diet: Sugar=%d, Milk=%d, Salt=%d", sugar, milk, salt));
        }
    }

    private int addNutrient(int current, int addAmount, int maxLimit) {
        return Math.min(maxLimit, current + addAmount);
    }

    private float adjustGoodBacteria(float goodBacteria, int sugar, int milk, int salt) {
        // Effects of sugar
        if (sugar > 50) {
            goodBacteria -= 0.1f; // Reduced penalty
        } else if (sugar <= 10) {
            goodBacteria += 0.1f;
        }

        // Effects of milk
        if (milk > 500) {
            goodBacteria -= 0.2f; // Reduced penalty
        } else if (milk > 100 && milk <= 500) {
            goodBacteria += 0.3f; // Boost for moderate milk
        } else if (milk <= 100) {
            goodBacteria -= 0.05f; // Minor reduction
        }

        // Effects of salt
        if (salt > 5) {
            goodBacteria -= 0.1f; // Reduced penalty
        } else if (salt <= 2) {
            goodBacteria += 0.1f;
        }

        // Natural recovery for balanced diet
        if (sugar <= 30 && milk <= 400 && salt <= 4) {
            goodBacteria += 0.05f; // Recovery effect
        }

        // Normalize to dampen reductions and amplify recovery
        float scale = goodBacteria < 1.0f ? 1.5f : 1.0f; // Amplify recovery if low
        goodBacteria *= scale;

        // Clamp values to ensure stability
        return Math.max(0.5f, Math.min(2.0f, goodBacteria)); // Cap between 0.5 and 2.0
    }


    private float adjustBadBacteria(float badBacteria, int sugar, int milk, int salt) {
        // Effects of sugar
        if (sugar > 50) {
            badBacteria += 0.1f; // Reduced growth
        } else if (sugar <= 10) {
            badBacteria -= 0.1f;
        }

        // Effects of milk
        if (milk > 500) {
            badBacteria += 0.2f; // Reduced growth
        } else if (milk > 100 && milk <= 500) {
            badBacteria -= 0.1f; // Reduced inhibition
        } else if (milk <= 100) {
            badBacteria += 0.05f; // Slight increase
        }

        // Effects of salt
        if (salt > 5) {
            badBacteria += 0.1f; // Reduced growth
        } else if (salt <= 2) {
            badBacteria -= 0.05f; // Slight adjustment
        }

        // Natural decay for balanced diet
        if (sugar <= 30 && milk <= 400 && salt <= 4) {
            badBacteria -= 0.1f; // Decay bad bacteria
        }

        // Scale growth to dampen rapid increases
        float scale = badBacteria > 1.5f ? 0.5f : 1.0f;
        badBacteria *= scale;

        // Clamp values
        return Math.max(0.1f, Math.min(1.5f, badBacteria)); // Cap between 0.1 and 1.5
    }

}
