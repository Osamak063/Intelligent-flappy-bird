/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flappybird;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Osama 3.1 gpa
 */
public class Bird {

    EvolutionaryNeuralNetwork network;
    Rectangle rectangle;
    String image;
    boolean isAlive;
    int yMotion;
    int fitness;

    int rank;
    double proportion;
    double commulative;

    //    only for the first time
    void initializeNetwork() {

        //creating initial nodes  
        network.getInputLayer().add(new Node());
        network.getInputLayer().add(new Node());

        //setting hidden layer
        network.getHiddenLayer().add(new Node());
        network.getHiddenLayer().add(new Node());
        network.getHiddenLayer().get(0).setBias(Mathematics.getRandom(-1.0, 0.5));
        network.getHiddenLayer().get(1).setBias(Mathematics.getRandom(-1.0, 0.5));

        //setting output  layer
        network.getOutputLayer().add(new Node());
        network.getOutputLayer().get(0).setBias(Mathematics.getRandom(-1.0, 0.5));

        //initializing hidden layer nodes weights
        network.initializeNodesWeights(network.getInputLayer(), network.getHiddenLayer());

        //calculating output layer nodes wieghts
        network.initializeNodesWeights(network.getHiddenLayer(), network.getOutputLayer());

    }

    public void jump(List<Rectangle> pipes) {

        Rectangle pipe = null;
//        horizontal distance from the start of the bird to the last edge of the pipe 
//      first neural network input
        double horizontalDistance = 3000;

        /*  vertical distance from the bottom of the bird to the top of lower pipe
           if difference is less thn make it 0
        secondd neural network input*/
        double verticalDistance;
        double temp;

//        checking the closest pipe
        for (int i = 0; i < pipes.size(); i++) {

//             check y!=0 so only lower pipes will be checked 
            if (pipes.get(i).y != 0) {
//            ending position of pipe - starting position of bird
                temp = (pipes.get(i).x + pipes.get(i).width) - rectangle.x;

//            temp should be greater thn zero so removed pipes wont be counted
                if (temp >= 0 && temp < horizontalDistance) {

                    horizontalDistance = temp;

                    pipe = pipes.get(i);
                }
            }
        }

//        now we have the closest pipe in variable pipe
//      calculate second input to neural network by calculating vertical difference
        temp = pipe.y - (rectangle.y + rectangle.height);
//        if difference is less thn 0 assign 0
        verticalDistance = temp >= 0 ? temp : 0;

//        dont know the maximu horizontal distance from bird to end of pipe so dividing by 700 analytically
        horizontalDistance = horizontalDistance / 1000;
//        minimum y position for the lower pipe will be 330 so normalizing vertical input value for ANN
        verticalDistance = verticalDistance / 700;

//        now feed forward neural netwokr with inputs, and return output
        double networkOutput = network.feedForward(horizontalDistance, verticalDistance);

//        if output> 0.5 jump otherwise not
        if (networkOutput > 0.5) {

//            jump
            yMotion = 0;
            yMotion -= 10;
        }

    }

    public static List<Bird> sortFitness(List<Bird> array) {
        int n = array.size();
        for (int j = 1; j < n; j++) {
            Bird key = array.get(j);
            int i = j - 1;
            while ((i > -1) && (Math.abs(array.get(i).getFitness()) < Math.abs(key.getFitness()))) {
                array.set(i + 1, array.get(i));
                i--;
            }
            array.set(i + 1, key);

        }
        return array;
    }

    public EvolutionaryNeuralNetwork getNetwork() {
        return network;
    }

    public void setNetwork(EvolutionaryNeuralNetwork network) {
        this.network = network;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

 

    public boolean isIsAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public int getyMotion() {
        return yMotion;
    }

    public void setyMotion(int yMotion) {
        this.yMotion = yMotion;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getProportion() {
        return proportion;
    }

    public void setProportion(double proportion) {
        this.proportion = proportion;
    }

    public double getCommulative() {
        return commulative;
    }

    public void setCommulative(double commulative) {
        this.commulative = commulative;
    }

    @Override
    public String toString() {
        return "Bird{\n" + "network=" + network + "\n  , rank=" + rank + "\n Fitness " + fitness + '}';
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    
}

class EvolutionaryAlgorithm {

    //this selection scheme is used only with population selection
    //it will return best no of individuals in sorted order
    public static ArrayList<Bird> truncationSelection(List<Bird> population, int noOfIndividualsRequired) {

        //reverse sorting population on the basis of fitness
        Bird.sortFitness(population);

        return new ArrayList<Bird>(population.subList(0, noOfIndividualsRequired));
    }

    //only for parent selection
    //it will return new array index 0  will contain 1st parent and 1 will contain 2nd parent
    public static List<Bird> randomSelection(List<Bird> population, int noOfRequiredIndividuals) {

        List<Bird> parents = new ArrayList<>();

        int random1;
        int random2;

        while (parents.size() < noOfRequiredIndividuals) {
            random1 = Mathematics.getRandom(0, population.size() - 1);
            random2 = Mathematics.getRandom(0, population.size() - 1);

//            System.out.println("ran1 " + random1 + " ran2 " + random2);
            if (random1 != random2) {
                parents.add(population.get(random1));
                parents.add(population.get(random2));

//                System.out.println("parent 1 " + parents.get(0).getFitness() + " " + parents.get(0));
//                System.out.println("parent 2 " + parents.get(1).getFitness() + " " + parents.get(1));
//                System.out.println(parents.size());
            }
        }
        return parents;
    }

    public static Bird[] rankBased(Bird[] population, int noOfRequiredIndividuals) {

        ArrayList<Bird> newRequiredPopulation = new ArrayList<>();
        int i, ranksTotal = 0;
        //sorting on the basis of fitness
        Mathematics.sort(population);

        //applying ranks
        for (i = 0; i < population.length; i++) {
            population[i].setRank(i + 1);
            ranksTotal = ranksTotal + i + 1;
        }
//        System.out.println("total ranks " + ranksTotal);

        //calculating rank proportion
        for (i = 0; i < population.length; i++) {
            population[i].setProportion(((double) population[i].getRank() / (double) ranksTotal));
//            System.out.println("Proportion " + population[i].getProportion());
        }

        //commulating
        //setting first individual commulative = to proportion
        population[0].setCommulative(population[0].getProportion());
        for (i = 1; i < population.length; i++) {
            population[i].setCommulative(population[i - 1].getCommulative() + population[i].getProportion());
//            System.out.println("Commulative " + population[i].getCommulative());

        }

        double random;
        int j;
        ///generating random number and selecting individuals for required population
        while (newRequiredPopulation.size() < noOfRequiredIndividuals) {
            random = Mathematics.getRandom();
//            System.out.println("Random " + random);
            //finding element in original population
            for (j = 0; j < population.length - 1; j++) {

                //if random number is less thn commulative of first element
                if (random < population[0].getCommulative()) {
//                    System.out.println("found elementtt " + random + " " + population[j].getCommulative() + " " + population[j]);
                    break;
                }
                //if random number is equal to commulative of any element
                if (population[j].getCommulative() == random) {
//                    System.out.println("found element " + random + " " + population[j].getCommulative() + " " + population[j]);
                    break;
                }
                //if random no fall between two numbers such that j < random >j+1 thn select j+1
                if (population[j].getCommulative() < random
                        && population[j + 1].getCommulative() >= random) {
//                    System.out.println("found elementt " + population[j].getCommulative() + " " + random + " " + population[j + 1].getCommulative() + " " + population[j + 1]);
                    j = j + 1;

                    break;
                }
            }  // j will be the position of randomly selected element on the basis of commulative value

            if (!newRequiredPopulation.contains(population[j])) {
                newRequiredPopulation.add(population[j]);
//                System.out.println("seleccted");
            }

        }

//        for (int g = 0; g < newRequiredPopulation.size(); g++) {
//            System.out.println("selected " + newRequiredPopulation.get(g).getCommulative());
//        }
        return newRequiredPopulation.toArray(new Bird[newRequiredPopulation.size()]);
    }

}
