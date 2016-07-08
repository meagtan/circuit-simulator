package circuit;

/**
 * An idealized voltage source fixing its voltage to a constant value.
 * Created by admin on 6/13/16.
 */
public class VoltageSource extends Component
{
    double value;

    public VoltageSource(Circuit circuit, Node start, Node end, double value)
    {
        super(circuit, start, end);
        setVoltage(value);
    }

    public void setVoltage(double value)
    {
        this.value = value;
        voltage.setValue(value, true);
    }

    protected void setCurrentRelations()
    {
        start.kcl(current);
        end.kcl(current);
    }

    protected void setVoltageRelations()
    {
        voltage.setValue(value, false);
    }
}
