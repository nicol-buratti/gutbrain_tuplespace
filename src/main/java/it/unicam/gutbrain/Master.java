package it.unicam.gutbrain;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * Hello world!
 */
public class Master implements Runnable {
    Space space;
    Map<String, Function<Space, Runnable>> map;

    public Master(Space space) {
        this.space = space;
        this.map = new HashMap<>();
        this.map.put("CLEAVED_PROTEIN", CleavedProteinAgent::new);
        this.map.put("OLIGOMER", Oligomer::new);
        this.map.put("AEP", AEPAgent::new);
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            while (true) {
                Object[] obj = space.get(new ActualField("CREATE"), new FormalField(String.class));
                System.out.println("CREATO " + obj[1]);
                Runnable agent = this.map.get(obj[1]).apply(space);

                executor.execute(agent);
                space.put(obj[1]);
                System.out.println(space.size());


            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }
}
