package circuit;

import circuit.aux.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Variable representing a property of a circuit element within a linear system of equations.
 * Created by admin on 7/4/16.
 */
public class CircuitVar
{
    LinearSystem system;
    CircuitElement parent;
    int varIndex;

    public CircuitVar(CircuitElement e, LinearSystem system)
    {
        parent = e;
        this.system = system;
        varIndex = system.newVariable();
    }

    public Double getValue()
    {
        parent.getCircuit().update();
        return system.getValue(varIndex);
    }

    public void setValue(Double value)
    {
        system.setValue(varIndex, value);
    }

    public void remove()
    {
        system.removeVariable(varIndex);
    }

    private int getIndex() { return varIndex; }

    public void resetRelations()
    {
        // TODO make it so only the relations added are removed
        system.resetRelations(varIndex);
    }

    @SafeVarargs
    final public void addRelation(double constant, Pair<CircuitVar, Double>... terms)
    {
        double[] coeffs = new double[system.relations.getCols() - 1];

        for (Pair<CircuitVar, Double> term : terms)
            coeffs[term.l.varIndex] = term.r;

        system.addRelation(varIndex, coeffs, constant);
    }

    public void addRelation(List<Pair<CircuitVar, Double>> terms, double constant)
    {
        double[] coeffs = new double[system.relations.getCols() - 1];

        for (Pair<CircuitVar, Double> term : terms)
            coeffs[term.l.varIndex] = term.r;

        system.addRelation(varIndex, coeffs, constant);
    }
}
