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
        current.setValue(value);
    }

    protected void setCurrentRelations()
    {
        current.setValue(value);
    }
}
