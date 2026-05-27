package tree;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ContinuousNode extends SplitNode implements Serializable {

    public ContinuousNode(Data trainingSet, int beginExampleIndex,
            int endExampleIndex, ContinuousAttribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    @Override
    void setSplitInfo(Data trainingSet, int beginExampleIndex,
            int endExampleIndex, Attribute attribute) {
        Double currentSplitValue = (Double) trainingSet
                .getExplanatoryValue(beginExampleIndex, attribute.getIndex());
        double bestInfoVariance = Double.MAX_VALUE;
        List<SplitInfo> bestMapSplit = null;

        for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
            Double value = (Double) trainingSet
                    .getExplanatoryValue(i, attribute.getIndex());

            if (value.doubleValue() != currentSplitValue.doubleValue()) {
                double candidateSplitVariance = new LeafNode(trainingSet, beginExampleIndex, i - 1).getVariance()
                        + new LeafNode(trainingSet, i, endExampleIndex).getVariance();

                if (bestMapSplit == null) {
                    bestMapSplit = new ArrayList<>();
                    bestMapSplit.add(new SplitInfo(currentSplitValue,
                            beginExampleIndex, i - 1, 0, "<="));
                    bestMapSplit.add(new SplitInfo(currentSplitValue,
                            i, endExampleIndex, 1, ">"));
                    bestInfoVariance = candidateSplitVariance;
                } else if (candidateSplitVariance < bestInfoVariance) {
                    bestInfoVariance = candidateSplitVariance;
                    bestMapSplit.set(0, new SplitInfo(currentSplitValue,
                            beginExampleIndex, i - 1, 0, "<="));
                    bestMapSplit.set(1, new SplitInfo(currentSplitValue,
                            i, endExampleIndex, 1, ">"));
                }
                currentSplitValue = value;
            }
        }

        mapSplit = bestMapSplit;

        // Controllo null: se tutti i valori erano uguali, mapSplit è null
        if (mapSplit != null && mapSplit.size() > 1) {
            if (mapSplit.get(1).beginIndex == mapSplit.get(1).getEndIndex()) {
                mapSplit.remove(1);
            }
        }
    }

    @Override
    int testCondition(Object value) {
        if (mapSplit == null || mapSplit.isEmpty())
            return -1;
        Double soglia = (Double) mapSplit.get(0).getSplitValue();
        Double v = (Double) value;
        return (v <= soglia) ? 0 : 1;
    }

    @Override
    public String toString() {
        return "CONTINUOUS SPLIT\n" + super.toString() + "\n";
    }
}