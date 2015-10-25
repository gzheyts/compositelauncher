package org.eclipse.debug.launching.composite.model.converter;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.launching.composite.CompositeLauncherPlugin;

/**
 * @author gzheyts
 */

public class LaunchConfigurationToStringConverter extends Converter {

    public static final String SEPARATOR = "|"; //$NON-NLS-1$

    public LaunchConfigurationToStringConverter() {
        super(ILaunchConfiguration.class, String.class);
    }


    @Override
    public Object convert(Object fromObject) {
        ILaunchConfiguration config = (ILaunchConfiguration) fromObject;

        ILaunchConfigurationType configType = null;
        try {
            configType = config.getType();
        } catch (CoreException e) {
            CompositeLauncherPlugin.error(e.getMessage(), e);
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(configType.getIdentifier()).append(SEPARATOR)
                .append(configType.getName()).append(SEPARATOR)
                .append(config.getName());

        return builder.toString();

    }
}
