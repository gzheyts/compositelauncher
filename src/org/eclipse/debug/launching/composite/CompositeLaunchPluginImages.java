package org.eclipse.debug.launching.composite;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * @author gzheyts
 */

public class CompositeLaunchPluginImages {

    private static ImageRegistry imageRegistry;

    private static HashMap<String, ImageDescriptor> imageDescriptors;


    private static URL ICON_BASE_URL = null;

    static {
        String pathSuffix = "icons/"; //$NON-NLS-1$

        try {
            ICON_BASE_URL = new URL(CompositeLauncherPlugin.getDefault().getDescriptor().getInstallURL(), pathSuffix);
        } catch (MalformedURLException e) {
            CompositeLauncherPlugin.error(e.getMessage(), e);
            // do nothing
        }
    }


    /**
     * Declare all images
     */
    private static void declareImages() {
        declareRegistryImage(CompositeLauncherPluginConstants.IMG_OBJ_COMPOSITE, "composite.gif"); //$NON-NLS-1$
        declareRegistryImage(CompositeLauncherPluginConstants.IMG_OBJ_ACTION_REMOVE_SELECTED, "remove-selected.png"); //$NON-NLS-1$
        declareRegistryImage(CompositeLauncherPluginConstants.IMG_OBJ_ACTION_CHECK_SELECTED, "check-selected.png"); //$NON-NLS-1$
    }


    private final static void declareRegistryImage(String key, String path) {
        ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
        try {
            desc = ImageDescriptor.createFromURL(makeIconFileURL(path));
        } catch (MalformedURLException me) {
            CompositeLauncherPlugin.error(me.getMessage(), me);
        }

        imageRegistry.put(key, desc);
        imageDescriptors.put(key, desc);
    }


    public static ImageRegistry getImageRegistry() {
        if (imageRegistry == null) {
            initializeImageRegistry();
        }
        return imageRegistry;
    }

    public static ImageRegistry initializeImageRegistry() {
        imageRegistry = new ImageRegistry(CompositeLauncherPlugin.getStandardDisplay());
        imageDescriptors = new HashMap<>(30);
        declareImages();
        return imageRegistry;
    }

    public static Image getImage(String key) {
        return getImageRegistry().get(key);
    }

    public static ImageDescriptor getDescriptor(String key) {
        return getImageRegistry().getDescriptor(key);
    }


    private static URL makeIconFileURL(String iconPath) throws MalformedURLException {
        if (ICON_BASE_URL == null) {
            throw new MalformedURLException();
        }
        return new URL(ICON_BASE_URL, iconPath);
    }


    public static void dispose() {
        if (imageRegistry != null) {
            imageRegistry.dispose();
            imageRegistry = null;
        }
        if (imageDescriptors != null) {
            imageDescriptors.clear();
            imageDescriptors = null;
        }
    }
}


