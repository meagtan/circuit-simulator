package circuit;

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

        current = new CircuitVar(this);
        voltage = new CircuitVar(this);
    }

    public Circuit getCircuit() { return circuit; }

    public CircuitElement[] getNeighbors() { return new CircuitElement[]{start, end}; }
    
    public CircuitVar[] getVariables() { return new CircuitVar[]{current, voltage}; }

    public void remove()
    {
        start.components.remove(this);
        end.components.remove(this);

        current.remove();
        voltage.remove();
    }

    protected abstract void setCurrentRelations();

    protected abstract void setVoltageRelations();

    public Double currentFromStart()
    {
        return current.getValue();
    }

    public Double voltageFromStart()
    {
        return voltage.getValue();
    }

    public List<Node> getNodes()
    {
        List<Node> res = new LinkedList<>();
        res.add(start);
        res.add(end);

        return res;
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

    public void update()
    {
        // TODO adjust voltage and current's varIndex based on e

        // set neighbors
        current.setNeighbors();
        voltage.setNeighbors();

        // set relations
        current.resetRelations();
        voltage.resetRelations();
        setCurrentRelations();
        setVoltageRelations();
    }
}
