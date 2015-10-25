package org.eclipse.debug.launching.composite.model.converter;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.debug.launching.composite.model.LCDraft;

/**
 * @author gzheyts
 */

public class LCDraftToStringConverter extends Converter {
    public static final String SEPARATOR = "|"; //$NON-NLS-1$

    public LCDraftToStringConverter() {
        super(LCDraft.class, String.class);
    }

    @Override
    public Object convert(Object fromObject) {
        LCDraft draft = (LCDraft) fromObject;
        StringBuilder builder = new StringBuilder();
        builder.append(draft.getTypeDraft().getId()).append(SEPARATOR)
                .append(draft.getTypeDraft().getName()).append(SEPARATOR)
                .append(draft.getName());
        return builder.toString();
    }

}
