package tree;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;

public class DiscreteNode extends SplitNode{

    public DiscreteNode(Data trainingSet, int beginExampelIndex, int endExampleIndex, DiscreteAttribute attribute){
        super(trainingSet,beginExampelIndex,endExampleIndex,attribute);
    }

    void setSplitInfo(Data trainingSet, int beginExampelIndex, int endExampleIndex, Attribute attribute) {
        trainingSet.sort(attribute,beginExampelIndex, endExampleIndex);

        // 2. Contiamo quanti valori discreti DIVERSI ci sono in questo sotto-insieme.
        // Questo ci serve per sapere quanto deve essere grande l'array mapSplit.
        int numSplits = 1;
        String currentValue = (String) trainingSet.getExplanatoryValue(beginExampelIndex, attribute.getIndex());

        for (int i = beginExampelIndex + 1; i <= endExampleIndex; i++) {
            String nextValue = (String) trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if (!currentValue.equals(nextValue)) {
                numSplits++; // Il valore è cambiato, c'è un nuovo split
                currentValue = nextValue;
            }
        }

        // 3. Inizializziamo la List mapSplit istanziando un ArrayList vuoto (la dimensione sarà dinamica)
        this.mapSplit = new java.util.ArrayList<SplitInfo>();

        // 4. Scorriamo di nuovo i dati per creare e salvare gli oggetti SplitInfo
        int splitIndex = 0; // Indice del figlio corrente
        int currentBegin = beginExampelIndex; // Indizio di inizio del blocco corrente
        currentValue = (String) trainingSet.getExplanatoryValue(beginExampelIndex, attribute.getIndex());

        for (int i = beginExampelIndex + 1; i <= endExampleIndex; i++) {
            String nextValue = (String) trainingSet.getExplanatoryValue(i, attribute.getIndex());

            if (!currentValue.equals(nextValue)) {
                // Il valore è cambiato (es. passiamo da 'A' a 'B').
                // Chiudiamo il blocco precedente creando uno SplitInfo.
                // Il costruttore richiede: valore, inizio, fine, id del figlio
                mapSplit.add(new SplitInfo(currentValue, currentBegin, i-1, splitIndex));

                // Aggiorniamo le variabili per iniziare il tracciamento del nuovo blocco
                currentBegin = i;
                currentValue = nextValue;
                splitIndex++;
            }
        }

        // 5. Fuori dal ciclo, dobbiamo aggiungere l'ultimo blocco residuo
        // (es. le 'D' finali nella tua tabella)

        mapSplit.add(new SplitInfo(currentValue, currentBegin, endExampleIndex, splitIndex));
    }

    int testCondition(Object value) {

        // Scorre tutti gli oggetti SplitInfo collezionati nell'array
        for (int i = 0; i < mapSplit.size(); i++) {

            // Confronta il valore in input con lo splitValue salvato
            // Usiamo .equals() perché stiamo confrontando degli Object (generalmente Stringhe per i valori discreti)
            if (mapSplit.get(i).splitValue.equals(value)) {
                return i; // Test positivo: restituisce l'indice (posizione nell'array / numero del ramo)
            }
        }

        // Fallback di sicurezza: se per qualche motivo il valore non esiste nei rami,
        // ritorniamo -1 per indicare che non è stato trovato alcun match.
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
