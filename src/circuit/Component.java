package circuit;

import circuit.aux.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * A circuit component with properties voltage and current connecting two nodes.
 * The voltage and current are taken to be directed from start to end.
 * Created by admin on 6/13/16.
 */
public abstract class Component implements CircuitElement
{
    Node start, end;
    Circuit circuit;
    CircuitVar current, voltage;

    public Component(Circuit circuit, Node start, Node end)
    {
        this.circuit = circuit;
        this.start = start;
        this.end = end;

        start.components.add(this);
        end.components.add(this);
        circuit.addComponent(this);

        current = new CircuitVar(this, circuit.system);
        voltage = new CircuitVar(this, circuit.system);
    }

    // methods

    public Circuit getCircuit() { return circuit; }

    public void setRelations()
    {
        // set relations
        current.resetRelations();
        voltage.resetRelations();
        setCurrentRelations();
        setVoltageRelations();
    }

    protected abstract void setCurrentRelations();

    protected void setVoltageRelations()
    {
        // voltage = end.potential - start.potential
        voltage.addRelation(0, new Pair<>(voltage, 1.0), new Pair<>(start.potential, 1.0), new Pair<>(end.potential, -1.0));
    }

    public void remove()
    {
        start.components.remove(this);
        end.components.remove(this);

        current.remove();
        voltage.remove();
    }

    public Double currentFromStart()
    {
        return current.getValue();
    }

    public Double voltageFromStart()
    {
        return voltage.getValue();
    }

    public Double currentBetween(Node start, Node end)
    {
        return changeSign(currentFromStart(), start, end);
    }

    public Double voltageBetween(Node start, Node end)
    {
        return changeSign(voltageFromStart(), start, end);
    }

    public Node otherNode(Node node)
    {
        if (node == start)
            return end;
        if (node == end)
            return start;
        return null;
    }

    protected Double changeSign(Double res, Node start, Node end)
    {
        if (start == this.start && end == this.end)
            return res;
        if (start == this.end && end == this.start)
            return -res;
        return null;
    }
}
