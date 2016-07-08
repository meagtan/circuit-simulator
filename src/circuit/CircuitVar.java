package circuit;

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

    public CircuitVar(CircuitElement e)
    {
        parent = e;
        system = e.getCircuit().system;
        varIndex = system.newVariable();
    }

    public Double getValue()
    {
        return system.getValue(varIndex);
    }

    public void setValue(Double value, boolean update)
    {
        system.setValue(varIndex, value);
        if (update)
            parent.getCircuit().update(parent); // TODO only update the relations that contain this variable
    }

    public void remove()
    {
        system.removeVariable(varIndex);
        parent.getCircuit().update(parent);
    }

    private int getIndex() { return varIndex; }

    public void setNeighbors()
    {
        system.setNeighbors(varIndex, Arrays.stream(parent.getNeighbors())
                .flatMapToInt(e -> Arrays.stream(e.getVariables()).mapToInt(CircuitVar::getIndex))
                .toArray());
    }

    public void resetRelations()
    {
        system.resetRelations(varIndex);
    }

    @SafeVarargs
    final public void addRelation(double constant, Pair<CircuitVar, Double>... terms)
    {
        double[] coeffs = new double[system.relations.cols - 1];

        for (Pair<CircuitVar, Double> term : terms)
            coeffs[term.l.varIndex] = term.r;

        system.addRelation(varIndex, coeffs, constant);
    }

    public void addRelation(List<Pair<CircuitVar, Double>> terms, double constant)
    {
        double[] coeffs = new double[system.relations.cols - 1];

        for (Pair<CircuitVar, Double> term : terms)
            coeffs[term.l.varIndex] = term.r;

        system.addRelation(varIndex, coeffs, constant);
    }
}
