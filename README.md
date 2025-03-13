# Gut-Brain Connection Simulation for Alzheimer's Disease using Tuple Spaces
This project simulates the connection between the gut and the brain in the context of Alzheimer's disease using tuple spaces. The simulation is implemented in Java with the jSpace library and follows a Maven project structure. The simulation models the interactions between various components of the gut and brain, enabling the study of their dynamic relationships.

## Components

### Gut Agents
- Protein: Simulates proteins involved in gut-brain interactions.
- AEP (Asparaginyl Endopeptidase): Enzyme affecting protein processing.
- Microbiota: Models the gut microbiome's role in the interaction.
- Diet: Represents dietary influences on the gut environment.

### Brain Agents
- Neuron: Represents neuronal activity in response to gut signals.
- Cytokine: Models inflammatory responses affecting brain function.
- Microglia: Simulates immune cells in the brain.

## Running the Simulation
To run the simulation, follow these steps:

### 1. Clone the Repository  
```bash
git clone https://github.com/nicol-buratti/gutbrain_tuplespace.git
cd gutbrain_tuplespace
```

### 2. Build the project using Maven
Make sure you are using Java 21 for this project. You can verify your Java version by running:
```bash
java -version
```
If you don't have Java 21 installed, download and install it from [here](https://www.oracle.com/java/technologies/downloads/#java21?er=221886).

Then, build the project with Maven:
```bash
mvn clean install
```

### 3. Modify the `env.yml` file
You can adjust the number of agents and other parameters in the `env.yml` file.

### 4. Run the simulation
```bash
mvn exec:java
```
This will start the simulation with the configuration specified in `env.yml`. The agents in the gut and brain will interact according to the modeled behaviors.

**Note**: The simulation will continue running indefinitely and does not terminate automatically. You will need to stop the simulation manually by interrupting the process (e.g., by pressing Ctrl + C in your terminal).

## Study Focus
The primary goal of this simulation is to study how various factors in the gut (such as protein levels, microbiota composition, and diet) influence the brain (neurons, cytokines, and microglia). By adjusting the parameters and observing the interactions, we aim to gain insights into the role of the gut-brain axis in the development and progression of Alzheimer's Disease.

# Contributors
- [Buratti Nicol](https://github.com/nicol-buratti)
- [Pennesi Diego](https://github.com/Diezz01)
- [Reucci Filippo](https://github.com/reus702)
