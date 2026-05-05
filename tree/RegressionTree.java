package tree;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;
import data.UnknownValueException;
import utility.Keyboard;

public class RegressionTree {
	Node root;
	RegressionTree childTree[];
	public RegressionTree(Data trainingSet){
		learnTree(trainingSet,0,trainingSet.getNumberOfExamples()-1,trainingSet.getNumberOfExamples()*10/100);
	}
	public RegressionTree(){

	}
	void learnTree(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf){
		if( isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)){
			//determina la classe che compare pi� frequentemente nella partizione corrente
			root=new LeafNode(trainingSet,begin,end);
		}
		else //split node
		{
			root=determineBestSplitNode(trainingSet, begin, end);

			if(root.getNumberOfChildren()>1){
				childTree=new RegressionTree[root.getNumberOfChildren()];
				for(int i=0;i<root.getNumberOfChildren();i++){
					childTree[i]=new RegressionTree();
					childTree[i].learnTree(trainingSet, ((SplitNode)root).getSplitInfo(i).beginIndex, ((SplitNode)root).getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
				}
			}
			else
				root=new LeafNode(trainingSet,begin,end);

		}
	}



	public void printTree(){
		System.out.println("********* TREE **********\n");
		System.out.println(toString());
		System.out.println("*************************\n");
	}

	public String toString(){
		String tree=root.toString()+"\n";
		if( root instanceof LeafNode){
		}
		else //split node
		{
			for(int i=0;i<childTree.length;i++)
				tree +=childTree[i];
		}
		return tree;

	}

	SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {

		SplitNode bestSplitNode = null;
		double minVariance = Double.MAX_VALUE; // Partiamo dal valore più alto possibile
		Attribute bestAttribute = null; // Memorizziamo l'attributo migliore per l'ordinamento finale

		int numAttributes = trainingSet.getNumberOfExplanatoryAttributes();

		// 1. Per ciascun attributo indipendente...
		for (int i = 0; i < numAttributes; i++) {

			// Assumiamo che gli attributi esplicativi siano discreti in questa fase
			DiscreteAttribute currentAttribute = (DiscreteAttribute) trainingSet.getExplanatoryAttribute(i);

			// 2. Istanzia il tree.DiscreteNode associato
			DiscreteNode currentNode = new DiscreteNode(trainingSet, begin, end, currentAttribute);

			// 3. Seleziona il nodo di split con minore varianza
			if (currentNode.getVariance() < minVariance) {
				minVariance = currentNode.getVariance();
				bestSplitNode = currentNode;
				bestAttribute = currentAttribute;
			}
		}

		// 4. Ordina la porzione di trainingSet corrente rispetto all'attributo indipendente del nodo di split selezionato
		if (bestAttribute != null) {
			trainingSet.sort(bestAttribute, begin, end);
		}

		// 5. Restituisce il nodo selezionato
		return bestSplitNode;
	}

	boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
		int currentNumberOfExamples = (end - begin) + 1;
		if (currentNumberOfExamples <= numberOfExamplesPerLeaf) {
			return true; // È un nodo foglia
		} else {
			return false; // Non è un nodo foglia (può essere ancora splittato)
		}

	}
	// Scandisce ciascun ramo dell'albero completo dalla radice alla foglia/
	public void printRules() {
		if (root instanceof LeafNode) {
			// Caso limite: l'intero albero è solo una foglia
			System.out.println("==> Class " + ((LeafNode) root).getPredictedClassValue());
		} else {
			// La radice è un nodo di split, avviamo l'attraversamento
			SplitNode splitRoot = (SplitNode) root;

			for (int i = 0; i < childTree.length; i++) {
				// Recuperiamo le informazioni del ramo corrente per iniziare la stringa
				// (es. "motor=A")
				String condition = splitRoot.getAttribute().getName() +
						splitRoot.getSplitInfo(i).comparator +
						splitRoot.getSplitInfo(i).splitValue;

				// Invochiamo il metodo ricorsivo sul figlio passandogli la prima condizione
				childTree[i].printRules(condition);
			}
		}
	}

	public void printRules(String current){
		if (root instanceof LeafNode) {
			// Siamo arrivati alla foglia: termina l'attraversamento visualizzando la regola finale
			System.out.println(current + " ==> Class " + ((LeafNode) root).getPredictedClassValue());
		} else {
			// Siamo in un nodo di split: dobbiamo concatenare e scendere ancora
			SplitNode splitRoot = (SplitNode) root;

			for (int i = 0; i < childTree.length; i++) {
				// Aggiungiamo " AND " e la nuova condizione alla stringa current ereditata dal padre
				String newCondition = current + " AND " +
						splitRoot.getAttribute().getName() +
						splitRoot.getSplitInfo(i).comparator +
						splitRoot.getSplitInfo(i).splitValue;

				// Chiamata ricorsiva al livello inferiore
				childTree[i].printRules(newCondition);
			}
		}
	}

	public double PredictClass() throws UnknownValueException{
		if(root instanceof LeafNode){
			return ((LeafNode)root).getPredictedClassValue();
		}else{
			int risp;
			System.out.println(((SplitNode)root).formulateQuery());
			risp = Keyboard.readInt();
			if(risp == -1 || risp >= root.getNumberOfChildren()-1){
				throw new UnknownValueException("il valore dovrebbe essere tra 1 e "+ (root.getNumberOfChildren()-1));
			}else{
				return childTree[risp].PredictClass();
			}
		}
	}


}
