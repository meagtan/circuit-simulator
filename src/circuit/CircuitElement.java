package circuit;


/**
 * A node or component in a circuit.
 * Created by admin on 7/4/16.
 */
public interface CircuitElement
{
    Circuit getCircuit();
    CircuitElement[] getNeighbors();
    CircuitVar[] getVariables();
    void update();

    default boolean hasNeighbor(CircuitElement e)
    {
        for (CircuitElement e1 : getNeighbors())
            if (e1 == e)
                return true;
        return false;
    }
}
