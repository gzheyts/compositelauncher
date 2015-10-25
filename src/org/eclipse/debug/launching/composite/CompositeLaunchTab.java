package org.eclipse.debug.launching.composite;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.internal.ui.launchConfigurations.ClosedProjectFilter;
import org.eclipse.debug.internal.ui.launchConfigurations.DeletedProjectFilter;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationTypeFilter;
import org.eclipse.debug.internal.ui.launchConfigurations.WorkingSetsFilter;
import org.eclipse.debug.launching.composite.databinding.ModelSetProperty;
import org.eclipse.debug.launching.composite.model.LCDraftModel;
import org.eclipse.debug.launching.composite.view.AvailableRunConfigurationsFilteredTree;
import org.eclipse.debug.launching.composite.view.actions.CheckSelectedDraftsAction;
import org.eclipse.debug.launching.composite.view.actions.RemoveSelectedDraftsAction;
import org.eclipse.debug.launching.composite.view.dnd.AvailableLaunchConfigurationsDragAdapter;
import org.eclipse.debug.launching.composite.view.dnd.LaunchConfigurationTransfer;
import org.eclipse.debug.launching.composite.view.dnd.SelectedLaunchConfigurationsDropAdapter;
import org.eclipse.debug.launching.composite.view.filters.DraftedConfigurationFilter;
import org.eclipse.debug.launching.composite.view.filters.LaunchTypeWithoutConfigurationsFilter;
import org.eclipse.debug.launching.composite.view.providers.LCDraftLabelProvider;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.internal.databinding.viewers.ViewerObservableListDecorator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PatternFilter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author gzheyts
 */

@SuppressWarnings("restriction")
public class CompositeLaunchTab extends AbstractLaunchConfigurationTab {


    private AvailableRunConfigurationsFilteredTree availableLaunchConfigurationsTree;

    private ToolBarManager toolBarManager;


    private TreeViewer selectedConfigurationsViewer;

    private DraftedConfigurationFilter selectedLaunchFilter;

    private LaunchTypeWithoutConfigurationsFilter noLaunchesForTypeFilter;


    @Override
    public String getHelpContextId() {
        return super.getHelpContextId();
    }

    @Override
    public void setHelpContextId(String id) {
        super.setHelpContextId(id);
    }

    @Override
    public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
        super.activated(workingCopy);
    }

    @Override
    public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
        super.deactivated(workingCopy);
    }


    @Override
    public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
        super.setLaunchConfigurationDialog(dialog);
    }

    @Override
	public void createControl(Composite parent) {


        SashForm sashForm = new SashForm(parent, SWT.SMOOTH);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(sashForm);
        sashForm.setOrientation(SWT.HORIZONTAL);
        sashForm.setVisible(true);

        GridDataFactory.fillDefaults().span(2, 1).applyTo(sashForm);


        createAvailableRunConfigurationsTree(sashForm);
        createSelectedRunConfigurationsTree(sashForm);
        setupDragNDrop();
        bindUI();
        setControl(sashForm);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy iLaunchConfigurationWorkingCopy) {

    }

    private void setupDragNDrop() {
        int dndOperations = DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[]{LaunchConfigurationTransfer.getInstance()};

        availableLaunchConfigurationsTree.getViewer().addDragSupport(dndOperations, transfers, new AvailableLaunchConfigurationsDragAdapter(availableLaunchConfigurationsTree.getViewer()));
        selectedConfigurationsViewer.addDropSupport(dndOperations, transfers, new SelectedLaunchConfigurationsDropAdapter(selectedConfigurationsViewer, this));
    }


    private void bindUI() {


        Realm realm = SWTObservables.getRealm(selectedConfigurationsViewer.getControl().getDisplay());
        ObservableSetTreeContentProvider contentProvider = new ObservableSetTreeContentProvider(
                new ModelSetProperty().setFactory(realm), null);

        selectedConfigurationsViewer.setContentProvider(contentProvider);

        selectedConfigurationsViewer.setLabelProvider(new DecoratingStyledCellLabelProvider(new LCDraftLabelProvider(
                contentProvider.getKnownElements(), BeanProperties.values(new String[]{"name"})), null, null)); //$NON-NLS-1$

        selectedConfigurationsViewer.setInput(LCDraftModel.getInstance());


    }


    private void createAvailableRunConfigurationsTree(Composite parent) {
        Composite launchConfigViewComposite = new Composite(parent, SWT.FLAT);
        launchConfigViewComposite.setLayout(new FillLayout());

        availableLaunchConfigurationsTree = new AvailableRunConfigurationsFilteredTree(
                launchConfigViewComposite
                , SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                , new PatternFilter()
                , DebugUIPlugin.getDefault().getLaunchConfigurationManager().getLaunchGroup(IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP)
                , createViewerFilters()
        );

        availableLaunchConfigurationsTree.createViewControl();
    }

    private void createSelectedRunConfigurationsTree(Composite parent) {

        final Composite leftPartComposite = new Composite(parent, SWT.FLAT);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(leftPartComposite);

        selectedConfigurationsViewer = new TreeViewer(leftPartComposite, SWT.FLAT | SWT.MULTI);
        selectedConfigurationsViewer.setComparator(new ViewerComparator(Collections.reverseOrder(Policy.getComparator())));
        GridDataFactory.fillDefaults().grab(true, true).span(1, 1).applyTo(selectedConfigurationsViewer.getTree());


        createToolBarPanel(leftPartComposite);


    }

    private void createToolBarPanel(final Composite parent) {

        toolBarManager = new ToolBarManager(SWT.FLAT | SWT.VERTICAL);


        toolBarManager.createControl(parent);

        GridDataFactory.fillDefaults().align(GridData.CENTER, GridData.CENTER).minSize(100, 300).grab(false, false).span(1, 1).applyTo(toolBarManager.getControl());


        toolBarManager.add(new RemoveSelectedDraftsAction(availableLaunchConfigurationsTree, selectedConfigurationsViewer, this));

        toolBarManager.add(new CheckSelectedDraftsAction(selectedConfigurationsViewer));

        toolBarManager.update(false);
        showToolBarPanel(toolBarManager.getControl(), false);
        ViewerProperties.multipleSelection().observe(selectedConfigurationsViewer)
                .addChangeListener(new IChangeListener() {
                    @Override
                    public void handleChange(ChangeEvent event) {
                        showToolBarPanel(toolBarManager.getControl()
                                , !((ViewerObservableListDecorator) event.getObservable()).isEmpty());
                    }
                });

    }


    void showToolBarPanel(Control control, boolean show) {
        GridData gd = (GridData) control.getLayoutData();
        gd.exclude = !show;
        control.setVisible(show);
        control.getParent().layout(false);
    }


    @Override
	public void initializeFrom(ILaunchConfiguration configuration) {
        LCDraftModel.getInstance().clearDrafts();
        LCDraftModel.getInstance().loadDrafts(configuration);
        availableLaunchConfigurationsTree.getViewer().refresh();
        selectedConfigurationsViewer.expandAll();
        availableLaunchConfigurationsTree.getViewer().expandAll();
    }


    @Override
	public void performApply(ILaunchConfigurationWorkingCopy config) {
        LCDraftModel.getInstance().saveDrafts(config);
    }


    @Override
	public void updateLaunchConfigurationDialog() {
        super.updateLaunchConfigurationDialog();
    }

    @Override
	public String getName() {
        return CompositeLauncherPluginMessages.LaunchTabTitle;
    }

    @Override
    public Image getImage() {
        return CompositeLaunchPluginImages.getImage(CompositeLauncherPluginConstants.IMG_OBJ_COMPOSITE);
    }


    private ViewerFilter[] createViewerFilters() {
        ArrayList<ViewerFilter> filters = new ArrayList<>();
        if (DebugUIPlugin.getDefault().getPreferenceStore().getBoolean(IInternalDebugUIConstants.PREF_FILTER_LAUNCH_CLOSED)) {
            filters.add(new ClosedProjectFilter());
        }
        if (DebugUIPlugin.getDefault().getPreferenceStore().getBoolean(IInternalDebugUIConstants.PREF_FILTER_LAUNCH_DELETED)) {
            filters.add(new DeletedProjectFilter());
        }
        if (DebugUIPlugin.getDefault().getPreferenceStore().getBoolean(IInternalDebugUIConstants.PREF_FILTER_LAUNCH_TYPES)) {
            filters.add(new LaunchConfigurationTypeFilter());
        }
        if (DebugUIPlugin.getDefault().getPreferenceStore().getBoolean(IInternalDebugUIConstants.PREF_FILTER_WORKING_SETS)) {
            filters.add(new WorkingSetsFilter());
        }

        noLaunchesForTypeFilter = new LaunchTypeWithoutConfigurationsFilter();
        filters.add(noLaunchesForTypeFilter);

        selectedLaunchFilter = new DraftedConfigurationFilter();
        filters.add(selectedLaunchFilter);


        return filters.toArray(new ViewerFilter[filters.size()]);
    }


}
