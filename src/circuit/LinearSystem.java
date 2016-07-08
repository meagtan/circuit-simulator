package circuit;

import java.util.ArrayList;
import java.lang.Math;
import java.util.Arrays;
import java.util.List;

/**
 * A linear system of equations, containing linear relations of variables represented as indices in a list of bindings.
 * Created by admin on 7/4/16.
 */
class LinearSystem
{
    ArrayList<Double> values,   // assigned values
                      bindings; // result of evaluation
    Matrix graph,      // adjacency matrix
           relations,  // contains all linear relations
           relIndices; // maps each index to the start and end of the rows in relations that correspond to that index

    LinearSystem()
    {
        values = new ArrayList<>();
        bindings = new ArrayList<>();
        graph = new Matrix(Math::max, Math::min);
        relations = new Matrix();
        relIndices = new Matrix(-1);
        relations.resize(0, 1); // add column for constant terms
        relIndices.resize(2, 0); // two rows representing start and end
    }

    // create new slot for a variable, return its index
    protected int newVariable()
    {
        values.add(null);
        bindings.add(null);

        // resize matrices
        graph.resize(1, 1);
        relIndices.resize(0, 1);
        relations.resize(0, 1); // add variable

        return values.size() - 1;
    }

    // get value of variable with index varIndex
    protected Double getValue(int varIndex)
    {
        if (!solveSystem(varIndex))
            return null;
        return bindings.get(varIndex);
    }

    // set value of variable with index varIndex
    protected void setValue(int varIndex, Double value)
    {
        values.set(varIndex, value);
        bindings.set(varIndex, value);
    }

    protected void removeVariable(int varIndex)
    {
        // TODO set things to null, remove bindings and neighborhoods

        // remove relations
        resetRelations(varIndex);
    }

    // Updating neighbors and relations

    protected void setNeighbors(int varIndex, int[] neighbors)
    {
        // reset neighbors of varIndex
        graph.multiplyRow(varIndex, 0);

        // set neighbors of var to 1
        for (int index : neighbors)
            graph.set(varIndex, index, 1);
    }

    protected int[] getNeighbors(int varIndex)
    {
        int[] neighbors = new int[graph.cols];

        for (int i = 0, j = 0; j < graph.rows; j++)
        {
            if (graph.get(varIndex, j) != 0)
            {
                neighbors[i] = j;
                i++;
            }
        }

        return neighbors;
    }

    // arranges relations in a list, sorted by support
    protected List<double[]> getRelations(int varIndex)
    {
        double[] bounds = relIndices.getColumn(varIndex);
        int start = (int) bounds[0], end = (int) bounds[1];

        if (start == -1)
            return null;

        List<double[]> res = new ArrayList<>();

        for (int i = start; i <= end; i++)
            res.add(relations.getRow(i));

        res.sort((row1, row2) -> Matrix.support(row1).size() - Matrix.support(row2).size());

        return res;
    }

    protected void addRelation(int varIndex, double[] coeffs, double constant)
    {
        double[] bounds = relIndices.getColumn(varIndex);
        int start = (int) bounds[0], end = (int) bounds[1];

        // check the varIndex column of relIndices
        if (start == -1) // if no relation is defined for varIndex
        {
            // add relation to the bottom of relations
            relations.adjoinRow(-1, coeffs);
            relations.set(-1, -1, constant);

            // set indices
            relIndices.set(0, varIndex, relations.rows - 1);
            relIndices.set(1, varIndex, relations.rows - 1);
        }
        else
        {
            // add relation to end + 1
            relations.adjoinRow(end + 1, coeffs);
            relations.set(end + 1, -1, constant);

            // update end
            relIndices.set(1, varIndex, end + 1);
        }
    }

    protected void resetRelations(int varIndex)
    {
        double[] bounds = relIndices.getColumn(varIndex);
        int start = (int) bounds[0], end = (int) bounds[1];

        // remove relations corresponding to varIndex
        for (int i = start; i <= end; i++)
            relations.deleteRow(start); // the next row gets pushed up

        // reset relation indices
        relIndices.set(0, varIndex, -1);
        relIndices.set(1, varIndex, -1);

        // shift the index of every relation after varIndex's up, including end
        for (int j = 0; j < relIndices.cols; j++)
            for (int i = 0; i <= 1; i++)
                if (relIndices.get(i, j) > start)
                    relIndices.set(i, j, relIndices.get(i, j) + start - end);

        // reset bindings
        for (int i = 0; i < graph.rows; i++)
            bindings.set(i, values.get(i));
    }

    // solves linear system and returns true if system is solvable, false otherwise
    // the goal is to get a binding for varIndex, not solve the entire system
    private boolean solveSystem(int varIndex)
    {
        if (bindings.get(varIndex) != null)
            return true;

        Matrix rels = new Matrix();
        ArrayList<Integer> queue = new ArrayList<>();
        List<Integer> support;

        rels.resize(0, relations.cols);
        queue.add(varIndex);

        while (bindings.get(varIndex) == null)
        {
            varIndex = queue.get(0);

            // go through the relations defined on varIndex
            for (double[] relation : getRelations(varIndex))
            {
                // first move all the bound variables to the constant coefficient (don't delete columns, just subtract)
                for (int i = 0; i < relation.length - 1; i++)
                {
                    if (bindings.get(i) != null)
                    {
                        relation[relation.length - 1] += bindings.get(i) * relation[i];
                        relation[i] = 0;
                    }
                }

                support = Matrix.support(relation);

                if (support.size() == 1)
                {
                    queue.remove(support.get(0));
                    // TODO calculate the binding for support.get(0)
                }
            }

            // add neighbors to unbounds
            for (Integer neighbor : getNeighbors(varIndex))
                if (!queue.contains(neighbor))
                    queue.add(neighbor);
            queue.remove(0);
        }

        // TODO
        return false;
    }
}
