# Genetic Algorithm for identifying optimal services

##1. Project Setup
1. Import Project
2. Add libraries to web-inf of target folder.  
Go to `project structure -> Artifacts -> available elements -> put to web-inf folder`   
so that the libraries will be accessible
3. update the `filePath` from `GAWebService` module to the input file path. which is `data/iput.txt`
4. Start the server. I have used `Tomcat 7.0.96`

##2. Project Structure

    data ->                         contains input text file  
    screenshots ->                  contains screenshots of api results
    src   ->                        Source code
        main/java/com/gs
            GAServlet ->            rest api interface (json): Class for handling api request to /api/ga
            GeneticAlgorithm ->     Custom implementation of genetic algorithm
            GAWebService ->         Handler class for Genetic Algorithm, prepares data in required format and interacts with GeneticAlgorithm Class  

##3. Screenshots
Located in the screenshots folder

##4. Custom implementation of Genetic Algorithm
The project is not using any libraries for Genetic Algorithm  

I have implemented a genetic algorithm to have complete control over the flow and avoiding understanding of library-specific functionalities.
It is completely parameterized, eg: population size, mutation rate, evolutions. 

#### Gene structure
I have kept gene size to 8 bits to handle all the possibilities of services

    1. first 3 bits are for service SC1, which has 5 possible values, to represent 5 values we need to have 3 bits.
    2. bit 4 and 5 are for service SC2, which has 3 possible values, to represent 3 values we need to have 2 bits.
    3. bits 6,7,8 are for service SC3, which has 8 possible values, to represent 8 values we need to have 3 bits. 

#### Fitness function
Before doing the fitness calculation, we need to have all the parameters in the range 0-1.
to achieve that, normalization is performed. 
There are some parameters like time and cost, where the values are lower the better. These cases are handled by subtracting the vales from highest possible values.

For total cost, I am doing addition of all the costs of services
for performance, reliability, availability I am doing multiplication of services.

I am using the following fitness function:

    (0.35 * totalCost) + (0.20 * totalPerformance) + (0.10 * totalReliability) + (0.35 * totalAvailability)


#### Flow
#####1. initialize the population (random)
Initial population is created with random values for genes. 
Depending upon the population size parameter passed the population is created.

#####2. create mating pool
Fitness is calculated for every gene by using fitness function.  
Higher the fitness value, higher the chances of picking up the gene for reproduction (Survival of the fittest!).  

#####3. new genes creation till we create the new population

    1. selection of parents from the mating pool
       Select two genes from mating pool
    2. crossover
       perform crossover of two genes
    3. mutation
       Mutating genes based on the muation rate. Issential to have as it creates random patterns that could lead to optimal solution
    4. add new gene to new population
 
We replace the new population with old population and we call it the generation.

The idea is after every generation the population would contain more fitter genes compared to previous generation. 

##5. Results
Services - S11, S22, S33  
fitness value - 1.236679843068123

##6. API
endpoint - api/ga  
Query Parameters - populationsize, evolutions, mutationrate

Recommended values: populationsize - 100+, evolutions - 20+, mutationrate - 0.02 to 0.05

If we pass evolutions to 20, we would be able to see the results after each generation pass.  
sample output - 
    
    {
      "generations": [
        {
          "generation": 1,
          "maxFitGene": "00001000",
          "services": "S11, S22, S31",
          "fitness": 1.2204233884811402
        },
        {
          "generation": 2,
          "maxFitGene": "00001000",
          "services": "S11, S22, S31",
          "fitness": 1.2204233884811402
        },
        {
          "generation": 3,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 4,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 5,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 6,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 7,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 8,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 9,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 10,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 11,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 12,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 13,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 14,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 15,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 16,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 17,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 18,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 19,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        },
        {
          "generation": 20,
          "maxFitGene": "00001010",
          "services": "S11, S22, S33",
          "fitness": 1.236679843068123
        }
      ],
      "services": "S11, S22, S33",
      "fitness": 1.236679843068123
    }
