package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.LinkedList;

public class Data {

	private Object data[][];
	private int numberOfExamples;
	private List<Attribute> explanatorySet;
	private ContinuousAttribute classAttribute;

	public Data(String fileName) throws TrainingDataException {
		try {
			Scanner sc;
			File inFile = new File(fileName);
			sc = new Scanner(inFile);
			String line = sc.nextLine();
			if (!line.contains("@schema"))
				throw new TrainingDataException("Errore nello schema");
			String s[] = line.split(" ");

			explanatorySet = new LinkedList<Attribute>();
			short iAttribute = 0;
			line = sc.nextLine();
			while (!line.contains("@data")) {
				s = line.split(" ");
				if (s[0].equals("@desc")) {
					if (s.length > 2) {
						// Attributo discreto: ha valori elencati
						String discreteValues[] = s[2].split(",");
						explanatorySet.add(new DiscreteAttribute(s[1], iAttribute, discreteValues));
					} else {
						// Attributo continuo: solo il nome
						explanatorySet.add(new ContinuousAttribute(s[1], iAttribute));
					}
				} else if (s[0].equals("@target")) {
					classAttribute = new ContinuousAttribute(s[1], iAttribute);
				}
				iAttribute++;
				line = sc.nextLine();
			}

			// @data 15
			numberOfExamples = Integer.parseInt(line.split(" ")[1]);

			// Popola data
			data = new Object[numberOfExamples][explanatorySet.size() + 1];
			short iRow = 0;
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				if (line.trim().isEmpty())
					continue;
				s = line.split(",");
				for (short jColumn = 0; jColumn < s.length - 1; jColumn++) {
					if (explanatorySet.get(jColumn) instanceof ContinuousAttribute)
						data[iRow][jColumn] = Double.parseDouble(s[jColumn].trim());
					else
						data[iRow][jColumn] = s[jColumn].trim();
				}
				data[iRow][s.length - 1] = Double.parseDouble(s[s.length - 1].trim());
				iRow++;
			}
			sc.close();
		} catch (FileNotFoundException e) {
			throw new TrainingDataException(e.toString());
		}
	}

	public int getNumberOfExamples() {
		return numberOfExamples;
	}

	public int getNumberOfExplanatoryAttributes() {
		return this.explanatorySet.size();
	}

	public double getClassValue(int exampleIndex) {
		return (double) this.data[exampleIndex][getNumberOfExplanatoryAttributes()];
	}

	public Attribute getExplanatorySet(int index) {
		return explanatorySet.get(index);
	}

	public ContinuousAttribute getClassAttribute() {
		return this.classAttribute;
	}

	public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
		return this.data[exampleIndex][attributeIndex];
	}

	public Attribute getExplanatoryAttribute(int index) {
		return explanatorySet.get(index);
	}

	public String toString() {
		String value = "";
		for (int i = 0; i < numberOfExamples; i++) {
			for (int j = 0; j < explanatorySet.size(); j++)
				value += data[i][j] + ",";
			value += data[i][explanatorySet.size()] + "\n";
		}
		return value;
	}

	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {
		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}

	private void swap(int i, int j) {
		Object temp;
		for (int k = 0; k < getNumberOfExplanatoryAttributes() + 1; k++) {
			temp = data[i][k];
			data[i][k] = data[j][k];
			data[j][k] = temp;
		}
	}

	private int partition(DiscreteAttribute attribute, int inf, int sup) {
		int i, j;
		i = inf;
		j = sup;
		int med = (inf + sup) / 2;
		String x = (String) getExplanatoryValue(med, attribute.getIndex());
		swap(inf, med);
		while (true) {
			while (i <= sup && ((String) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0)
				i++;
			while (((String) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0)
				j--;
			if (i < j)
				swap(i, j);
			else
				break;
		}
		swap(inf, j);
		return j;
	}

	private int partition(ContinuousAttribute attribute, int inf, int sup) {
		int i, j;
		i = inf;
		j = sup;
		int med = (inf + sup) / 2;
		Double x = (Double) getExplanatoryValue(med, attribute.getIndex());
		swap(inf, med);
		while (true) {
			while (i <= sup && ((Double) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0)
				i++;
			while (((Double) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0)
				j--;
			if (i < j)
				swap(i, j);
			else
				break;
		}
		swap(inf, j);
		return j;
	}

	private void quicksort(Attribute attribute, int inf, int sup) {
		if (sup >= inf) {
			int pos;
			if (attribute instanceof DiscreteAttribute)
				pos = partition((DiscreteAttribute) attribute, inf, sup);
			else
				pos = partition((ContinuousAttribute) attribute, inf, sup);

			if ((pos - inf) < (sup - pos + 1)) {
				quicksort(attribute, inf, pos - 1);
				quicksort(attribute, pos + 1, sup);
			} else {
				quicksort(attribute, pos + 1, sup);
				quicksort(attribute, inf, pos - 1);
			}
		}
	}
}