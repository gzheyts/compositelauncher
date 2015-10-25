package org.eclipse.debug.launching.composite.view.providers;

import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.value.IValueProperty;

import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.launching.composite.CompositeLauncherPluginMessages;
import org.eclipse.debug.launching.composite.model.LCDraft;
import org.eclipse.debug.launching.composite.model.LCTypeDraft;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledString;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;

import java.util.Set;

/**
 * @author gzheyts
 */

@SuppressWarnings("restriction")
public class LCDraftLabelProvider extends LabelProvider implements IStyledLabelProvider {


    public static final String GREEN_COLOR_NAME = "org.eclipse.debug.launching.composite.view.providers.LCDraftLabelProvider.color.green"; //$NON-NLS-1$
    public static final String RED_COLOR_NAME = "org.eclipse.debug.launching.composite.view.providers.LCDraftLabelProvider.color.red"; //$NON-NLS-1$


    static {
        JFaceResources.getColorRegistry().put(GREEN_COLOR_NAME, new RGB(35, 145, 39));
        JFaceResources.getColorRegistry().put(RED_COLOR_NAME, new RGB(221, 27, 27));
    }


    protected IObservableMap[] attributeMaps;

    private IMapChangeListener mapChangeListener = new IMapChangeListener() {
        @Override
        public void handleMapChange(MapChangeEvent event) {

            Set<?> affectedElements = event.diff.getChangedKeys();
            LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent(
                    LCDraftLabelProvider.this, affectedElements
                    .toArray());
            fireLabelProviderChanged(newEvent);
        }
    };

    public LCDraftLabelProvider(final IObservableSet viewerElementSet, final IValueProperty[] labelProperties) {


        attributeMaps = Properties.observeEach(viewerElementSet, labelProperties);

        for (int i = 0; i < attributeMaps.length; i++) {
            attributeMaps[i].addMapChangeListener(mapChangeListener);
        }
    }


    @Override
    public StyledString getStyledText(Object element) {

        //Object value = attributeMaps[0].get(element);

        StyledString styledString = new StyledString();
        if (element instanceof LCTypeDraft) {
            LCTypeDraft typeDraft = (LCTypeDraft) element;
            styledString.append(typeDraft.getName());
            if (typeDraft.isMissed()) {
                appendMissing(styledString);
            }

        } else if (element instanceof LCDraft) {
            LCDraft draft = (LCDraft) element;
            styledString.append(draft.getName());
            if (draft.isMissed()) {
                appendMissing(styledString);
            }
        }
        return styledString;
    }

    private StyledString appendMissing(StyledString styledString) {
        return styledString.append(CompositeLauncherPluginMessages.targetElementIsMissing, new StyledString.Styler() {
            @Override
            public void applyStyles(TextStyle textStyle) {
                textStyle.foreground = JFaceResources.getColorRegistry().get(RED_COLOR_NAME);
                textStyle.font = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);
            }
        });

    }

    @Override
    public Image getImage(Object element) {
        String imageKey = null;
        if (element instanceof LCDraft) {
            imageKey = ((LCDraft) element).getTypeDraft().getId();
        } else if (element instanceof LCTypeDraft) {
            imageKey = ((LCTypeDraft) element).getId();
        }
        return imageKey == null ? null : DebugPluginImages.getImage(imageKey);
    }

    @Override
    public void dispose() {
        for (int i = 0; i < attributeMaps.length; i++) {
            attributeMaps[i].removeMapChangeListener(mapChangeListener);
        }
        super.dispose();
        this.attributeMaps = null;
        this.mapChangeListener = null;
    }
}


