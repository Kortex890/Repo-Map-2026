package tree;

import data.Data;
import java.io.Serializable;

public class LeafNode extends Node implements Serializable{
    double predictedClassValue;
    public LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
        super(trainingSet, beginExampleIndex, endExampleIndex);
        int numberOfExamplesInNode = endExampleIndex - beginExampleIndex + 1;
        double sum = 0.0;
        for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
            sum = sum + trainingSet.getClassValue(i);
        }
        this.predictedClassValue = sum / numberOfExamplesInNode;
        double sumSquaredDiff = 0.0;
        for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
            double diff = trainingSet.getClassValue(i) - this.predictedClassValue;
            sumSquaredDiff += (diff * diff);
        }
        this.variance = sumSquaredDiff / numberOfExamplesInNode;
    }

    double getPredictedClassValue() {
        return predictedClassValue;
    }

    int getNumberOfChildren(){
        return 0;
    }

    public String toString(){
        return "Foglia: " +super.toString() + "| Predicted Value"+ getPredictedClassValue();
    }

}
