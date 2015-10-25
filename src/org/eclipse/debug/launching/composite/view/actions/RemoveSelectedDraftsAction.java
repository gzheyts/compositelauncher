package org.eclipse.debug.launching.composite.view.actions;

import org.eclipse.debug.launching.composite.*;
import org.eclipse.debug.launching.composite.model.LCDraft;
import org.eclipse.debug.launching.composite.model.LCDraftModel;
import org.eclipse.debug.launching.composite.view.AvailableRunConfigurationsFilteredTree;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author gzheyts
 */

public class RemoveSelectedDraftsAction extends Action {

    private TreeViewer selectionsViewer;
    private AvailableRunConfigurationsFilteredTree availableViewer;
    private CompositeLaunchTab launchTab;

    public RemoveSelectedDraftsAction(final AvailableRunConfigurationsFilteredTree availableViewer, final TreeViewer selectionsViewer, CompositeLaunchTab launchTab) {
        super("", AS_PUSH_BUTTON); //$NON-NLS-1$
        setToolTipText(CompositeLauncherPluginMessages.DraftsToolbarActionRemoveSelectedTooltip);
        setImageDescriptor(CompositeLaunchPluginImages.getDescriptor(CompositeLauncherPluginConstants.IMG_OBJ_ACTION_REMOVE_SELECTED));

        this.selectionsViewer = selectionsViewer;
        this.availableViewer = availableViewer;
        this.launchTab = launchTab;
    }

    @Override
    public void run() {
        CompositeLauncherPlugin.info(this.getText());

        IStructuredSelection selection = (IStructuredSelection) selectionsViewer.getSelection();

        if (selection.isEmpty()) {
            return;
        }

        boolean result = MessageDialog.openQuestion(selectionsViewer.getControl().getShell()
                , CompositeLauncherPluginMessages.LaunchTabConfirmRemoveSelectionDialogTitle
                , CompositeLauncherPluginMessages.LaunchTabConfirmRemoveSelectionDialogMessage);
        if (result) {
            List<LCDraft> configDrafts = new LinkedList<>();

            for (Iterator<?> iter = selection.iterator(); iter.hasNext(); ) {
                Object item = iter.next();
                if (item instanceof LCDraft) {
                    configDrafts.add((LCDraft) item);
                }
            }

            if (!configDrafts.isEmpty()) {
                LCDraftModel.getInstance().removeLCDrafts(configDrafts);
                availableViewer.refreshTree();
                launchTab.updateLaunchConfigurationDialog();
            }

        }

    }
}
