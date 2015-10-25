package org.eclipse.debug.launching.composite.view.dnd;

import org.eclipse.debug.launching.composite.CompositeLauncherPlugin;
import org.eclipse.debug.launching.composite.CompositeLaunchTab;
import org.eclipse.debug.launching.composite.model.LCDraft;
import org.eclipse.debug.launching.composite.model.LCDraftModel;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

import java.util.List;

/**
 * @author gzheyts
 */

public class SelectedLaunchConfigurationsDropAdapter extends ViewerDropAdapter {

    private CompositeLaunchTab launchTab;

    public SelectedLaunchConfigurationsDropAdapter(Viewer viewer, CompositeLaunchTab tab) {
        super(viewer);
        this.launchTab = tab;

    }

    @Override
    public void drop(DropTargetEvent event) {
        super.drop(event);
    }

    @Override
    public boolean performDrop(Object data) {
        LCDraft[] drafts = (LCDraft[]) data;
        if (drafts == null || drafts.length == 0) {
            return false;
        }

        try {
            List<LCDraft> appended = LCDraftModel.getInstance().appendAll(drafts);
            if (!appended.isEmpty()) {
                getViewer().setSelection(new StructuredSelection(appended), true);
                launchTab.updateLaunchConfigurationDialog();
            }
        } catch (Exception e) {
            CompositeLauncherPlugin.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    @Override
    public boolean validateDrop(Object target, int operation, TransferData transferType) {
        return true;
    }
}
