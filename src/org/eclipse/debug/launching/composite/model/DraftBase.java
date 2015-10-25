package org.eclipse.debug.launching.composite.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author gzheyts
 */

public abstract class DraftBase {
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener
                                                     listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }


}

