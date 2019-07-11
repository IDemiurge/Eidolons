package main.system.auxiliary.data;


import main.entity.obj.Obj;
import main.system.auxiliary.StringMaster;

import java.util.*;

public class ArrayMaster<T> {

    public static String[] getFilledStringArray(int instances, String defaultData) {
        String[] array = new String[instances];
        List<String> list = new ArrayList<>();
        while (instances > 0) {
            list.add(defaultData);
            instances--;
        }
        return list.toArray(array);
    }

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

    public static void rotateMatrix_(Object[][] matrix) {
        rotateMatrix_(matrix, true);
    }

    public static void rotateMatrix_(Object[][] matrix, boolean clockwise) {
        new ArrayMaster<>().rotateMatrix(matrix, clockwise);
    }

    public static String[][] rotateMatrixInPlaceClockwise(String[][] matrix) {
        return new ArrayMaster<String>().rotateMatrixInPlaceClockwise_(matrix);
    }

    public static String[][] rotateMatrixInPlaceAnticlockwise(String[][] matrix) {
        return new ArrayMaster<String>().rotateMatrixInPlaceAnticlockwise_(matrix);
    }

    public static String[][] flip(String[][] matrix, boolean hor, boolean vert) {
        String[][] flipped = new String[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[0].length; ++j) {
                int i1 = i;
                int j1 = j;
                if (hor) {
                    i1 = matrix.length - i - 1;
                }
                if (vert)
                    j1 = matrix[0].length - j - 1;

                flipped[i1][j1] = matrix[i][j];
            }
        }
        return flipped;
    }

    public static String[][] rotateStringMatrixClockwise(String[][] matrix) {
        String[][] rotated = new String[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix[0].length; ++i) {
            for (int j = 0; j < matrix.length; ++j) {
                rotated[i][j] = matrix[matrix.length - j - 1][i];
            }
        }
        return rotated;
    }

    public static String[][] rotateStringMatrixAnticlockwise(String[][] matrix) {
        String[][] rotated = new String[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix[0].length; ++i) {
            for (int j = 0; j < matrix.length; ++j) {
                rotated[i][j] = matrix[j][matrix[0].length - i - 1];
            }
        }
        return rotated;
    }

    public static Integer[][] rotateIntegerMatrixClockwise(Integer[][] matrix) {
        Integer[][] rotated = new Integer[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix[0].length; ++i) {
            for (int j = 0; j < matrix.length; ++j) {
                rotated[i][j] = matrix[matrix.length - j - 1][i];
            }
        }
        return rotated;
    }

    public static Integer[][] rotateIntegerMatrixAnticlockwise(Integer[][] matrix) {
        Integer[][] rotated = new Integer[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix[0].length; ++i) {
            for (int j = 0; j < matrix.length; ++j) {
                rotated[i][j] = matrix[j][matrix[0].length - i - 1];
            }
        }
        return rotated;
    }

    public static String[][] cloneMatrix(String[][] matrix) {
        String[][] clone = new String[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[0].length; ++j) {
                clone[i][j] = matrix[i][j];
            }
        }

        return clone;
    }

    public static boolean within2D(Object[][] cells, int x, int y) {
        if (x < 0)
            return false;
        if (y < 0)
            return false;
        if (cells.length <= x)
            return false;
        if (cells[x].length <= y)
            return false;

        return true;
    }

    public static Integer[][] rotate(boolean clockwise, Integer[][] matrix) {
        if (matrix.length == matrix[0].length)
            return clockwise ? new ArrayMaster<Integer>().rotateMatrixInPlaceClockwise_(matrix) :
                    new ArrayMaster<Integer>().rotateMatrixInPlaceAnticlockwise_(matrix);

        return clockwise ? rotateIntegerMatrixClockwise(matrix) :
                rotateIntegerMatrixAnticlockwise(matrix);
    }

    public static String[][] cloneString2d(String[][] cells) {
        String[][] clone = new String[cells.length][cells[0].length];
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                clone[x][y] = cells[x][y];
            }
        }
        return clone;
    }

    private static boolean[][][][] booleanFalseArrays = new boolean[100][100][1][1]; // speed up clearshot if we can

    public static void resetBufferArray() {
        booleanFalseArrays = new boolean[100][100][1][1];
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                booleanFalseArrays[i][j] = new boolean[i][j];
            }
        }
    }

//    public static boolean  isClearshotCached(  Obj source,
//                                                   Obj target) {
//       result= cacheMap.get(getKey(source, target))!=null ;
//        cacheMap.put(getKey(source, target), true);
//        return result;
//    }

    public static boolean[][] getBooleanFalseArray(int i, int i1) {
//        main.system.auxiliary.log.LogMaster.log(1, i + ":" + i1);
        return booleanFalseArrays[i][i1];
    }
    /**
     * This method rotates the matrix 90 degrees counter clockwise without using extra buffer..
     */
    public T[][] rotateMatrixInPlaceClockwise_(T[][] matrix) {


        int n = matrix[0].length;
        T tmp;
        for (int i = 0; i < n / 2; i++) {
            for (int j = i; j < n - i - 1; j++) {
                tmp = matrix[i][j];
                matrix[i][j] = matrix[j][n - i - 1];
                matrix[j][n - i - 1] = matrix[n - i - 1][n - j - 1];
                matrix[n - i - 1][n - j - 1] = matrix[n - j - 1][i];
                matrix[n - j - 1][i] = tmp;
            }

        }
        return matrix;
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

    public static boolean contains_(Object[] array, Object t) {
        return new ArrayMaster<>().contains(array, t);
    }

    public boolean contains(T[] array, T t) {
        for (T d : array) {
            if (t == null) {
                if (d == null) {
                    return true;
                }
            }
            if (t.equals(d)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method rotates the matrix 90 degrees counter clockwise without using extra buffer..
     */
    public T[][] rotateMatrixInPlaceAnticlockwise_(T[][] matrix) {
        int n = matrix.length;
        T top;
        for (int i = 0; i < n / 2; i++) {

            for (int j = i; j < n - i - 1; j++) {

                top = matrix[i][j];
                matrix[i][j] = matrix[n - j - 1][i];
                matrix[n - j - 1][i] = matrix[n - i - 1][n - j - 1];
                matrix[n - i - 1][n - j - 1] = matrix[j][n - i - 1];
                matrix[j][n - i - 1] = top;

            }

        }
        return matrix;
    }

    public String[][] rotate2dStringArray(String[][] matrix, boolean clockwise) {
//        if (matrix.length == matrix[0].length)
//            return clockwise ? rotateMatrixInPlaceClockwise(matrix) :
//             rotateMatrixInPlaceAnticlockwise(matrix);
        return clockwise ? rotateStringMatrixClockwise(matrix) :
                rotateStringMatrixAnticlockwise(matrix);
    }

    public Object[][] rotateMatrix(Object[][] matrix, boolean clockwise) {
        if (matrix == null)
            return matrix;
        int w = matrix.length;
        int h = matrix[0].length;
        if (w != h) {
            matrix = new String[h][w]; //how to deal with this?
        }
        if (clockwise)
            rotateAlongDiagonal(matrix);
        else
            getTranspose(matrix);
        rotateAlongMidRow(matrix);
        return matrix;
    }

    public String getCellsString(Object[][] cells) {
        String cellsString = "";
        for (Object[] sub : cells) {
            for (Object sub1 : sub) {
                cellsString += sub1;
            }
            cellsString += "\n";
        }
        return cellsString;
    }

    private void getTranspose(Object[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix[0].length; j++) {
                Object temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
    }

    private void rotateAlongMidRow(Object[][] matrix) {
        int len = matrix.length;
        for (int i = 0; i < len / 2; i++) {
            for (int j = 0; j < len; j++) {
                Object temp = matrix[i][j];
                matrix[i][j] = matrix[len - 1 - i][j];
                matrix[len - 1 - i][j] = temp;
            }
        }
    }

    //     for making it rotate clock-wise, just change the function getTranspose() to rotateAlongDiagonal() in rotateMatrix() function.
    private void rotateAlongDiagonal(Object[][] matrix) {
        int len = matrix.length;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                Object temp = matrix[i][j];
                matrix[i][j] = matrix[len - 1 - j][len - 1 - i];
                matrix[len - 1 - j][len - 1 - i] = temp;
            }
        }
    }
}
