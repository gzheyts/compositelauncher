package org.eclipse.debug.launching.composite.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @author gzheyts
 */

public class LCDraft extends DraftBase {
    private String name;
    private boolean missed;

    private LCTypeDraft typeDraft;

    public LCDraft(String configName) {
        this.name = configName;
    }

    public LCDraft(final LCDraft draft) {
        this(draft.name);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        firePropertyChange("name", this.name, this.name = name); //$NON-NLS-1$
    }

    public void setTypeDraft(LCTypeDraft typeDraft) {
        firePropertyChange("typeDraft", this.typeDraft, this.typeDraft = typeDraft); //$NON-NLS-1$
    }


    public LCTypeDraft getTypeDraft() {
        return typeDraft;
    }


    public boolean isMissed() {
        return missed;
    }

    public void setMissed(boolean missed) {
        firePropertyChange("missed", this.missed, this.missed = missed); //$NON-NLS-1$
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LCDraft that = (LCDraft) o;

        if (!name.equals(that.name)) return false;
        return true;

    }


    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public static LCDraft fromConfiguration(final ILaunchConfiguration config) throws CoreException {
        return new LCDraft(config.getName());
    }
}
