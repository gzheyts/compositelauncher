package org.eclipse.debug.launching.composite.view;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationTreeContentProvider;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationViewer;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchGroupFilter;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.WorkbenchViewerComparator;

/**
 * @author gzheyts
 */

public final class AvailableRunConfigurationsFilteredTree extends FilteredTree {

    private ILaunchGroup fLaunchGroup = null;

    private ViewerFilter[] fFilters = null;

    private int fTreeStyle = -1;

    private PatternFilter fPatternFilter = null;


    public AvailableRunConfigurationsFilteredTree(Composite parent, int treeStyle, PatternFilter filter, ILaunchGroup group, ViewerFilter[] filters) {
        super(parent, treeStyle, filter, true);
        fLaunchGroup = group;
        fFilters = filters;
        fPatternFilter = filter;
        fTreeStyle = treeStyle;
    }

    @Override
    protected TreeViewer doCreateTreeViewer(Composite cparent, int style) {
        treeViewer = new LaunchConfigurationViewer(cparent, style);
        treeViewer.setLabelProvider(new DecoratingLabelProvider(DebugUITools.newDebugModelPresentation(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
        treeViewer.setComparator(new WorkbenchViewerComparator());
        treeViewer.setContentProvider(new LaunchConfigurationTreeContentProvider(fLaunchGroup.getMode(), cparent.getShell()));
        treeViewer.addFilter(new LaunchGroupFilter(fLaunchGroup));
        treeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
        if (fFilters != null) {
            for (int i = 0; i < fFilters.length; i++) {
                treeViewer.addFilter(fFilters[i]);
            }
        }
        treeViewer.getControl().addHelpListener(new HelpListener() {
            @Override
            public void helpRequested(HelpEvent evt) {
                handleHelpRequest(evt);
            }
        });
        return treeViewer;
    }

    @Override
    protected void createControl(Composite cparent, int treeStyle) {
        super.createControl(cparent, treeStyle);
        setBackground(cparent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
    }

    @Override
    protected void init(int treeStyle, PatternFilter filter) {
    }

    public void createViewControl() {
        super.init(fTreeStyle, fPatternFilter);
    }

    protected void handleHelpRequest(HelpEvent evt) {
    }

    public void refreshTree() {
        textChanged();
    }

}
