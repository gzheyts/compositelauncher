package org.eclipse.debug.launching.composite;


import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author gzheyts
 */

public class CompositeLauncherPlugin extends AbstractUIPlugin {


    public static final String ID_PLUGIN = "org.eclipse.debug.launching.composite.CompositeLaunchSupport"; //$NON-NLS-1$


    private static CompositeLauncherPlugin fgDefault;

    public CompositeLauncherPlugin(IPluginDescriptor descriptor) {
        super(descriptor);
        fgDefault = this;

    }

    public static CompositeLauncherPlugin getDefault() {
        return fgDefault;
    }

    public static void info(final String message) {
        getDefault().getLog().log(new Status(IStatus.INFO, ID_PLUGIN, message));
    }

    public static void error(final String message, final Throwable e) {
        getDefault().getLog().log(new Status(IStatus.ERROR, ID_PLUGIN, message, e));
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);

        CompositeLaunchPluginImages.dispose();
    }


    public static Display getStandardDisplay() {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        return display;
    }

}
