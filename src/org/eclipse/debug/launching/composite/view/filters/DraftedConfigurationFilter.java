package org.eclipse.debug.launching.composite.view.filters;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.launching.composite.CompositeLauncherPlugin;
import org.eclipse.debug.launching.composite.model.LCDraftModel;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author gzheyts
 */

public class DraftedConfigurationFilter extends ViewerFilter {
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof ILaunchConfiguration) {
            try {
                return !LCDraftModel.getInstance().isDrafted((ILaunchConfiguration) element);
            } catch (CoreException e) {
                CompositeLauncherPlugin.error(e.getMessage(), e);
            }
        }
        return true;
    }
}
