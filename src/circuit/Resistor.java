package circuit;

import circuit.aux.Pair;

/**
 * A resistor binding its current to its voltage by the linear factor of its resistance.
 * Created by admin on 6/13/16.
 */
public class Resistor extends Component
{
    double resistance;

    public Resistor(Circuit circuit, Node start, Node end, double value)
    {
        super(circuit, start, end);
        resistance = value;
    }

    public double getResistance()
    {
        return resistance;
    }

    public void setResistance(double resistance)
    {
        this.resistance = resistance;

        current.resetRelations();
        setCurrentRelations();
        start.setRelations();
        end.setRelations();
    }

    protected void setCurrentRelations()
    {
        // Ohm's law, with current the opposite direction of voltage
        current.addRelation(0, new Pair<>(current, resistance), new Pair<>(voltage, 1.0));
    }
}
