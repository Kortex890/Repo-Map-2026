package tree;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;
import java.io.Serializable;

public class DiscreteNode extends SplitNode implements Serializable{

    public DiscreteNode(Data trainingSet, int beginExampelIndex, int endExampleIndex, DiscreteAttribute attribute){
        super(trainingSet,beginExampelIndex,endExampleIndex,attribute);
    }

    void setSplitInfo(Data trainingSet, int beginExampelIndex, int endExampleIndex, Attribute attribute) {
        trainingSet.sort(attribute,beginExampelIndex, endExampleIndex);

        
        int numSplits = 1;
        String currentValue = (String) trainingSet.getExplanatoryValue(beginExampelIndex, attribute.getIndex());

        for (int i = beginExampelIndex + 1; i <= endExampleIndex; i++) {
            String nextValue = (String) trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if (!currentValue.equals(nextValue)) {
                numSplits++; // Il valore è cambiato, c'è un nuovo split
                currentValue = nextValue;
            }
        }

        
        this.mapSplit = new java.util.ArrayList<SplitInfo>();

        
        int splitIndex = 0; 
        int currentBegin = beginExampelIndex; 
        currentValue = (String) trainingSet.getExplanatoryValue(beginExampelIndex, attribute.getIndex());

        for (int i = beginExampelIndex + 1; i <= endExampleIndex; i++) {
            String nextValue = (String) trainingSet.getExplanatoryValue(i, attribute.getIndex());

            if (!currentValue.equals(nextValue)) {
                mapSplit.add(new SplitInfo(currentValue, currentBegin, i-1, splitIndex));

                currentBegin = i;
                currentValue = nextValue;
                splitIndex++;
            }
        }


        mapSplit.add(new SplitInfo(currentValue, currentBegin, endExampleIndex, splitIndex));
    }

    int testCondition(Object value) {

        for (int i = 0; i < mapSplit.size(); i++) {
            if (mapSplit.get(i).splitValue.equals(value)) {
                return i; // Test positivo: restituisce l'indice (posizione nell'array / numero del ramo)
            }
        }

        return -1;
    }

    SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
        SplitNode bestSplitNode = null;
        double minVariance = Double.MAX_VALUE;
        Attribute bestAttribute = null;

        int numAttributes = trainingSet.getNumberOfExplanatoryAttributes();

        for (int i = 0; i < numAttributes; i++) {
            DiscreteAttribute currentAttribute = (DiscreteAttribute) trainingSet.getExplanatoryAttribute(i);
            DiscreteNode currentNode = new DiscreteNode(trainingSet, begin, end, currentAttribute);

            if (currentNode.getVariance() < minVariance) {
                minVariance = currentNode.getVariance();
                bestSplitNode = currentNode;
                bestAttribute = currentAttribute;
            }
        }

        if (bestAttribute != null) {
            trainingSet.sort(bestAttribute, begin, end);
        }

        return bestSplitNode;
    }

    public String toString() {
        String v = "DISCRETE SPLIT\n";
        v += super.toString() + "\n";

        if (mapSplit != null) {
            for (int i = 0; i < mapSplit.size(); i++) {
                v += "child " + i + " split value" + mapSplit.get(i).comparator + mapSplit.get(i).splitValue
                        + "[Examples:" + mapSplit.get(i).beginIndex + "-" + mapSplit.get(i).endIndex + "]\n";
            }
        }
        return v;
    }
}
