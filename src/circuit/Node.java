package circuit;

import circuit.aux.Pair;

import java.util.ArrayList;
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
        potential = new CircuitVar(this, circuit.system);
    }

    // methods

    public Circuit getCircuit() { return circuit; }

    public void setRelations()
    {
        // reset relations containing potential variable
        potential.resetRelations();

        // add kcl law for neighboring currents
        ArrayList<Pair<CircuitVar, Double>> pairs = new ArrayList<>();

        for (Component comp : components)
            pairs.add(new Pair<>(comp.current, comp.changeSign(1.0, this, comp.otherNode(this))));

        potential.addRelation(pairs, 0);
    }

    public void remove()
    {
        components.forEach(circuit::removeComponent);
        potential.remove();
    }

    // interface

    public Double getPotential()
    {
        return potential.getValue();
    }

    public void setPotential(double value)
    {
        potential.setValue(value);
    }
}
