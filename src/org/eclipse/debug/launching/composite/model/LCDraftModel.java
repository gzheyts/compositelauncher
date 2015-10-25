package org.eclipse.debug.launching.composite.model;

import org.eclipse.core.databinding.conversion.IConverter;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.debug.core.*;
import org.eclipse.debug.launching.composite.CompositeLauncherPlugin;
import org.eclipse.debug.launching.composite.model.converter.LCDraftToStringConverter;

import java.util.*;

/**
 * @author gzheyts
 */

public class LCDraftModel extends DraftBase {


    private Set<LCTypeDraft> configTypeDrafts = new HashSet<>();

    public static final String DRAFT_SEPARATOR = "|"; //$NON-NLS-1$

    public static final String LAUNCHCONFIGSTOREATTRIBUTE = "childLaunches"; //$NON-NLS-1$

    private static LCDraftModel instance;


    private LCDraftModel() {
    }

    public static LCDraftModel getInstance() {
        if (instance == null) {
            instance = new LCDraftModel();
        }
        return instance;
    }

    public Set<LCTypeDraft> getConfigTypeDrafts() {
        return configTypeDrafts;
    }

    public void setConfigTypeDrafts(Set<LCTypeDraft> drafts) {
        Object oldValue = new HashSet<>(configTypeDrafts);
        firePropertyChange("configTypeDrafts", oldValue, this.configTypeDrafts = drafts); //$NON-NLS-1$
    }


    public boolean containsDraftAll(final ILaunchConfiguration[] configurations) throws CoreException {
        for (ILaunchConfiguration config : configurations) {
            if (!isDrafted(config)) {
                return false;
            }
        }
        return true;
    }


    public boolean isDrafted(final ILaunchConfiguration config) throws CoreException {
        LCTypeDraft typeNode = findLCTypeDraft(LCTypeDraft.fromConfigurationType(config.getType()));
        return (typeNode != null) && (typeNode.findLCDraft(LCDraft.fromConfiguration(config)) != null);

    }

    public void removeLCDrafts(final List<LCDraft> drafts) {
        for (LCDraft draft : drafts) {
            removeLCDraft(draft);
        }
    }

    public void removeLCDraft(final LCDraft draft) {

        LCTypeDraft typeNode = null;

        for (LCTypeDraft draftType : configTypeDrafts) {
            if (draftType.equals(draft.getTypeDraft())) {
                typeNode = draftType;
            }
        }

        typeNode.remove(draft);

        if (typeNode.isEmpty()) {
            removeLCTypeDraft(typeNode);
        }

    }

    final boolean removeLCTypeDraft(final LCTypeDraft typeDraft) {
        Object oldValue = new HashSet<>(configTypeDrafts);
        boolean removed = configTypeDrafts.remove(typeDraft);
        if (removed) {
            firePropertyChange("configTypeDrafts", oldValue, configTypeDrafts); //$NON-NLS-1$
        }
        return removed;
    }

    public void saveDrafts(ILaunchConfigurationWorkingCopy launchConfiguration) {

        List<String> toSave = new LinkedList<>();

        IConverter draftConverter = new LCDraftToStringConverter();

        for (LCTypeDraft draftTypeNode : configTypeDrafts) {
            for (LCDraft draftNode : draftTypeNode.getConfigDrafts()) {
                toSave.add((String) draftConverter.convert(draftNode));
            }
        }

        launchConfiguration.setAttribute(LAUNCHCONFIGSTOREATTRIBUTE, toSave);
    }


    public List<LCDraft> appendAll(LCDraft[] drafts) {
        List<LCDraft> appendedDrafts = new LinkedList<>();
        for (LCDraft draft : drafts) {
            LCDraft appended = append(draft);
            if (appended != null) {
                appendedDrafts.add(appended);
            }
        }
        return appendedDrafts;
    }

    public LCDraft append(final LCDraft draft) {
        LCTypeDraft typeNode = getOrCreateLCTypeDraft(draft.getTypeDraft());
        return typeNode.addLCDraft(draft);


    }


    public LCTypeDraft getOrCreateLCTypeDraft(final LCTypeDraft typeDraft) {
        LCTypeDraft result = findLCTypeDraft(typeDraft);
        return (result == null) ? addLCTypeDraft(typeDraft) : result;
    }

    public LCTypeDraft findLCTypeDraft(LCTypeDraft typeDraft) {
        for (Iterator<LCTypeDraft> iterator = configTypeDrafts.iterator(); iterator.hasNext(); ) {
            LCTypeDraft next = iterator.next();
            if (next.equals(typeDraft)) {
                return next;
            }
        }
        return null;
    }

    public LCTypeDraft addLCTypeDraft(final LCTypeDraft typeDraft) {
        Object oldValue = new HashSet<>(configTypeDrafts);

        LCTypeDraft newTypeDraft = new LCTypeDraft(typeDraft);
        boolean added = configTypeDrafts.add(newTypeDraft);
        if (added) {
            firePropertyChange("configTypeDrafts", oldValue, this.configTypeDrafts); //$NON-NLS-1$
        }
        return newTypeDraft;
    }

    public LCTypeDraft findLCTypeDraft(final LCDraft draft) {
        for (LCTypeDraft typeDraftNode : configTypeDrafts) {
            if (typeDraftNode.getId().equals(draft.getTypeDraft())) {
                return typeDraftNode;
            }
        }
        return null;
    }

    public void loadDrafts(ILaunchConfiguration launchConfiguration) {
        List<String> childLaunches = null;
        try {
            childLaunches = launchConfiguration.getAttribute(LAUNCHCONFIGSTOREATTRIBUTE, new ArrayList<String>());
        } catch (CoreException e) {
            CompositeLauncherPlugin.error(e.getMessage(), e);
        }

        Map<String, LCTypeDraft> draftTypesMap = new HashMap<>();


        for (String childLaunch : childLaunches) {
            StringTokenizer tokenizer = new StringTokenizer(childLaunch, DRAFT_SEPARATOR);
            String configTypeId = tokenizer.nextToken();
            String configTypeName = tokenizer.nextToken();
            String configName = tokenizer.nextToken();

            LCTypeDraft typeDraft = draftTypesMap.get(configTypeId);

            if (typeDraft == null) {
                typeDraft = new LCTypeDraft(configTypeId, configTypeName);
                typeDraft.setMissed(isMissedCLType(configTypeId));
                draftTypesMap.put(configTypeId, typeDraft);
            }

            LCDraft draft = typeDraft.getOrCreateLCDraft(configName);
            try {
                draft.setMissed(isMissedLC(configTypeId, configName));
            } catch (CoreException e) {
                CompositeLauncherPlugin.error(e.getMessage(), e);
            }

        }
        setConfigTypeDrafts(new HashSet<>(draftTypesMap.values()));
    }

    private boolean isMissedCLType(final String configTypeId) {
        return getLaunchManager().getLaunchConfigurationType(configTypeId) == null;
    }

    private boolean isMissedLC(final String configTypeId, final String configName) throws CoreException {
        ILaunchConfigurationType launchConfigurationType = getLaunchManager().getLaunchConfigurationType(configTypeId);
        if (launchConfigurationType == null) {
            return true;
        }
        boolean missed = true;
        ILaunchConfiguration[] launchConfigurations = getLaunchManager().getLaunchConfigurations(launchConfigurationType);
        for (ILaunchConfiguration config : launchConfigurations) {
            if (config.getName().equals(configName)) {
                missed = false;
                break;
            }
        }
        return missed;
    }

    public ILaunchConfiguration[] fetchChildConfigurationsExceptMissed() throws CoreException {

        List<ILaunchConfiguration> configurations = new ArrayList<>();
        for (LCTypeDraft typeNode : configTypeDrafts) {
            if (typeNode.isMissed()) { // skip configurations  with missed configuration type
                continue;
            }
            ILaunchConfigurationType launchConfigurationType = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(typeNode.getId());
            ILaunchConfiguration[] launchConfigurations = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(launchConfigurationType);

            for (ILaunchConfiguration config : launchConfigurations) {
                boolean contains = false;
                for (LCDraft configNode : typeNode.getConfigDrafts()) {

                    if (configNode.getName().equals(config.getName()) && !configNode.isMissed()) { // skip missed configurations
                        contains = true;
                        break;
                    }
                }
                if (contains) {
                    configurations.add(config);
                }
            }
        }
        return configurations.toArray(new ILaunchConfiguration[configurations.size()]);
    }

    private static ILaunchManager getLaunchManager() {
        return DebugPlugin.getDefault().getLaunchManager();
    }

    public void clearDrafts() {
        Object oldValue = new HashSet<>(configTypeDrafts);
        configTypeDrafts.clear();
        firePropertyChange("configTypeDrafts", oldValue, configTypeDrafts); //$NON-NLS-1$
    }
}
