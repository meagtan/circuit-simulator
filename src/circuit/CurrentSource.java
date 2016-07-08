package circuit;

/**
 * An idealized current source with a fixed value for the current.
 * Created by admin on 6/13/16.
 */
public class CurrentSource extends Component
{
    double value;

    public CurrentSource(Circuit circuit, Node start, Node end, double value)
    {
        super(circuit, start, end);
        setCurrent(value);
    }

    public void setCurrent(double value)
    {
        this.value = value;
        current.setValue(value, true);
    }

    protected void setCurrentRelations()
    {
        current.setValue(value, false);
    }

    protected void setVoltageRelations()
    {
        // voltage = end.potential - start.potential
        voltage.addRelation(0, new Pair<>(voltage, 1.0), new Pair<>(start.potential, 1.0), new Pair<>(end.potential, -1.0));
    }
}
