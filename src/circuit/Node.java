package circuit;

import java.util.LinkedList;

/**
 * A node in a circuit containing a potential variable, which might be a free variable or bound to a value.
 * Created by admin on 6/13/16.
 */
public class Node implements CircuitElement
{
    Circuit circuit;
    LinkedList<Component> components;
    CircuitVar potential;

    public Node(Circuit circuit)
    {
        this.circuit = circuit;
        circuit.addNode(this);

        components = new LinkedList<>();
        potential = new CircuitVar(this);
    }

    public Circuit getCircuit() { return circuit; }

    public CircuitElement[] getNeighbors() { return (CircuitElement[]) components.toArray(); }

    public CircuitVar[] getVariables() { return new CircuitVar[]{potential}; }

    public boolean remove()
    {
        components.forEach(circuit::removeComponent);
        potential.remove();
        return true;
    }

    public Double getPotential()
    {
        return potential.getValue();
    }

    public void setPotential(double value)
    {
        potential.setValue(value, true);
    }

    public void update()
    {
        potential.setNeighbors();

        // set relations
        potential.resetRelations();
        // for each component AB, V_A - V_AB - V_B = 0
        for (Component comp : components)
            potential.addRelation(0, new Pair<>(potential, 1.0),
                                     new Pair<>(comp.voltage, -1.0),
                                     new Pair<>(comp.otherNode(this).potential, -1.0));
    }

    // add kcl to current's relations
    protected void kcl(CircuitVar current)
    {
        assert current.parent.hasNeighbor(this);

        LinkedList<Pair<CircuitVar, Double>> pairs = new LinkedList<>();

        for (Component comp : components)
            pairs.add(new Pair<>(comp.current, comp.changeSign(1.0, this, comp.otherNode(this))));

        current.addRelation(pairs, 0); // mother of god why is this language so horrible
    }
}
