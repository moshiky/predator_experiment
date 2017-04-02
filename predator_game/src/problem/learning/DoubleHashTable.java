package problem.learning;

/**
 * Created by Lev Levin on 09/01/2017.
 */
public class DoubleHashTable<Tkey> {
    private double[] values;

    public DoubleHashTable(int size){
        this.values = new double[size];
    }

    public double get(Tkey key) {
        int index = key.hashCode();
        return this.values[index];
    }

    public void put(Tkey key, double value){
        int index = key.hashCode();
        this.values[index] = value;
    }

    public void reset(){
        for (int i = 0; i < values.length; i++){
            values[i] = 0;
        }
    }
}
