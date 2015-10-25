package org.eclipse.debug.launching.composite;


import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.launching.composite.model.LCDraftModel;
import org.eclipse.debug.ui.DebugUITools;

import java.text.MessageFormat;

/**
 * @author gzheyts
 */

public class CompositeLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

    public CompositeLaunchConfigurationDelegate() {
        super();
    }


    @Override
	public void launch(ILaunchConfiguration parentConfiguration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        ILaunchConfiguration[] childConfigurations = LCDraftModel.getInstance().fetchChildConfigurationsExceptMissed();

        final SubMonitor subMonitor = SubMonitor.convert(monitor
                , MessageFormat.format(CompositeLauncherPluginMessages.launchingConfiguration, new Object[]{parentConfiguration.getName()})
                , childConfigurations.length);


        if (subMonitor.isCanceled()) {
            return;
        }

        final MultiStatus status = new MultiStatus(DebugPlugin.getUniqueIdentifier(), IStatus.OK, "", null); //$NON-NLS-1$

        try {
            for (int i = 0; i < childConfigurations.length; ++i) {
                if (subMonitor.isCanceled()) {
                    break;
                }
                final ILaunchConfiguration upcoming = childConfigurations[i];

                subMonitor.subTask(MessageFormat.format(CompositeLauncherPluginMessages.launchingChildConfiguration, new Object[]{upcoming.getName()}));
                DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {
                	@Override
                    public void run() {

                        try {
                            DebugUITools.buildAndLaunch(upcoming, ILaunchManager.RUN_MODE, new SubProgressMonitor(subMonitor, 1));
                        } catch (CoreException e) {
                            status.merge(e.getStatus());
                        }
                    }
                });
                subMonitor.worked(1);
            }

        } finally {
            subMonitor.done();
        }
        if (!status.isOK()) {
            throw new CoreException(status);
        }

    }

}
