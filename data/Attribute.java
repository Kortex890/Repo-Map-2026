package data;

public abstract class Attribute {
    private String name;
    private int index;

    //Costruttore
    public Attribute(String nome, int indice) {
        this.name = nome;
        this.index = indice;
    }
    //restituisce il valore (in questo caso stringa) name
    public String getName() {
        return this.name;
    }
    //restituisce il valore indice (index)
    public int getIndex() {
        return this.index;
    }
}

