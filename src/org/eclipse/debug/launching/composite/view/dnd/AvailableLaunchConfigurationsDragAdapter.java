package org.eclipse.debug.launching.composite.view.dnd;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

/**
 * @author gzheyts
 */

public class AvailableLaunchConfigurationsDragAdapter implements DragSourceListener {

    private StructuredViewer viewer;

    public AvailableLaunchConfigurationsDragAdapter(final StructuredViewer viewer) {
        this.viewer = viewer;
    }


    /**
     * Method declared on DragSourceListener
     */
    @Override
	public void dragFinished(DragSourceEvent event) {
        if (!event.doit)
            return;

        if (event.detail == DND.DROP_MOVE) {
            //clear selection
            viewer.setSelection(StructuredSelection.EMPTY);
            //hide drafted configurations
            viewer.refresh();

        }
    }

    /**
     * Method declared on DragSourceListener
     */
    @Override
	public void dragSetData(DragSourceEvent event) {
        event.data = viewer.getSelection();
    }

    /**
     * Method declared on DragSourceListener
     */
    @Override
	public void dragStart(DragSourceEvent event) {
        event.doit = !viewer.getSelection().isEmpty();
    }
}