/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flappybird;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Osama 3.1 gpa
 */
public class EvolutionaryNeuralNetwork {

    private double error;
    private ArrayList<Node> inputLayer;
    private ArrayList<Node> hiddenLayer;
    private ArrayList<Node> outputLayer;

    public EvolutionaryNeuralNetwork() {

        inputLayer = new ArrayList<Node>();
        hiddenLayer = new ArrayList<Node>();
        outputLayer = new ArrayList<Node>();
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    //only for hidden and output layers
    public void initializeNodesWeights(ArrayList<Node> previousLayer, ArrayList<Node> currentLayer) {

        for (int i = 0; i < currentLayer.size(); i++) {

            for (int j = 0; j < previousLayer.size(); j++) {

                //setting hidden layer weight
                currentLayer.get(i).getInCommingWeights().add(Mathematics.getRandom(-1.0, 0.5));
            }
        }
    }

    public void calculateNodesOutput(ArrayList<Node> previousLayer, ArrayList<Node> currentLayer) {

        //for each current layer node
        for (int i = 0; i < currentLayer.size(); i++) {

            double x = 0;
            for (int j = 0; j < previousLayer.size(); j++) {
                //bias
                x = x + previousLayer.get(j).getOutput() * currentLayer.get(i).getInCommingWeights().get(j) + currentLayer.get(i).getBias();

//               double output=1/(1+(Math.exp(-x)));
            }
            // output= 1/ 1 + e^ -x
            currentLayer.get(i).setOutput(1 / (1 + (Math.
                    exp(-(x + currentLayer.get(i).getBias())))));

        }
    }

    public double feedForward(double horizontalDistance, double verticalDistance) {

        //setting input
        this.getInputLayer().get(0).addAttribute(horizontalDistance);
        this.getInputLayer().get(0).setOutput(horizontalDistance);

        this.getInputLayer().get(1).addAttribute(verticalDistance);
        this.getInputLayer().get(1).setOutput(verticalDistance);

        //calculating output
        this.calculateNodesOutput(this.getInputLayer(), this.getHiddenLayer());
        this.calculateNodesOutput(this.getHiddenLayer(), this.getOutputLayer());

        //calculating error
//            this.calculateOutputLayerError(Double.parseDouble(values[2]), this.getOutputLayer().get(0).getOutput());
//          returning network output
        return this.getOutputLayer().get(0).getOutput();

    }

    public void mutate() {
        //possibility of mutation
        if (Mathematics.getRandom() >= 0.50) {

            Double childArray[] = this.returnNetworkAsArray();

            //applying mutation 
            //now checking how much indexes to mutate
            int noOfMutations = Mathematics.getRandom(1, 9);
            //it will save indexes to mutate
            List<Integer> indexesToMutate = new ArrayList<>();

            while (indexesToMutate.size() <= noOfMutations - 1) {

                int index = Mathematics.getRandom(0, 8);

                if (!indexesToMutate.contains(index)) {
                    indexesToMutate.add(index);
                }
            }

            //applying mutation on selected indexes
            for (int k = 0; k < indexesToMutate.size(); k++) {
                //checking either positive mutation or negative
                if (Mathematics.getRandom() > 0.5) {
                    //positive mutation
                    childArray[indexesToMutate.get(k)] = (childArray[indexesToMutate.get(k)] + 0.25) > 1.0 ? 1.0 : childArray[indexesToMutate.get(k)] + 0.25;

                } else {
                    //negative mutation
                    childArray[indexesToMutate.get(k)] = (childArray[indexesToMutate.get(k)] - 0.25) < -1.0 ? -1.0 : childArray[indexesToMutate.get(k)] - 0.25;

                }
            }

            this.setNetworkFromArray(childArray);
        }
    }

    public void setNetworkFromArray(Double[] array) {

        //setting inputs
        //initializing hidden layer
        this.hiddenLayer.get(0).getInCommingWeights().set(0, array[0]);//get(0);
        this.hiddenLayer.get(0).getInCommingWeights().set(1, array[1]);//get(1);
        this.hiddenLayer.get(1).getInCommingWeights().set(0, array[2]);//.get(0);
        this.hiddenLayer.get(1).getInCommingWeights().set(1, array[3]);///.get(1);
        //setting hidden layer biases
        this.hiddenLayer.get(0).setBias(array[6]);
        this.hiddenLayer.get(1).setBias(array[7]);

        //setting outputlayer weights
        this.outputLayer.get(0).getInCommingWeights().set(0, array[4]);
        this.outputLayer.get(0).getInCommingWeights().set(1, array[5]);

        this.outputLayer.get(0).setBias(array[8]);

    }

    public Double[] returnNetworkAsArray() {

        Double[] array = new Double[9];
//        array[0] = this.inputLayer.get(0).getInCommingWeights().get(0);
//        array[1] = this.inputLayer.get(1).getInCommingWeights().get(0);
//        array[2] = this.inputLayer.get(2).getInCommingWeights().get(0);
        array[0] = hiddenLayer.get(0).getInCommingWeights().get(0);
        array[1] = hiddenLayer.get(0).getInCommingWeights().get(1);
        array[2] = hiddenLayer.get(1).getInCommingWeights().get(0);
        array[3] = hiddenLayer.get(1).getInCommingWeights().get(1);
        array[4] = outputLayer.get(0).getInCommingWeights().get(0);
        array[5] = outputLayer.get(0).getInCommingWeights().get(1);
        array[6] = hiddenLayer.get(0).getBias();
        array[7] = hiddenLayer.get(1).getBias();
        array[8] = outputLayer.get(0).getBias();

        return array;
    }

    public void makeNetworkFromArray(Double[] array) {

        //creating initial nodes  
        this.getInputLayer().add(new Node());
        this.getInputLayer().add(new Node());

        //setting hidden layer
        this.getHiddenLayer().add(new Node());
        this.getHiddenLayer().add(new Node());

        //initializing hidden layer
        this.hiddenLayer.get(0).getInCommingWeights().add(array[0]);//get(0);
        this.hiddenLayer.get(0).getInCommingWeights().add(array[1]);//get(1);
        this.hiddenLayer.get(1).getInCommingWeights().add(array[2]);//.get(0);
        this.hiddenLayer.get(1).getInCommingWeights().add(array[3]);///.get(1);
        //setting hidden layer biases
        this.hiddenLayer.get(0).setBias(array[6]);
        this.hiddenLayer.get(1).setBias(array[7]);

        //setting output  layer
        this.getOutputLayer().add(new Node());
        //setting outputlayer weights
        this.outputLayer.get(0).getInCommingWeights().add(array[4]);
        this.outputLayer.get(0).getInCommingWeights().add(array[5]);

        this.getOutputLayer().get(0).setBias(array[8]);

    }

    public void crossOver(List<Bird> parents, boolean firstPartFromFirstParent) {

        Double parent1[] = parents.get(0).getNetwork().returnNetworkAsArray();
        Double parent2[] = parents.get(1).getNetwork().returnNetworkAsArray();

        //copy first part from first parent
        if (firstPartFromFirstParent) {

            Double[] child = new Double[9];

            child[0] = parent1[0];
            child[1] = parent1[1];
            child[2] = parent1[2];
            child[3] = parent1[3];
            child[4] = parent1[4];

            child[5] = parent2[5];
            child[6] = parent2[6];
            child[7] = parent2[7];
            child[8] = parent2[8];
//            child[11] = parent2[11];
//            child[12] = parent2[12];
//            child[13] = parent2[13];

            this.makeNetworkFromArray(child);
        } else {

            Double[] child = new Double[9];

            child[0] = parent2[0];
            child[1] = parent2[1];
            child[2] = parent2[2];
            child[3] = parent2[3];
            child[4] = parent2[4];

            child[5] = parent1[5];
            child[6] = parent1[6];
            child[7] = parent1[7];
            child[8] = parent1[8];

            this.makeNetworkFromArray(child);
        }

    }

    public ArrayList<Node> getInputLayer() {
        return inputLayer;
    }

    public void setInputLayer(ArrayList<Node> inputLayer) {
        this.inputLayer = inputLayer;
    }

    public ArrayList<Node> getHiddenLayer() {
        return hiddenLayer;
    }

    public void setHiddenLayer(ArrayList<Node> hiddenLayer) {
        this.hiddenLayer = hiddenLayer;
    }

    public ArrayList<Node> getOutputLayer() {
        return outputLayer;
    }

 

    public void setOutputLayer(ArrayList<Node> outputLayer) {
        this.outputLayer = outputLayer;
    }

    

    public String toString() {

        return /*"input1 " + inputLayer.get(0).getInCommingWeights().get(0) + " \n"+
                 "input2 " + inputLayer.get(1).getInCommingWeights().get(0) + " \n"+*/
                "w14 " + hiddenLayer.get(0).getInCommingWeights().get(0) + " \n"
                + "w24 " + hiddenLayer.get(0).getInCommingWeights().get(1) + " \n"
                + "w15 " + hiddenLayer.get(1).getInCommingWeights().get(0) + " \n"
                + "w25 " + hiddenLayer.get(1).getInCommingWeights().get(1) + " \n"
                + "w46 " + outputLayer.get(0).getInCommingWeights().get(0) + " \n"
                + "w56 " + outputLayer.get(0).getInCommingWeights().get(1) + " \n"
                + "bias 4 " + hiddenLayer.get(0).getBias() + "\n"
                + "bias 5 " + hiddenLayer.get(1).getBias() + "\n"
                + "bias 6 " + outputLayer.get(0).getBias() + "\n"
                + "Output " + outputLayer.get(0).getOutput() + "\n"
                + "Target Output 0.6002295" + " \n"
                + "Error " + this.getError();
    }
}

//initial values will also be stored in inComming wights array
class Node {

    double output;
    double error;
    double bias;
    ArrayList<Double> inCommingWeights;

    public Node() {
        this.inCommingWeights = new ArrayList<>();
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public ArrayList<Double> getInCommingWeights() {
        return inCommingWeights;
    }

    public void setInCommingWeights(ArrayList<Double> inCommingWeights) {
        this.inCommingWeights = inCommingWeights;
    }

//    public void addAttribute(int inCommingIndex, double weight){
//        this.inCommingWeights.add(new Weights(inCommingIndex,weight));
//    }
    public void addAttribute(double weight) {
        this.inCommingWeights.add(weight);
    }

    public void setAttribute(double weight, int index) {
        this.inCommingWeights.set(index, weight);
    }

    public void addHiddenLayerNodeWeight() {

    }

    public void calculateHiddenNodeInput() {

    }

}



class Mathematics {

    public static DecimalFormat f = new DecimalFormat("##.000");
    static Random random = null;

    public Mathematics() {
    }

    public static Random getInstance() {

        if (random == null) {
            random = new Random(System.currentTimeMillis());
        }

        return random;
    }

     public static void sort(Bird[] array) {
        int n = array.length;
        for (int j = 1; j < n; j++) {
            Bird key = array[j];
            int i = j - 1;
            while ((i > -1) && (array[i].getFitness() > key.getFitness())) {
                array[i + 1] = array[i];
                i--;
            }
            array[i + 1] = key;
        }

    }
     
    //get double random number between 0.0(inclusive) and 1.0 (exclusive)
    public static double getRandom() {

        return Mathematics.roundDoubleTo3DecimalPosition(Mathematics.getInstance().nextDouble());
    }

    //get int random in min max range
    public static int getRandom(int min, int max) {

        return Mathematics.getInstance().nextInt(max + 1 - min) + min;
    }

    //get double random in min max range 
    public static double getRandom(double min, double max) {

        return min + (max - min) * Mathematics.getInstance().nextDouble();

    }

    public static double roundDoubleTo3DecimalPosition(Double d) {
        return Double.parseDouble(f.format(d));
    }
}
