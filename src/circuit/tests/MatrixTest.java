package circuit.tests;

import circuit.Matrix;

import java.util.Arrays;

/**
 * Created by admin on 7/7/16.
 */
public class MatrixTest
{
    public static void main(String[] args)
    {
        Matrix a = new Matrix(-1);

        a.resize(2, 2);

        a.set(0, 1, 3);

        a.adjoinColumn(1, a.getRow(0));

        // a.deleteColumn(0);

        System.out.println(Arrays.toString(a.transform(new double[]{1, 2, 3})));

        System.out.println(a);
    }
}
