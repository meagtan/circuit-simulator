package circuit.aux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * A resizable matrix with arithmetic operations.
 * Created by admin on 7/5/16.
 */
public class Matrix
{
    double[][] array;
    int rows, cols;
    double init = 0;

    BinaryOperator<Double> add = (i, j) -> i + j, mul = (i, j) -> i * j;

    public Matrix()
    {
        rows = cols = 0;
        // the array will not be initialized until the first assignment
    }

    public Matrix(int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
    }

    public Matrix(double init)
    {
        rows = cols = 0;
        this.init = init;
    }

    public Matrix(BinaryOperator<Double> add, BinaryOperator<Double> mul)
    {
        rows = cols = 0;
        this.add = add;
        this.mul = mul;
    }

    public Matrix(Matrix other)
    {
        rows = other.rows;
        cols = other.cols;
        init = other.init;

        // deep copy
        array = new double[rows][cols];
        for (int i = 0; i < rows; i++)
            array[i] = Arrays.copyOf(other.array[i], cols);
    }

    // methods

    public int getRows() { return rows; }

    public int getCols() { return cols; }

    public String toString()
    {
        String res = "";
        if (array != null)
            for (double[] row : array)
                res += Arrays.toString(row) + "\n";
        return res;
    }

    public void resize(int di, int dj)
    {
        double[][] newArray;

        rows += di;
        cols += dj;

        // resize array
        if (array != null)
        {
            newArray = new double[rows][cols];
            for (int i1 = 0; i1 < rows; i1++)
                newArray[i1] = new double[cols];

            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    if (i < array.length && j < array[0].length)
                        newArray[i][j] = array[i][j];
            array = newArray;
        }
    }

    public double get(int i, int j)
    {
        if (i == -1)
            i += rows;
        if (j == -1)
            j += cols;

        if (array == null || rows <= i || cols <= j)
            return init;
        return array[i][j];
    }

    public void set(int i, int j, double value)
    {
        if (i == -1)
            i += rows;
        if (j == -1)
            j += cols;

        if (i < 0 || j < 0)
            return;

        // resize if necessary
        if (i >= rows || j >= cols)
            resize(i + 1 - rows, j + 1 - cols);

        // initialize array
        if (array == null)
            initArray();

        array[i][j] = value;
    }

    private void initArray()
    {
        array = new double[rows][cols];
        for (int i1 = 0; i1 < rows; i1++)
            Arrays.fill(array[i1], init);
    }

    public double[] getRow(int i)
    {
        if (i >= rows)
            return null;

        if (array == null)
            initArray();

        return Arrays.copyOf(array[i], cols);
    }

    // assuming row.length = cols
    public void addRow(int i, double[] row)
    {
        row = Arrays.copyOf(row, cols);

        for (int j = 0; j < cols; j++)
            set(i, j, add.apply(get(i, j), row[j]));
    }

    public double[] getColumn(int j)
    {
        if (j >= cols)
            return null;

        double[] col = new double[rows];
        for (int i = 0; i < rows; i++)
            col[i] = get(i, j);
        return col;
    }

    // assuming col.length = rows
    public void addColumn(int j, double[] col)
    {
        col = Arrays.copyOf(col, rows);

        for (int i = 0; i < rows; i++)
            set(i, j, add.apply(get(i, j), col[i]));
    }

    public void multiplyRow(int i, double x)
    {
        for (int j = 0; j < cols; j++)
            set(i, j, mul.apply(get(i, j), x));
    }

    public void multiplyColumn(int j, double x)
    {
        for (int i = 0; i < rows; i++)
            set(i, j, mul.apply(get(i, j), x));
    }

    public void setRow(int i, double[] row)
    {
        multiplyRow(i, 0);
        addRow(i, row);
    }

    public void setColumn(int j, double[] col)
    {
        multiplyColumn(j, 0);
        addColumn(j, col);
    }

    // adjoin row at ith position
    public void adjoinRow(int i, double[] row)
    {
        createRow(i);
        addRow(i, row);
    }

    // adjoin row at ith position
    public void adjoinColumn(int j, double[] col)
    {
        createColumn(j);
        addColumn(j, col);
    }

    // create new row at ith position
    public void createRow(int i)
    {
        if (i == -1)
            i = rows;

        if (i > rows)
            resize(i - rows, 0);
        else
            resize(1, 0);

        if (array == null)
            initArray();

        // shift rows down
        for (int i1 = rows - 2; i1 >= i; i1--)
            setRow(i1 + 1, getRow(i1));

        // clear ith row
        multiplyRow(i, 0);
    }

    // create new column at jth position
    public void createColumn(int j)
    {
        if (j == -1)
            j = cols;

        if (j > cols)
            resize(0, j - cols);
        else
            resize(0, 1);

        // shift columns down
        for (int j1 = cols - 2; j1 >= j; j1--)
            setColumn(j1 + 1, getColumn(j1));

        // clear jth column
        multiplyColumn(j, 0);
    }

    public void deleteRow(int i)
    {
        if (i >= rows)
            return;

        // shift rows up
        for (int i1 = i; i1 < rows - 1; i1++)
            setRow(i1, getRow(i1 + 1));

        resize(-1, 0);
    }

    public void deleteColumn(int j)
    {
        if (j >= cols)
            return;

        // shift rows up
        for (int j1 = j; j1 < cols - 1; j1++)
            setColumn(j1, getColumn(j1 + 1));

        resize(0, -1);
    }

    public static double scalarProduct(double[] v, double[] w, BinaryOperator<Double> mul)
    {
        int min = v.length > w.length ? w.length : v.length;
        double res = 0;

        for (int i = 0; i < min; i++)
            res += mul.apply(v[i], w[i]);

        return res;
    }

    public static double scalarProduct(double[] v, double[] w)
    {
        return scalarProduct(v, w, (i, j) -> i * j);
    }

    public double[] transform(double[] vector)
    {
        if (vector.length != cols)
            return null;

        double[] res = new double[rows];

        for (int i = 0; i < rows; i++)
            res[i] = scalarProduct(getRow(i), vector, mul);

        return res;
    }

    public void add(Matrix other)
    {
        for (int i = 0; i < rows; i++)
            addRow(i, other.getRow(i));
    }

    public void scalarMultiply(double factor)
    {
        for (int i = 0; i < rows; i++)
            multiplyRow(i, factor);
    }

    // returns new matrix because row/col properties may change
    public Matrix multiply(Matrix other)
    {
        Matrix res = new Matrix(add, mul);

        for (int j = 0; j < cols; j++)
            res.adjoinColumn(j, transform(other.getColumn(j)));

        return res;
    }

    // return indices whose values are nonzero
    public static List<Integer> support(double[] row)
    {
        ArrayList<Integer> res = new ArrayList<>();

        for (int i = 0; i < row.length; i++)
            if (row[i] != 0)
                res.add(i);

        return res;
    }

    public static List<Integer> support(double[] row, List<Integer> indices)
    {
        return support(filter(row, indices));
    }

    // make all components of row other than those in indices zero
    public static double[] filter(double[] row, List<Integer> indices)
    {
        double[] res = new double[row.length];

        for (int index : indices)
            if (0 <= index && index < row.length)
                res[index] = row[index];

        return res;
    }

    public static double[] multiply(double[] row, double factor)
    {
        double[] res = new double[row.length];
        for (int i = 0; i < row.length; i++)
            res[i] = row[i] * factor;
        return res;
    }
}
