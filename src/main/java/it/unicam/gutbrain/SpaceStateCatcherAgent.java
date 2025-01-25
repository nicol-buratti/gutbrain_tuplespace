package it.unicam.gutbrain;

import it.unicam.gutbrain.gut.ProteinStatus;
import it.unicam.gutbrain.gut.ProteinType;
import lombok.SneakyThrows;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import org.jspace.io.json.jSonUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SpaceStateCatcherAgent implements Runnable {
    private final Space space;

    public SpaceStateCatcherAgent(Space space) {
        this.space = space;
    }

    @Override
    @SneakyThrows
    public void run() {
        jSonUtils jsonUtil = jSonUtils.getInstance();
        String fileName = "output.txt";
        File file = new File(fileName);
        //if file doesnt exists, then create it
        if (!file.exists())
            file.createNewFile();

        String[] tupleNames = {"GUT", "AEP", "PROTEIN", "OLIGOMER", "BACTERIA", "DIET", "MICROGLIA", "NEURON", "CYTOKINE"};
        try (FileWriter fileWriter = new FileWriter(file, true);
             BufferedWriter bufferWriter = new BufferedWriter(fileWriter)) {

            while (true) {
                // Pause execution for 1 second
                Thread.sleep(1000);

                // Query the space
                List<Object[]> query = Arrays.stream(tupleNames)
                        .parallel()
                        .map(this::getTuplesFromName)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());


                // Convert to JSON string
                String string = jsonUtil.toString(query);

                // Write to file
                bufferWriter.write(string);
                bufferWriter.newLine();
                bufferWriter.flush(); // Ensure data is written to file immediately
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize writer: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @SneakyThrows
    private List<Object[]> getTuplesFromName(String s) {
        List<Object[]> tuples = new LinkedList<>();
        switch (s) {
            case "GUT":
                Object[] gut = space.query(new ActualField(s), new FormalField(Integer.class));
                tuples.add(gut);
                break;
            case "PROTEIN":
                Object[] protein1 = space.query(new ActualField(s), new ActualField(ProteinType.ALPHA), new ActualField(ProteinStatus.NORMAL), new FormalField(Integer.class));
                Object[] protein2 = space.query(new ActualField(s), new ActualField(ProteinType.ALPHA), new ActualField(ProteinStatus.NORMAL), new FormalField(Integer.class));
                Object[] protein3 = space.query(new ActualField(s), new ActualField(ProteinType.TAU), new ActualField(ProteinStatus.CLEAVED), new FormalField(Integer.class));
                Object[] protein4 = space.query(new ActualField(s), new ActualField(ProteinType.TAU), new ActualField(ProteinStatus.CLEAVED), new FormalField(Integer.class));
                tuples.add(protein1);
                tuples.add(protein2);
                tuples.add(protein3);
                tuples.add(protein4);
                break;
            case "DIET":
                Object[] diet = space.query(new ActualField(s), new FormalField(Object.class), new FormalField(Object.class), new FormalField(Object.class));
                tuples.add(diet);
                break;
            default:
                List<Object[]> other = space.queryAll(new ActualField(s), new FormalField(Object.class), new FormalField(Object.class));
                tuples.addAll(other);
                break;
        }
        return tuples;
    }
}
