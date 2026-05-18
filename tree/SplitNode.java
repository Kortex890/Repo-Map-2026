package tree;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;
import java.util.List;
import java.util.ArrayList;

abstract class SplitNode extends Node implements Comparable<SplitNode>{
	// Classe che colelzione informazioni descrittive dello split
	class SplitInfo{
		Object splitValue;
		int beginIndex;
		int endIndex;
		int numberChild;
		String comparator="=";
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
		}
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild, String comparator){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
			this.comparator=comparator;
		}
		int getBeginindex(){
			return beginIndex;			
		}
		int getEndIndex(){
			return endIndex;
		}
		 Object getSplitValue(){
			return splitValue;
		}
		public String toString(){
			return "child " + numberChild +" split value"+comparator+splitValue + "[Examples:"+beginIndex+"-"+endIndex+"]";
		}
		 String getComparator(){
			return comparator;
		}
	
		
	}

	Attribute attribute;

	List<SplitInfo> mapSplit=new ArrayList<SplitInfo>();
	
	double splitVariance;

	abstract void setSplitInfo(Data trainingSet, int beginExampelIndex, int endExampleIndex, Attribute attribute);

	abstract int testCondition (Object value);

	public int compareTo(SplitNode o) {
		if (this.splitVariance < o.splitVariance) {
			return 1;  // Minore varianza residua significa maggiore Information Gain
		} else if (this.splitVariance > o.splitVariance) {
			return -1; // Maggiore varianza residua significa minore Information Gain
		} else {
			return 0;  // Strutture con varianza identica
		}
	}

	SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
		super(trainingSet, beginExampleIndex, endExampleIndex);
		this.attribute = attribute;
		trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
		setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);

		//compute variance
		splitVariance = 0;
		for (int i = 0; i < mapSplit.size(); i++) {
			double localVariance = new LeafNode(trainingSet, mapSplit.get(i).getBeginindex(), mapSplit.get(i).getEndIndex()).getVariance();
			splitVariance += (localVariance);
		}
	}

	Attribute getAttribute(){
		return attribute;
	}
	
	double getVariance(){
		return splitVariance;
	}
	
	int getNumberOfChildren(){
		return mapSplit.size();
	}

	SplitInfo getSplitInfo(int child){
		return mapSplit.get(child);
	}

	String formulateQuery(){
		String query = "";
		for(int i=0;i<mapSplit.size();i++)
			query+= (i + ":" + attribute + mapSplit.get(i).getComparator() +mapSplit.get(i).getSplitValue())+"\n";
		return query;
	}
	SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
		SplitNode bestSplitNode = null;
		double minVariance = Double.MAX_VALUE; // Inizializziamo al valore massimo possibile
		Attribute bestAttribute = null; // Ci serve per ricordare su quale attributo ordinare alla fine
			// Otteniamo il numero di attributi indipendenti dal dataset
		int numAttributes = trainingSet.getNumberOfExplanatoryAttributes();

			// 1. Per ciascun attributo indipendente...
		for (int i = 0; i < numAttributes; i++) {
			// Estraiamo l'attributo corrente (assumiamo siano discreti come da traccia precedente)
			DiscreteAttribute currentAttribute = (DiscreteAttribute) trainingSet.getExplanatoryAttribute(i);

			// 2. Istanziamo il tree.DiscreteNode associato
			// NOTA: Assumiamo che il costruttore richieda (dataset, inizio, fine, attributo)
			// e che la varianza venga calcolata automaticamente al suo interno.
			DiscreteNode currentNode = new DiscreteNode(trainingSet, begin, end, currentAttribute);

			// 3. Selezioniamo il nodo con minore varianza
			if (currentNode.getVariance() < minVariance) {
				minVariance = currentNode.getVariance();
				bestSplitNode = currentNode;
				bestAttribute = currentAttribute;
			}
		}

			// 4. Ordina la porzione di trainingSet corrente rispetto all'attributo del nodo selezionato
			if (bestAttribute != null) {
				trainingSet.sort(bestAttribute, begin, end);
			}

			// 5. Restituisce il nodo selezionato
			return bestSplitNode;
		}

	public String toString(){
		String v= "SPLIT : attribute=" +attribute +" "+ super.toString()+  " Split Variance: " + getVariance()+ "\n" ;

		for(int i=0;i<mapSplit.size();i++){
			v+= "\t"+mapSplit.get(i)+"\n";
		}

		return v;
	}

	// Implementazione del confronto biologico basato sulla varianza dello split


}
