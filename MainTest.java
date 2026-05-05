import data.*;
import tree.*;
import utility.Keyboard;

import java.io.FileNotFoundException;

class MainTest {

	public static void main(String[] args) {
		char repeat;
		do {
			System.out.println("Training set:");
			String fileName = Keyboard.readString();

			try {
				System.out.println("Starting data acquisition phase!");
				Data trainingSet = new Data(fileName);

				System.out.println("Starting learning phase!");
				RegressionTree tree = new RegressionTree(trainingSet);

				tree.printRules();
				tree.printTree();

				char repeatPrediction;
				do {
					System.out.println("Starting prediction phase!");
					try {
						double prediction = tree.PredictClass();
						System.out.println(prediction);
					} catch (UnknownValueException e) {
						System.err.println(e.getMessage());
					}

					System.out.println("Would you repeat? (y/n)");
					repeatPrediction = Keyboard.readChar();
				} while (Character.toLowerCase(repeatPrediction) == 'y');

			} catch (TrainingDataException e) {
				System.err.println("data.TrainingDataException: " + e.getMessage());
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}

			System.out.println("Would you learn a new tree? (y/n)");
			repeat = Keyboard.readChar();
		} while (Character.toLowerCase(repeat) == 'y');
	}
}
