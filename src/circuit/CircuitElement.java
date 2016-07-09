package circuit;


/**
 * A node or component in a circuit.
 * Created by admin on 7/4/16.
 */
public interface CircuitElement
{

    Circuit getCircuit();

    // set only the relations that the element defines
    void setRelations();

    // unbind element from neighbors
    void remove();
}
