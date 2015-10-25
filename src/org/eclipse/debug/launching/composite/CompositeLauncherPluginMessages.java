/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.launching.composite;

import org.eclipse.osgi.util.NLS;

public final class CompositeLauncherPluginMessages extends NLS {

    private static final String BUNDLE_NAME = "org.eclipse.debug.launching.composite.messages";//$NON-NLS-1$

    public static String DraftsToolbarActionCheckSelectedTooltip;
    public static String DraftsToolbarActionRemoveSelectedTooltip;
    public static String LaunchTabTitle;
    public static String LaunchTabConfirmRemoveSelectionDialogTitle;
    public static String LaunchTabConfirmRemoveSelectionDialogMessage;
    public static String launchingConfiguration;
    public static String launchingChildConfiguration;
    public static String targetElementIsMissing;


    static {
        NLS.initializeMessages(BUNDLE_NAME, CompositeLauncherPluginMessages.class);
    }
}