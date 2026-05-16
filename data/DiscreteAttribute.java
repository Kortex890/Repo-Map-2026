package data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class DiscreteAttribute extends Attribute{
    private Set<String> values=new TreeSet<>();

    public DiscreteAttribute(String name, int index, String value[]){
        super(name, index);
        this.values = values;
    }

    public int getNumberofDistinctValues(){
        return values.size();

    }

    @Override
    public Iterator<String> iterator(){
        return values.iterator();
        }
}

