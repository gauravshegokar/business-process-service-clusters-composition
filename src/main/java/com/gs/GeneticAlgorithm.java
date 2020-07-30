package com.gs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GeneticAlgorithm {

    private final HashMap<String, HashMap<String, HashMap<String, Object>>> candidatesInfo;
    private final double mutationRate;
    List<String> population = new ArrayList<>();
    private List<Double> populationFitness = new ArrayList<>();
    private List<String> matingPool = new ArrayList<>();
    private int populationSize;
    private int geneSize = 8;


    public GeneticAlgorithm(int POPULATION_SIZE, int GENE_SIZE, double MUTATION_RATE, HashMap<String, HashMap<String, HashMap<String, Object>>> mapNormalized) {
        this.populationSize = POPULATION_SIZE;
        this.geneSize = GENE_SIZE;
        this.mutationRate = MUTATION_RATE;
        this.candidatesInfo = mapNormalized;
    }

    // initializes the population to random genes
    public void initializePopulation() {
        for (int i = 0; i < populationSize; ) {
            String randomBinary = "";
            for (int j = 0; j < geneSize; j++) {
                Random random = new Random();
                boolean res;
                res = random.nextBoolean();
                if (res)
                    randomBinary = randomBinary.concat("1");
                else
                    randomBinary = randomBinary.concat("0");
            }
            population.add(randomBinary);
            i++;
        }
        this.evaluatePopulationFitness();
    }

    // evaluates population fitness
    public void evaluatePopulationFitness() {
        List<Double> currentPopFitness = new ArrayList<>();
        for (String e : population) {
            currentPopFitness.add(getGeneFitness(e));
        }
        this.populationFitness = currentPopFitness;
    }

    // Fitness function
    public double getGeneFitness(String e) {
        // magic number to be tuned later -> 0.000001
        if (!candidatesInfo.get("SC1").containsKey(e.substring(0, 3))) {
            return 0.0001;
        }
        if (!candidatesInfo.get("SC2").containsKey(e.substring(3, 5))) {
            return 0.0001;
        }
        if (!candidatesInfo.get("SC3").containsKey(e.substring(5, 8))) {
            return 0.0001;
        }

        float costSC1 = Float.parseFloat(candidatesInfo.get("SC1").get(e.substring(0, 3)).get("cost").toString());
        float costSC2 = Float.parseFloat(candidatesInfo.get("SC2").get(e.substring(3, 5)).get("cost").toString());
        float costSC3 = Float.parseFloat(candidatesInfo.get("SC3").get(e.substring(5, 8)).get("cost").toString());
        float totalCost = costSC1 + costSC2 + costSC3;

        float perSC1 = Float.parseFloat(candidatesInfo.get("SC1").get(e.substring(0, 3)).get("performance").toString());
        float perSC2 = Float.parseFloat(candidatesInfo.get("SC2").get(e.substring(3, 5)).get("performance").toString());
        float perSC3 = Float.parseFloat(candidatesInfo.get("SC3").get(e.substring(5, 8)).get("performance").toString());
        float totalPerformance = perSC1 * perSC2 * perSC3;

        float relSC1 = Float.parseFloat(candidatesInfo.get("SC1").get(e.substring(0, 3)).get("reliability").toString());
        float relSC2 = Float.parseFloat(candidatesInfo.get("SC2").get(e.substring(3, 5)).get("reliability").toString());
        float relSC3 = Float.parseFloat(candidatesInfo.get("SC3").get(e.substring(5, 8)).get("reliability").toString());
        float totalReliability = relSC1 * relSC2 * relSC3;

        float avSC1 = Float.parseFloat(candidatesInfo.get("SC1").get(e.substring(0, 3)).get("availability").toString());
        float avSC2 = Float.parseFloat(candidatesInfo.get("SC2").get(e.substring(3, 5)).get("availability").toString());
        float avSC3 = Float.parseFloat(candidatesInfo.get("SC3").get(e.substring(5, 8)).get("availability").toString());
        float totalAvailability = avSC1 * avSC2 * avSC3;

        return (0.35 * totalCost) + (0.20 * totalPerformance) + (0.10 * totalReliability) + (0.35 * totalAvailability);
    }

    // mutates gene based in the mutation rate
    private String getMutationGene(String newGene) {
        String mutatedGene = "";
        for (int i = 0; i < newGene.length(); i++) {
            char c = newGene.charAt(i);
            Random rand = new Random();
            if (rand.nextFloat() < this.mutationRate) {
                if (Character.toString(c).equals("1")) {
                    mutatedGene = mutatedGene.concat("0");
                } else {
                    mutatedGene = mutatedGene.concat("1");
                }
            } else {
                mutatedGene = mutatedGene.concat(Character.toString(c));
            }
        }
        return mutatedGene;
    }

    // crossover between two genes
    private String getCrossover(String matingGene1, String matingGene2) {
        // TODO:
        // need to randomize the midpoint selection
        // currently taking 3 as to not break sequence
        // could be (should be) randomized for effective new population
        return matingGene1.substring(0, 3) + matingGene2.substring(3, 8);
    }

    // creates mating pool
    // genes with high fitness value gets higher chances to be picked for reproduction
    private void naturalSelection() {
        Double maxFitness = 0.0;
        List<String> matingPool = new ArrayList<>();
        for (Double fitness : populationFitness) {
            if (fitness > maxFitness) {
                maxFitness = fitness;
            }
        }

        for (int i = 0; i < this.population.size(); i++) {
            double fitness_ = populationFitness.get(i) * 100 / maxFitness;
            for (int j = 0; j < fitness_; j++) {
                matingPool.add(this.population.get(i));
            }
        }
        this.matingPool = matingPool;
    }

    // generates new generation
    // Step 1: build mating pool
    // Step 2: pick two genes from mating pool
    // Step 3: crossover
    // Step 4: mutation
    // Step 5: add new gene to population
    public void generateNewGeneration() {

        // build mating pool based on the population fitness
        // the candidate with the highest fitness gets more probability of reproduction
        this.naturalSelection();

        // create New population (generation) with mating members from mating pull
        List<String> newPopulation = new ArrayList<>();
        int randomNum;
        for (int i = 0; i < this.populationSize; i++) {
            randomNum = ThreadLocalRandom.current().nextInt(0, this.matingPool.size());
            String matingGene1 = this.matingPool.get(randomNum);

            randomNum = ThreadLocalRandom.current().nextInt(0, this.matingPool.size());
            String matingGene2 = this.matingPool.get(randomNum);

            // crossover
            String newGene = getCrossover(matingGene1, matingGene2);

            // mutation
            String newGeneMutation = getMutationGene(newGene);

            // add new gene to population
            newPopulation.add(newGeneMutation);
        }

        // generation completed, now setting current population to the new generation
        this.population = newPopulation;

        // evaluates the fitness of new population
        this.evaluatePopulationFitness();
    }

    // returns fittest gene
    public String getMaxFitGene() {
        double maxFitness = 0.0;
        int maxFitnessIdx = 0;

        for (int i = 0; i < this.population.size(); i++) {
            if (this.populationFitness.get(i) > maxFitness) {
                maxFitness = this.populationFitness.get(i);
                maxFitnessIdx = i;
            }
        }
        return this.population.get(maxFitnessIdx);
    }

    // maps gene to services
    public List<String> mapGeneToServices(String maxFitGene) {
        List<String> serviceArray = new ArrayList<>();
        String firstService = candidatesInfo.get("SC1").get(maxFitGene.substring(0, 3)).get("name").toString();
        String secondService = candidatesInfo.get("SC2").get(maxFitGene.substring(3, 5)).get("name").toString();
        String thirdService = candidatesInfo.get("SC3").get(maxFitGene.substring(5, 8)).get("name").toString();

        serviceArray.add(firstService);
        serviceArray.add(secondService);
        serviceArray.add(thirdService);
        return serviceArray;
    }
}
