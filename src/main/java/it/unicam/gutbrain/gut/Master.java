package it.unicam.gutbrain.gut;

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
    Space space;
    Map<String, Function<Space, Runnable>> map;

    public Master(Space space) {
        this.space = space;
        this.map = new HashMap<>();
        this.map.put("CLEAVED_ALPHA_PROTEIN", (sp) -> new CleavedProteinAgent(sp, ProteinType.ALPHA));
        this.map.put("CLEAVED_TAU_PROTEIN", (sp) -> new CleavedProteinAgent(sp, ProteinType.TAU));
        this.map.put("AEP", AEPAgent::new);
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            while (true) {
                Object[] obj = space.get(new ActualField("CREATE"), new FormalField(String.class));
                logger.info("CREATO " + obj[1]);
                Runnable agent = this.map.get(obj[1]).apply(space);

                executor.execute(agent);
                space.put(obj[1]);
//                System.out.println(space.size());


            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }
}
