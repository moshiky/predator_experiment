package problem.learning;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * Created by Lev Levin on 03/01/2017.
 */
public class ShortHashTable<Tkey> {
    private short[] values;

    public ShortHashTable(int size){
        this.values = new short[size];
    }

    public short get(Tkey key) {
        int index = key.hashCode();
        return this.values[index];
    }

    public void put(Tkey key, short value){
        int index = key.hashCode();
        this.values[index] = value;
    }

    public void reset(){
        for (int i = 0; i < values.length; i++){
            values[i] = 0;
        }
    }
}
