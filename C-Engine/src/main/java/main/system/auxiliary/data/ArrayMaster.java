package main.system.auxiliary.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ArrayMaster<T> {

    public static boolean isNotEmpty(Object[] template) {
        if (template == null) {
            return false;
        }
        return template.length != 0;
    }

    public static int[] getIntArrayBetween(int i, int i2) {
        int size = i2 - i;
        int[] result = new int[size];
        int a = 0;
        for (int n = i; n < i2; n++) {
            result[a] = n;
            a++;
        }
        return result;
    }

    public int indexOf(T[] array, T item) {
        int i = 0;
        for (T item_ : array) {
            if (item_.equals(item)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public List<List<T>> get2dList(T[][] array) {
        List<List<T>> list = new LinkedList<>();
        for (T[] t : array) {
            list.add(Arrays.asList(t));
        }
        return list;
    }

    public Collection<Collection<T>> get2dListFrom3dArray(T[][][] array) {
        Collection<Collection<T>> list = new LinkedList<>();
        for (T[][] t : array) {
            for (T[] t1 : t) {
                list.add(Arrays.asList(t1));
            }
        }
        return list;
    }

    public boolean contains(T[] damage_mods, T unblockable) {
        for (T d: damage_mods){
            if (unblockable == null) {
                if (d == null) {
                    return true;
                }
            }
            if (unblockable.equals(d)) {
                return true;
            }
        }
        return false;
    }
    public static   void rotateMatrix_(Object[][] matrix ){
        rotateMatrix_(matrix, true);
    }
        public static   void rotateMatrix_(Object[][] matrix, boolean clockwise){
        new ArrayMaster<>().rotateMatrix(matrix, clockwise);
    }
        public   void rotateMatrix(T[][] matrix, boolean clockwise){
        if(matrix == null)
            return;
        if(matrix.length != matrix[0].length)//INVALID INPUT
            return;
        if (clockwise)
            rotateAlongDiagonal(matrix);
        else
        getTranspose(matrix);
        rotateAlongMidRow(matrix);
    }

    public String getCellsString(T[][] cells) {
        String cellsString = "";
        for (T[] sub : cells) {
            for (T sub1 : sub) {
                cellsString += sub1;
            }
            cellsString += "\n";
        }
        return cellsString;
    }
    private   void getTranspose(T[][] matrix) {
        for(int i = 0; i < matrix.length; i++){
            for(int j = i+1; j < matrix.length ; j++){
                T temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
    }

    private   void rotateAlongMidRow(T[][] matrix) {
        int len = matrix.length ;
        for(int i = 0; i < len/2; i++){
            for(int j = 0;j < len; j++){
                T temp = matrix[i][j];
                matrix[i][j] = matrix[len-1 -i][j];
                matrix[len -1 -i][j] = temp;
            }
        }
    }

//     for making it rotate clock-wise, just change the function getTranspose() to rotateAlongDiagonal() in rotateMatrix() function.
    private   void rotateAlongDiagonal(T[][] matrix) {
        int len = matrix.length;
        for(int i = 0; i < len; i++){
            for(int j = 0; j < len - 1 - i ; j++){
                T temp = matrix[i][j];
                matrix[i][j] = matrix[len -1 - j][len-1-i];
                matrix[len -1 - j][len-1-i] = temp;
            }
        }
    }

}
