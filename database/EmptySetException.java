package database;

public class EmptySetException extends Exception {
	public EmptySetException() {
		super("La query ha restituito un resultset vuoto");
	}
}
