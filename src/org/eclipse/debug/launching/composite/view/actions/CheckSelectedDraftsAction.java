package org.eclipse.debug.launching.composite.view.actions;

import org.eclipse.debug.launching.composite.CompositeLauncherPluginMessages;
import org.eclipse.debug.launching.composite.CompositeLauncherPluginConstants;
import org.eclipse.debug.launching.composite.CompositeLaunchPluginImages;
import org.eclipse.debug.launching.composite.CompositeLauncherPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * @author gzheyts
 */

//todo: check child launch configuration if it can be launched
public class CheckSelectedDraftsAction extends Action {

    @SuppressWarnings("unused")
	private final TreeViewer viewer;

    public CheckSelectedDraftsAction(final TreeViewer viewer) {
        super("", AS_PUSH_BUTTON); //$NON-NLS-1$
        setToolTipText(CompositeLauncherPluginMessages.DraftsToolbarActionCheckSelectedTooltip);
        setImageDescriptor(CompositeLaunchPluginImages.getDescriptor(CompositeLauncherPluginConstants.IMG_OBJ_ACTION_CHECK_SELECTED));
        this.viewer = viewer;
    }

    @Override
    public void run() {
        CompositeLauncherPlugin.info(this.getText());
    }

}
