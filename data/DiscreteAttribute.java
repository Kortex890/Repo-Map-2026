package data;

public class DiscreteAttribute extends Attribute{
    private String values[];

    public DiscreteAttribute(String name, int index, String value[]){
        super(name, index);
        this.values = value;
    }

    public int getNumberofDistinctValues(){
        return this.values.length;

    }

    public String getValue(int i){
        return this.values[i];
    }
}

