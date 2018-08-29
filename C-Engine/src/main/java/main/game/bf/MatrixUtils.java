package main.game.bf;

/**
 * Created by JustMe on 8/27/2018.
 */
public class MatrixUtils {
    public static int[][] rotate(boolean rotation, int[][] mat) {
        if (rotation) {
            rotate90(mat);
        } else {
            rotate90(mat);
            rotate90(mat);
            rotate90(mat);
        }
        return mat;
    }

    // After transpose we swap elements of
    // column one by one for finding left
    // rotation of matrix by 90 degree
    static void reverseColumns(int arr[][])
    {
        for (int i = 0; i < arr[0].length; i++)
            for (int j = 0, k = arr[0].length - 1;
                 j < k; j++, k--) {
                int temp = arr[j][i];
                arr[j][i] = arr[k][i];
                arr[k][i] = temp;
            }
    }

    // Function for do transpose of matrix
    static void transpose(int arr[][])
    {
        for (int i = 0; i < arr.length; i++)
            for (int j = i; j < arr[0].length; j++) {
                int temp = arr[j][i];
                arr[j][i] = arr[i][j];
                arr[i][j] = temp;
            }
    }

    // Function to anticlockwise rotate
    // matrix by 90 degree
    static void rotate90(int arr[][])
    {
        transpose(arr);
        reverseColumns(arr);
    }



    // An Inplace function to rotate a N x N matrix
    // by 90 degrees in anti-clockwise direction
    static void rotateMatrixAntiClockwise(int N, int mat[][])
    {
        // Consider all squares one by one
        for (int x = 0; x < N / 2; x++)
        {
            // Consider elements in group of 4 in
            // current square
            for (int y = x; y < N-x-1; y++)
            {
                // store current cell in temp variable
                int temp = mat[x][y];

                // move values from right to top
                mat[x][y] = mat[y][N-1-x];

                // move values from bottom to right
                mat[y][N-1-x] = mat[N-1-x][N-1-y];

                // move values from left to bottom
                mat[N-1-x][N-1-y] = mat[N-1-y][x];

                // assign temp to left
                mat[N-1-y][x] = temp;
            }
        }
    }


    // Function for print matrix
    static void printMatrix(int arr[][])
    {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++)
                System.out.print(arr[i][j] + " ");
            System.out.println("");
        }
    }

}
