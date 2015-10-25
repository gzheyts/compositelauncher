package org.eclipse.debug.launching.composite.databinding;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.property.set.DelegatingSetProperty;
import org.eclipse.core.databinding.property.set.ISetProperty;
import org.eclipse.debug.launching.composite.model.LCDraftModel;
import org.eclipse.debug.launching.composite.model.LCTypeDraft;

/**
 * @author gzheyts
 */

public class ModelSetProperty extends DelegatingSetProperty {

    private ISetProperty modeConfigTypeDraftsProperty = BeanProperties.set(LCDraftModel.class, "configTypeDrafts"); //$NON-NLS-1$
    private ISetProperty modelConfigDraftsProperty = BeanProperties.set(LCTypeDraft.class, "configDrafts"); //$NON-NLS-1$

    @Override
    protected ISetProperty doGetDelegate(Object source) {
        if (source instanceof LCDraftModel) {
            return modeConfigTypeDraftsProperty;
        } else if (source instanceof LCTypeDraft) {
            return modelConfigDraftsProperty;
        }
        return null;
    }
}
