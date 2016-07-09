package circuit;

import java.util.LinkedList;

/**
 * An electrical circuit containing nodes and components.
 * Created by admin on 6/13/16.
 */
public class Circuit
{
    LinkedList<Node> nodes;
    LinkedList<Component> components;
    LinearSystem system;

    public Circuit()
    {
        nodes = new LinkedList<>();
        components = new LinkedList<>();
        system = new LinearSystem();
    }

    protected void addNode(Node node)
    {
        // check if already exists
        if (!nodes.contains(node))
        {
            nodes.add(node);
            // update();
        }
    }

    protected void addComponent(Component comp)
    {
        if (!components.contains(comp))
        {
            components.add(comp);
            // update();
        }
    }

    public void removeNode(Node node)
    {
        node.remove();
        nodes.remove(node);
        update();
    }

    public void removeComponent(Component comp)
    {
        comp.remove();
        components.remove(comp);
        update();
    }

    protected void update()
    {
        nodes.forEach(Node::setRelations);
        components.forEach(Component::setRelations);
    }

    public double getVoltageBetween(Node start, Node end)
    {
        return end.getPotential() - start.getPotential();
    }

    public double getCurrentBetween(Node start, Node end)
    {
        // TODO
        return -1;
    }
}
