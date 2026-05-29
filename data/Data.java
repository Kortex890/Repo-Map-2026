package data;

import database.Column;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.EmptySetException;
import database.Example;
import database.TableData;
import database.TableSchema;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Data {

    private List<Example> data = new ArrayList<Example>();
    private int numberOfExamples;
    private List<Attribute> explanatorySet;
    private ContinuousAttribute classAttribute;

    public Data(String tableName) throws TrainingDataException {
        DbAccess db = new DbAccess();
        try {
            db.initConnection();
            TableData tableData = new TableData(db);
            TableSchema schema = new TableSchema(db, tableName);
            
            // Controlli richiesti dalle specifiche sulle colonne
            if (schema.getNumberOfAttributes() < 2) {
                throw new TrainingDataException("La tabella deve avere almeno due colonne.");
            }
            if (!schema.getColumn(schema.getNumberOfAttributes() - 1).isNumeric()) {
                throw new TrainingDataException("L'ultimo attributo (target) deve essere numerico.");
            }
            
            // Popola la lista 'data'
            this.data = tableData.getTransazioni(tableName);
            
            // Controllo sulle tuple
            if (this.data.isEmpty()) {
                throw new TrainingDataException("La tabella ha zero tuple.");
            }
            
            // INIZIALIZZAZIONE DEGLI ATTRIBUTI MANCANTI
            this.numberOfExamples = this.data.size();
            this.explanatorySet = new ArrayList<>();
            
            // Iteriamo su tutte le colonne tranne l'ultima (che è il target)
            for (int i = 0; i < schema.getNumberOfAttributes() - 1; i++) {
                Column c = schema.getColumn(i);
                
                if (c.isNumeric()) {
                    this.explanatorySet.add(new ContinuousAttribute(c.getColumnName(), i));
                } else {
                    // Se l'attributo è discreto, recuperiamo i suoi valori distinti dal DB
                    Set<Object> distinctObjValues = tableData.getDistinctColumnValues(tableName, c);
                    
                    // Convertiamo il Set<Object> in Set<String> (TreeSet mantiene l'ordine alfabetico)
                    Set<String> distinctStrValues = new TreeSet<>();
                    for (Object v : distinctObjValues) {
                        distinctStrValues.add(v.toString());
                    }
                    
                    this.explanatorySet.add(new DiscreteAttribute(c.getColumnName(), i, distinctStrValues));
                }
            }
            
            // Inizializza l'attributo di classe (target), che sappiamo essere l'ultimo ed essere numerico
            Column targetColumn = schema.getColumn(schema.getNumberOfAttributes() - 1);
            this.classAttribute = new ContinuousAttribute(targetColumn.getColumnName(), schema.getNumberOfAttributes() - 1);
            
        } catch (DatabaseConnectionException e) {
            throw new TrainingDataException("Connessione al database fallita: " + e.getMessage());
        } catch (SQLException e) {
            throw new TrainingDataException("Errore SQL, tabella inesistente o query errata: " + e.getMessage());
        } catch (EmptySetException e) {
            throw new TrainingDataException("Tabella vuota: " + e.getMessage());
        } finally {
            db.closeConnection();
        }
    }

    public int getNumberOfExamples() {
        return numberOfExamples;
    }

    public int getNumberOfExplanatoryAttributes() {
        return this.explanatorySet.size();
    }

    public double getClassValue(int exampleIndex) {
        return (double) this.data.get(exampleIndex).get(getNumberOfExplanatoryAttributes());
    }

    public Attribute getExplanatorySet(int index) {
        return explanatorySet.get(index);
    }

    public ContinuousAttribute getClassAttribute() {
        return this.classAttribute;
    }

    public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
        return this.data.get(exampleIndex).get(attributeIndex);
    }

    public Attribute getExplanatoryAttribute(int index) {
        return explanatorySet.get(index);
    }

    public String toString() {
        String value = "";
        for (int i = 0; i < numberOfExamples; i++) {
            for (int j = 0; j < explanatorySet.size(); j++)
                value += data.get(i).get(j) + ",";
            value += data.get(i).get(explanatorySet.size()) + "\n";
        }
        return value;
    }

    public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {
        quicksort(attribute, beginExampleIndex, endExampleIndex);
    }

    private void swap(int i, int j) {
        Object temp;
        for (int k = 0; k < getNumberOfExplanatoryAttributes() + 1; k++) {
            temp = data.get(i).get(k);
            data.get(i).set(k, data.get(j).get(k));
            data.get(j).set(k, temp);
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