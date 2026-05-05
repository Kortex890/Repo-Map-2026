package data;

public class ContinuousAttribute extends Attribute{

    private double min;
    private double max;

    public ContinuousAttribute(String nome,int index){
        super(nome,index);
    }

    public void validateValue(Double value) throws UnknownValueException {
        if (value == null || Double.isNaN(value)) {
            throw new UnknownValueException("Valore mancante per l'attributo " + super.getName());
        }
        if (value < min || value > max) {
            throw new UnknownValueException("Il valore " + value + " è fuori dal range consentito [" + min + ", " + max + "] per l'attributo " + super.getName());
        }
    }


}

