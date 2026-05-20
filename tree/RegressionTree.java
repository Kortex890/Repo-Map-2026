package tree;

import data.Attribute;
import data.Data;
import data.DiscreteAttribute;
import data.UnknownValueException;
import utility.Keyboard;

import java.util.TreeSet;

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
		TreeSet<SplitNode> ts= new TreeSet<SplitNode>();

		int numAttributes = trainingSet.getNumberOfExplanatoryAttributes();

		for (int i = 0; i < numAttributes; i++) {
			DiscreteAttribute currentAttribute = (DiscreteAttribute) trainingSet.getExplanatoryAttribute(i);
			DiscreteNode currentNode = new DiscreteNode(trainingSet, begin, end, currentAttribute);
			ts.add(currentNode);

		}

		SplitNode bestSplitNode = ts.last();
		if (bestSplitNode != null && bestSplitNode.getAttribute() != null) {
			trainingSet.sort(bestSplitNode.getAttribute(), begin, end);
		}

		return bestSplitNode;
	}

	boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
		int currentNumberOfExamples = (end - begin) + 1;
		if (currentNumberOfExamples <= numberOfExamplesPerLeaf) {
			return true; 
		} else {
			return false; 
		}

	}
	
	public void printRules(){
		if (root instanceof LeafNode) {
			System.out.println("==> Class " + ((LeafNode) root).getPredictedClassValue());
		} else {
			SplitNode splitRoot = (SplitNode) root;
			for (int i = 0; i < childTree.length; i++) {
				String condition = splitRoot.getAttribute().getName() +
						splitRoot.getSplitInfo(i).getComparator() + // Sostituito .comparator con .getComparator()
						splitRoot.getSplitInfo(i).getSplitValue();   // Sostituito .splitValue con .getSplitValue()

				
				childTree[i].printRules(condition);
			}
		}
	}

	public void printRules(String current){
		if (root instanceof LeafNode) {
			System.out.println(current + " ==> Class " + ((LeafNode) root).getPredictedClassValue());
		} else {
			SplitNode splitRoot = (SplitNode) root;

			for (int i = 0; i < childTree.length; i++) {
				String newCondition = current + " AND " +
						splitRoot.getAttribute().getName() +
						splitRoot.getSplitInfo(i).getComparator() + // Usiamo il getter esistente in SplitInfo
						splitRoot.getSplitInfo(i).getSplitValue();   // Usiamo il getter esistente in SplitInfo

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
			if(risp == -1 || risp >= root.getNumberOfChildren()){
				throw new UnknownValueException("il valore dovrebbe essere tra 1 e "+ (root.getNumberOfChildren()-1));
			}else{
				return childTree[risp].PredictClass();
			}
		}
	}


}
