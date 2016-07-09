package circuit;

import circuit.aux.Matrix;

import java.util.ArrayList;
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
    Matrix relations,   // contains all linear relations
           relIndices;  // maps each index to the start and end of the rows in relations that correspond to that index

    LinearSystem()
    {
        values = new ArrayList<>();
        bindings = new ArrayList<>();
        relations = new Matrix();
        relIndices = new Matrix(-1);
        relations.resize(0, 1); // add column for constant terms
        relIndices.resize(2, 0); // two rows representing start and end
    }

    // methods

    // create new slot for a variable, return its index
    protected int newVariable()
    {
        values.add(null);
        bindings.add(null);

        relations.createColumn(0); // add variable
        relIndices.resize(0, 1);

        return values.size() - 1;
    }

    // get value of variable with index varIndex
    protected Double getValue(int varIndex)
    {
        if (!solveSystem(varIndex))
            return values.get(varIndex);
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

    // arranges relations in a list, sorted by support
    protected List<double[]> getRelations(int varIndex)
    {
        List<double[]> res = new ArrayList<>();

        for (int i = 0; i < relations.getRows(); i++)
            if (relations.get(i, varIndex) != 0)
                res.add(Arrays.copyOf(relations.getRow(i), relations.getCols()));

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
            relIndices.set(0, varIndex, relations.getRows() - 1);
            relIndices.set(1, varIndex, relations.getRows() - 1);
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

    // remove relations defined by varIndex
    protected void resetRelations(int varIndex)
    {
        double[] bounds = relIndices.getColumn(varIndex);
        int start = (int) bounds[0], end = (int) bounds[1];

        // remove relations corresponding to varIndex
        if (start != -1)
            for (int i = start; i <= end; i++)
                relations.deleteRow(start); // the next row gets pushed up

        // reset relation indices
        relIndices.set(0, varIndex, -1);
        relIndices.set(1, varIndex, -1);

        // shift the index of every relation after varIndex's up, including end
        for (int j = 0; j < relIndices.getCols(); j++)
            for (int i = 0; i <= 1; i++)
                if (relIndices.get(i, j) > start)
                    relIndices.set(i, j, relIndices.get(i, j) + start - end);

        // reset bindings
        bindings.set(varIndex, values.get(varIndex));
    }

    // solves linear system and returns true if system is solvable, false otherwise
    // the goal is to get a binding for varIndex, not solve the entire system
    private boolean solveSystem(int varIndex)
    {
        if (bindings.get(varIndex) != null)
            return true;

        Matrix rels = new Matrix();
        ArrayList<Integer> visited = new ArrayList<>();
        List<Integer> support;
        int index;

        rels.resize(0, relations.getCols());
        visited.add(varIndex);

        // load rels
        for (int i = 0; i < visited.size(); i++)
        {
            index = visited.get(i);

            // go through the relations defined on varIndex
            for (double[] relation : getRelations(index))
            {
                // first move all the bound variables to the constant coefficient (don't delete columns, just subtract)
                for (int j = 0; j < relation.length - 1; j++)
                {
                    if (bindings.get(j) != null)
                    {
                        relation[relation.length - 1] += bindings.get(j) * relation[j];
                        relation[j] = 0;
                    }
                }

                support = Matrix.support(relation);

                // check if it defines only one variable
                if (support.size() == 1 || (support.size() == 2 && support.contains(relation.length - 1))) // including the constant term
                {
                    // bind variable
                    visited.remove(support.get(0));
                    bindings.set(support.get(0), relation[relation.length - 1] / relation[support.get(0)]);
                }
                else
                {
                    // add the relation to the matrix every other variable in the relation to the queue
                    rels.adjoinRow(-1, relation);
                    for (int ind : support)
                        if (ind != index && ind != relation.length - 1 && !visited.contains(ind))
                            visited.add(ind);
                }
            }
        }

        // reduce rels to reduced row echelon form
        for (int i = 0; i < rels.getRows(); i++)
        {
            // if row is empty, delete it and jump to the beginning
            if (Matrix.support(rels.getRow(i)).isEmpty())
            {
                rels.deleteRow(i);
                i--;
                continue;
            }

            // set pivot index
            index = Matrix.support(rels.getRow(i)).get(0);

            // if the constant is nonzero and no other coefficient is, the system is inconsistent
            if (index == rels.getCols() - 1)
                return false;

            // multiply row so that the coefficient of index is 1
            rels.multiplyRow(i, 1 / rels.get(i, index));

            // subtract row from next rows so their index column is 0
            for (int j = i + 1; j < rels.getRows(); j++)
                rels.addRow(j, Matrix.multiply(rels.getRow(i), - rels.get(j, index)));
        }

        // make it so every row has at most one nonzero cell other than the constant
        for (int i = rels.getRows() - 1; i >= 0; i--)
        {
            // if row is empty, delete it and jump to the beginning
            if (Matrix.support(rels.getRow(i)).isEmpty())
            {
                rels.deleteRow(i);
                i--;
                continue;
            }

            // set pivot index
            index = Matrix.support(rels.getRow(i)).get(0);

            // if the constant is nonzero and no other coefficient is, the system is inconsistent
            if (index == rels.getCols() - 1)
                return false;

            // remove index's coefficient from every previous row
            for (int j = 0; j < i; j++)
                rels.addRow(j, Matrix.multiply(rels.getRow(i), - rels.get(j, index)));
        }

        // now that all relations contain at most one variable, bind them to constants
        for (int i = 0; i < rels.getRows(); i++)
        {
            // if row is empty, continue
            if (Matrix.support(rels.getRow(i)).isEmpty())
                continue;

            // set pivot index
            index = Matrix.support(rels.getRow(i)).get(0);

            // if the constant is nonzero and no other coefficient is, the system is inconsistent
            if (index == rels.getCols() - 1)
                return false;

            // assign value to index
            bindings.set(index, rels.get(i, -1));
        }

        return bindings.get(varIndex) != null;
    }
}
