package org.eclipse.debug.launching.composite.view.dnd;


import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.launching.composite.CompositeLauncherPlugin;
import org.eclipse.debug.launching.composite.model.LCDraft;
import org.eclipse.debug.launching.composite.model.LCTypeDraft;
import org.eclipse.debug.launching.composite.model.converter.LCDraftToStringConverter;
import org.eclipse.debug.launching.composite.model.converter.LaunchConfigurationToStringConverter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author gzheyts
 */

public class LaunchConfigurationTransfer extends ByteArrayTransfer {

    private static LaunchConfigurationTransfer instance = new LaunchConfigurationTransfer();
    private static final String TYPE_NAME = "LAUNCHCONFIGURATION_TYPE"; //$NON-NLS-1$
    private static final int TYPE_ID = registerType(TYPE_NAME);
    private static IConverter toStringConverter = new LaunchConfigurationToStringConverter();


    public static LaunchConfigurationTransfer getInstance() {
        return instance;
    }

    private LaunchConfigurationTransfer() {
    }

    @Override
    protected String[] getTypeNames() {
        return new String[]{TYPE_NAME};
    }

    @Override
    protected int[] getTypeIds() {
        return new int[]{TYPE_ID};
    }


    @Override
    protected Object nativeToJava(TransferData transferData) {
        byte[] bytes = (byte[]) super.nativeToJava(transferData);
        return fromByteArray(bytes);
    }

    @Override
    protected void javaToNative(Object object, TransferData transferData) {
        byte[] bytes = toByteArray(object);
        if (bytes != null) {
            super.javaToNative(bytes, transferData);
        }
    }

    @Override
    public boolean isSupportedType(TransferData transferData) {
        return super.isSupportedType(transferData);
    }

    @Override
    protected boolean validate(Object object) {
        return super.validate(object);
    }

    private byte[] toByteArray(Object object) {

        IStructuredSelection selection = (IStructuredSelection) object;

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);

        try {
            out.writeInt(selection.size());
            for (Iterator iterator = selection.iterator(); iterator.hasNext(); ) {
                Object next = iterator.next();
                writeConfiguration((ILaunchConfiguration) next, out);
            }
            out.flush();
            out.close();
            return byteOut.toByteArray();

        } catch (IOException e) {
            CompositeLauncherPlugin.error(e.getMessage(), e);
            return null;
        }
    }

    private void writeConfiguration(ILaunchConfiguration config, DataOutputStream out) throws IOException {
        out.writeUTF((String) toStringConverter.convert(config));
    }

    private Object fromByteArray(byte[] bytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            int size = in.readInt();

            Map<String, LCTypeDraft> typeMap = new HashMap<>();
            LCDraft[] drafts = new LCDraft[size];
            for (int i = 0; i < size; ++i) {
                drafts[i] = readDraft(in, typeMap);
            }
            return drafts;
        } catch (IOException e) {
            CompositeLauncherPlugin.error(e.getMessage(), e);
            return null;
        }
    }

    private LCDraft readDraft(DataInputStream in, Map<String, LCTypeDraft> typesMap) throws IOException {
        StringTokenizer tokenizer = new StringTokenizer(in.readUTF(), LCDraftToStringConverter.SEPARATOR);
        String configTypeId = tokenizer.nextToken();
        String configTypeName = tokenizer.nextToken();
        String configName = tokenizer.nextToken();

        LCTypeDraft typeDraft;

        if (typesMap.containsKey(configTypeId)) {
            typeDraft = typesMap.get(configTypeId);
        } else {
            typeDraft = new LCTypeDraft(configTypeId, configTypeName);
            typesMap.put(configTypeId, typeDraft);
        }

        return typeDraft.getOrCreateLCDraft(configName);
    }
}
