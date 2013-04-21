package at.ac.tuwien.ldsc.group1.domain.components;

import java.util.List;

public interface Composite {
    /**
     * Add a component to this machine
     */
    void addComponent (Component component);

    /**
     * Remove a component from this machine
     */
    void removeComponent(Component component);

    /**
     * Get a list of components that are managed by
     * this machine.
     */
    List<Component> getComponents();

    Component getParent();
}
