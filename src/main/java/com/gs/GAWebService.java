package com.gs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GAWebService {
    private String filePath = "/Users/gauravshegokar/Documents/CMU/FALL_2019/SOC/hw6/GA_simplified/data/input.txt";

    // normalizes the parameters, brings all the parameters in range 0 to 1
    private static HashMap<String, HashMap<String, HashMap<String, Object>>> normalizeMap(HashMap<String, HashMap<String, HashMap<String, Object>>> map) {
        float maxCost = 0;
        float maxTime = 0;

        // normalize cost
        for (String key1 : map.keySet()) {
            for (String key2 : map.get(key1).keySet()) {
                int val = Integer.parseInt(map.get(key1).get(key2).get("cost").toString());
                if(val > maxCost){
                    maxCost = val;
                }
            }
        }

        for (String key1 : map.keySet()) {
            for (String key2 : map.get(key1).keySet()) {
                int val = Integer.parseInt(map.get(key1).get(key2).get("cost").toString());
                float noramlizedCost = (maxCost - val)/maxCost;
                map.get(key1).get(key2).put("cost", noramlizedCost);
            }
        }


        // normalize performance
        for (String key1 : map.keySet()) {
            for (String key2 : map.get(key1).keySet()) {
                int val = Integer.parseInt(map.get(key1).get(key2).get("performance").toString());
                if(val > maxTime){
                    maxTime = val;
                }
            }
        }

        for (String key1 : map.keySet()) {
            for (String key2 : map.get(key1).keySet()) {
                int val = Integer.parseInt(map.get(key1).get(key2).get("performance").toString());
                float normalizedTime = (maxTime - val)/maxTime;
                map.get(key1).get(key2).put("performance", normalizedTime);
            }
        }

        // normalize reliability and availability
        for (String key1 : map.keySet()) {
            for (String key2 : map.get(key1).keySet()) {
                float val = Integer.parseInt(map.get(key1).get(key2).get("reliability").toString());
                float normalized = val/100;
                map.get(key1).get(key2).put("reliability", normalized);

                val = Integer.parseInt(map.get(key1).get(key2).get("availability").toString());
                normalized = val/100;
                map.get(key1).get(key2).put("availability", normalized);
            }
        }
        return map;
    }

    // reads data from input file and creates a nested hasmap for easy access of elements
    public static HashMap<String, HashMap<String, HashMap<String,Object>>> getDataHashmapFromFile(String filePath){
        HashMap<String, HashMap<String, HashMap<String,Object>>> map = new HashMap<String, HashMap<String,HashMap<String,Object>>>();

        List<String> lines = new ArrayList<>();  //11 players
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        for (String line: lines) {
            String[] splited = line.split("\\s+");
            if(!map.containsKey(splited[0])){
                map.put(splited[0], new HashMap<String, HashMap<String,Object>>());
            }
            if(!map.get(splited[0]).containsKey(splited[6])) {
                map.get(splited[0]).put(splited[6], new HashMap<String, Object>());
            }
            map.get(splited[0]).get(splited[6]).put("cost", Integer.parseInt(splited[2]));
            map.get(splited[0]).get(splited[6]).put("reliability",  Integer.parseInt(splited[3]));
            map.get(splited[0]).get(splited[6]).put("performance",  Integer.parseInt(splited[4]));
            map.get(splited[0]).get(splited[6]).put("availability",  Integer.parseInt(splited[5]));
            map.get(splited[0]).get(splited[6]).put("name", splited[1]);
        }
        return map;
    }

    // returns the result json for api
    // takes population size, mutation rate and evolutions as input
    public String getResults(int POPULATION_SIZE, double MUTATION_RATE, int EVOLUTIONS){
        final int GENE_SIZE = 8;
        HashMap<String, HashMap<String, HashMap<String,Object>>> map = GAWebService.getDataHashmapFromFile(filePath);
        HashMap<String, HashMap<String, HashMap<String,Object>>> mapNormalized = GAWebService.normalizeMap(map);

        // Step 1 initialize class
        GeneticAlgorithm ga = new GeneticAlgorithm(POPULATION_SIZE, GENE_SIZE, MUTATION_RATE, mapNormalized);

        // Step 2 initialize population
        ga.initializePopulation();

        JsonArray jsArray = new JsonArray();
        // Step 3 generate new generation
        for (int i = 0; i < EVOLUTIONS; i++) {
            ga.generateNewGeneration();
            String maxFitGene = ga.getMaxFitGene();
            List<String> services = ga.mapGeneToServices(maxFitGene);
            JsonObject curJsonObj = new JsonObject();
            curJsonObj.addProperty("generation", i+1);
            curJsonObj.addProperty("maxFitGene", maxFitGene);
            curJsonObj.addProperty("services", String.join(", ", services));
            curJsonObj.addProperty("fitness", ga.getGeneFitness(maxFitGene));
            jsArray.add(curJsonObj);
        }

        JsonObject resultJson = new JsonObject();
        resultJson.add("generations", jsArray);
        resultJson.addProperty("services", String.join(", ", ga.mapGeneToServices(ga.getMaxFitGene())));
        resultJson.addProperty("fitness", ga.getGeneFitness(ga.getMaxFitGene()));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(resultJson);
    }

    // Console Application code
    public static void main(String[] args) throws Exception {
        String filePath = "data/input.txt";

        HashMap<String, HashMap<String, HashMap<String,Object>>> map = GAWebService.getDataHashmapFromFile(filePath);
        HashMap<String, HashMap<String, HashMap<String,Object>>> mapNormalized = GAWebService.normalizeMap(map);

        final int POPULATION_SIZE = 50;
        final int GENE_SIZE = 8;
        final double MUTATION_RATE = 0.05;
        final int EVOLUTIONS = 20;

        // Step 1 initialize class
        GeneticAlgorithm ga = new GeneticAlgorithm(POPULATION_SIZE, GENE_SIZE, MUTATION_RATE, mapNormalized);

        // Step 2 initialize population
        ga.initializePopulation();

        // Step 3 generate new generation
        for (int i = 0; i < EVOLUTIONS; i++) {

            // builds mating pool
            ga.generateNewGeneration();
            String maxFitGene = ga.getMaxFitGene();
            List<String> services = ga.mapGeneToServices(maxFitGene);
            System.out.println("-=-=-= Generation "+ (i+1) +" ==-==-=-=");
            System.out.println(maxFitGene);
            System.out.println(services);
            System.out.println(ga.getGeneFitness(maxFitGene));
            System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        }
    }
}
