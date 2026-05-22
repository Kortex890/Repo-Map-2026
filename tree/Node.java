package tree;

import data.Data;
import java.io.Serializable;

public abstract class Node implements Serializable{
    static int idNodeCount = 0;
    int id_Node;
    int BeginExampleIndex;
    int endExampleindex;
    double variance;

    Node(Data trainingsSet, int BeginExampleIndex, int endExampleindex) {
        this.id_Node = 0;
        this.BeginExampleIndex = BeginExampleIndex;
        this.endExampleindex = endExampleindex;
        this.variance = 0.0;
    }

    int getNode() {
        return id_Node;
    }

    int getBeginExampleIndex() {
        return BeginExampleIndex;
    }

    int getEndExampleindex() {
        return endExampleindex;
    }

    double getVariance() {
        return variance;
    }
    abstract int getNumberOfChildren();
    @Override
    public String toString() {
        return " "+BeginExampleIndex+
                ","+ endExampleindex +
                ","+ variance;
    }
}