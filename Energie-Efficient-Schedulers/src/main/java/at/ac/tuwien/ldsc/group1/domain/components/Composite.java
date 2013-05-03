package at.ac.tuwien.ldsc.group1.domain.components;

import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;

import java.util.List;

public interface Composite {
    /**
     * Add a component to this machine
     */
    void addComponent (Component component) throws ResourceUnavailableException;

    /**
     * Remove a component from this machine
     */
    void removeComponent(Component component);

    /**
     * Get a list of components that are managed by
     * this machine.
     */
    List<Component> getComponents();
}
