package org.eclipse.debug.launching.composite.view.filters;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.launching.composite.CompositeLauncherPlugin;
import org.eclipse.debug.launching.composite.model.LCDraftModel;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author gzheyts
 */

public class LaunchTypeWithoutConfigurationsFilter extends ViewerFilter {

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof ILaunchConfigurationType) {

            try {
                ILaunchConfigurationType configType = (ILaunchConfigurationType) element;
                ILaunchConfiguration[] launchConfigurations = DebugPlugin.getDefault()
                        .getLaunchManager().getLaunchConfigurations(configType);

                if (launchConfigurations == null || launchConfigurations.length == 0) {
                    return false;
                } else if (LCDraftModel.getInstance().containsDraftAll(launchConfigurations)) {
                    return false;
                }
            } catch (CoreException e) {
                CompositeLauncherPlugin.error(e.getMessage(), e);
            }
        }
        return true;
    }

}
