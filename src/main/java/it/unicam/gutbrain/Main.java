package it.unicam.gutbrain;

import it.unicam.gutbrain.gut.Master;
import it.unicam.gutbrain.gut.ProteinStatus;
import it.unicam.gutbrain.gut.ProteinType;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

public class Main {
    public static void main(String[] argv) throws InterruptedException, IOException {
        Space space = new SequentialSpace();
        space.put("GUT", 0); // impermeability
        //space.put("BRAIN")

        Master master = new Master(space);

        Yaml yaml = new Yaml();
        InputStream inputStream = Files.newInputStream(new File("C:\\Users\\nicki\\IdeaProjects\\gutbrain\\env.yaml").toPath());
        Map<String, Integer> env = yaml.load(inputStream);

        space.put("CREATE", "PROTEINGENERATOR");

        for (int i = 0; i < env.get("aep_enzyme"); i++) {
            space.put("CREATE", "AEP");
        }

        space.put("PROTEIN", ProteinType.ALPHA, ProteinStatus.NORMAL, env.get("alpha_proteins"));
        space.put("PROTEIN", ProteinType.TAU, ProteinStatus.NORMAL, env.get("tau_proteins"));
        space.put("OLIGOMER", ProteinType.ALPHA, env.get("alpha_oligomers"));
        space.put("OLIGOMER", ProteinType.TAU, env.get("tau_oligomers"));

        master.run();

    }
}
