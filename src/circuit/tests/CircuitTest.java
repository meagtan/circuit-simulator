package circuit.tests;

import circuit.*;

/**
 * Created by admin on 7/9/16.
 */
public class CircuitTest
{
    public static void main(String[] args)
    {
        Circuit circuit = new Circuit();
        Node a, b, c;

        a = new Node(circuit);
        b = new Node(circuit);
        c = new Node(circuit);

        c.setPotential(1);

        VoltageSource ac = new VoltageSource(circuit, c, a, 9);

        new Resistor(circuit, a, b, 1);
        Resistor r = new Resistor(circuit, b, c, 3);
        new Resistor(circuit, b, c, 2 * r.getResistance());

        System.out.println("The current through BC is " + r.currentFromStart());
    }
}
