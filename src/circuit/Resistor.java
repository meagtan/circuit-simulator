package circuit;

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
    }

    protected void setCurrentRelations()
    {
        // Ohm's law
        current.addRelation(0, new Pair<>(current, resistance), new Pair<>(voltage, -1.0));
    }

    protected void setVoltageRelations()
    {
        // voltage = end.potential - start.potential
        voltage.addRelation(0, new Pair<>(voltage, 1.0), new Pair<>(start.potential, 1.0), new Pair<>(end.potential, -1.0));
    }
}
