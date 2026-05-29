package database;

public class DatabaseConnectionException extends Exception {
	public DatabaseConnectionException() {
		super("Errore nella connessione al database");
	}

	public DatabaseConnectionException(String message) {
		super(message);
	}
}
