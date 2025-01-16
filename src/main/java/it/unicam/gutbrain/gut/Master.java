package it.unicam.gutbrain.gut;

import lombok.SneakyThrows;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class Master implements Runnable {

    private static final Logger logger = Logger.getLogger(Master.class.getName());
    private final Space space;
    private final Map<String, Function<Space, Runnable>> map;


    public Master(Space space) {
        this.space = space;
        this.map = new HashMap<>();
        this.map.put("CLEAVED_ALPHA_PROTEIN", (sp) -> new CleavedProteinAgent(sp, ProteinType.ALPHA));
        this.map.put("CLEAVED_TAU_PROTEIN", (sp) -> new CleavedProteinAgent(sp, ProteinType.TAU));
        this.map.put("AEP", AEPAgent::new);
        this.map.put("PROTEINGENERATOR", ProteinGenerator::new);
        this.map.put("DIETGENERATOR", DietAgent::new);
        this.map.put("GUTPERMEABILITYGENERATOR", GutPermeabilityAgent::new);
        this.map.put("SPACESTATECATCHER", SpaceStateCatcherAgent::new);

    }

    @Override
    @SneakyThrows
    public void run() {
        ExecutorService executor = Executors.newCachedThreadPool();
        while (true) {
            Object[] obj = space.get(new ActualField("CREATE"), new FormalField(String.class), new FormalField(Integer.class));
            if ((int) obj[2] == 0) {
                space.put(obj[0], obj[1], obj[2]);
                continue;
            }
            logger.info("CREATO " + obj[1]);
            space.put("CREATE", obj[1], (int) obj[2] - 1);
            Runnable agent = this.map.get((String) obj[1]).apply(space);
            executor.execute(agent);
        }
    }
}
