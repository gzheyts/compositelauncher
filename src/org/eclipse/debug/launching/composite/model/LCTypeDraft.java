package org.eclipse.debug.launching.composite.model;


import org.eclipse.debug.core.ILaunchConfigurationType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author gzheyts
 */

public class LCTypeDraft extends DraftBase {
    private String id;
    private String name;
    private boolean missed;

    private Set<LCDraft> configDrafts = new HashSet<>();


    public LCTypeDraft(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    public LCTypeDraft(final LCTypeDraft typeDraft) {
        this(typeDraft.id, typeDraft.name);
    }


    public static LCTypeDraft fromConfigurationType(final ILaunchConfigurationType configType) {
        return new LCTypeDraft(configType.getIdentifier(), configType.getName());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        firePropertyChange("id", this.id, this.id = id); //$NON-NLS-1$
    }

    public String getName() {
        return name;
    }

    public void setName(String configDrafts) {
        firePropertyChange("name", this.name, this.name = configDrafts); //$NON-NLS-1$
    }

    public void setConfigDrafts(Set<LCDraft> drafts) {
        Object oldValue = new HashSet<>(configDrafts);
        firePropertyChange("configDrafts", oldValue, this.configDrafts = drafts); //$NON-NLS-1$
    }

    public Set<LCDraft> getConfigDrafts() {
        return configDrafts;
    }

    public boolean isEmpty() {
        return configDrafts.isEmpty();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LCTypeDraft that = (LCTypeDraft) o;

        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Deprecated
    public boolean contains(final String lcDraftName) {
        return getDraftByName(lcDraftName) != null;
    }


    public LCDraft getDraftByName(final String lcDraftName) {
        for (LCDraft draft : configDrafts) {
            if (draft.getName().equals(lcDraftName)) {
                return draft;
            }
        }
        return null;
    }

    public boolean remove(final LCDraft draft) {

        Set<LCDraft> oldValue = new HashSet<>(configDrafts);
        boolean removed = configDrafts.remove(draft);
        if (removed) {
            firePropertyChange("configDrafts", oldValue, configDrafts); //$NON-NLS-1$
        }

        return removed;
    }

    public LCDraft getOrCreateLCDraft(final String configName) {
        LCDraft draftByName = getDraftByName(configName);
        return (draftByName == null) ? addLCDraft(new LCDraft(configName)) : draftByName;
    }

    public LCDraft addLCDraft(final LCDraft draft) {
        Object oldValue = new HashSet<>(configDrafts);

        LCDraft newDraft = new LCDraft(draft);
        newDraft.setTypeDraft(this);

        boolean added = configDrafts.add(newDraft);
        if (added) {
            firePropertyChange("configDrafts", oldValue, configDrafts); //$NON-NLS-1$
        }
        return newDraft;
    }

    public LCDraft findLCDraft(final LCDraft draft) {
        for (Iterator<LCDraft> iterator = configDrafts.iterator(); iterator.hasNext(); ) {
            LCDraft next = iterator.next();
            if (next.equals(draft)) {
                return next;
            }
        }
        return null;
    }


    public boolean isMissed() {
        return missed;
    }

    public void setMissed(boolean missed) {
        firePropertyChange("missed", this.missed, this.missed = missed); //$NON-NLS-1$
    }

    public boolean contains(final LCDraft draft) {
        return configDrafts.contains(draft);
    }
}
