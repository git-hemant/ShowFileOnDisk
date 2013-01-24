/** 
 *	Eclipse Plugin ShowFileOnDisk
 *  Copyright (C) 2013  Hemant Singh
 *  Date: 1 January 2013
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */    	
package com.github.hs2501.eclipse.util.showfile;

import java.io.File;
import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * The base handler for all the platforms. It iterates in the selected resources
 * and attempts to open the platform specific file browser (windows explorer,
 * mac finder, etc).
 * 
 * @author Hemant Singh
 */
public abstract class ShowFileInHandler extends AbstractHandler
{
	
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbenchSite site = HandlerUtil.getActiveSite(event);
        if (site == null) {
            return null;
        }
        
        // We will support multi-selection
        IStructuredSelection selection = getStructuredSelection(event);
        Iterator<?> iterator = selection.iterator();
		while (iterator.hasNext()) {
			Object elem = iterator.next();
			IResource res = null;
			// Most of the time the selected item will not be IResource but
			// adaptable to IResource for instance IJavaElement.
			if (elem instanceof IAdaptable) {
				res = (IResource) ((IAdaptable) elem)
						.getAdapter(IResource.class);
			}
			else if (elem instanceof IResource) {
				res = (IResource) elem;
			}
			
			// TODO: If we can't find the corresponding resource
			// then we should disable the command on the selection.
			if (res == null) {
				return null;
			}

			String path = mapResourceToFilePath(res);

			// We will get null value in the path, if we couldn't find 
			if (path == null) {
				// TODO: Localize the string
				String msg = MessageFormat.format("Unable to find the corresponding file for the selected resource: {0}", res);//$NON-NLS-1$
				IStatus status = new Status(IStatus.WARNING, ShowFilePlugin.PLUGIN_ID, msg);
				ShowFilePlugin.getDefault().getLog().log(status);
			} else {
				try {
					// Create OS specific command which we will execute.
					String cmdToExecute = getRuntimeCommand(path);
					Runtime.getRuntime().exec(cmdToExecute);
				} catch (Exception e) {
					// TODO: Localize the string
					String msg = MessageFormat.format("Error in opening the file resource: {0}", res);//$NON-NLS-1$
					IStatus status = new Status(IStatus.ERROR, ShowFilePlugin.PLUGIN_ID, msg, e);
					ShowFilePlugin.getDefault().getLog().log(status);
				}
			}
        }
        return null;
    }
    
    private String mapResourceToFilePath(IResource res) {
		// Validation for the resource which doesn't have any associated location.
    	if (res.getLocation() == null || res.getLocation().toOSString() == null) {
    		return null;
    	}
		// In-case of the linked resource, we need to use the path from location URI
		// as linked resources are virtual items which are mapped to a location.
		// In-case of lined resource IResource.getLocation() will return it's location
		// from .project file.
		String resourcePathOnOS = res.getLocation().toOSString();
		File file = new File(resourcePathOnOS);
		// TODO: If somehow we didn't got valid path, which can happen
		// in-case we are dealing with items which have some custom EFS
		// implementation underneath. Then we will recursively look for
		// the parent resource for which we can find the valid path in
		// filesystem of OS.
		// TODO: This should be part of the enablement as well.
		if (!file.exists()) {
			
			// This can happen for virtual resources, for which we can never find the corresponding file on disk
			// or resources with there own EFS implementation, here we will attempt to use some heuristics to
			// map EFS implementation to it's disk path.
			String filePath = resourcePathOnOS;
			// First try to remove the file extension
			if (filePath.lastIndexOf('.') > -1) {
				filePath = filePath.substring(0, filePath.lastIndexOf('.'));
			}
			file = new File(filePath);
			if (!file.exists()) {
				IResource parentRes = res;
				while (parentRes != null || !file.exists()) {
					parentRes = parentRes.getParent();
					if (parentRes == null || parentRes.getRawLocation() == null) {
						break;
					}
					file = new File(parentRes.getRawLocation().toOSString());
				}
			}
		}
		if (file != null && file.exists()) {
			return file.getAbsolutePath();
		}
		
		// If we can't find the children, then try lookup parent, this time without resource path location
		file = new File(resourcePathOnOS);
		while (file != null && !file.exists()) {
			file = file.getParentFile();
		}
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		return null;
	}

	protected abstract String getRuntimeCommand(String path);

    protected IStructuredSelection getStructuredSelection(ExecutionEvent event) throws ExecutionException
    {
        Object selection = HandlerUtil.getVariableChecked(event, ISources.ACTIVE_CURRENT_SELECTION_NAME);
        if ((selection instanceof IStructuredSelection) && (!((IStructuredSelection) selection).isEmpty())) {
            return (IStructuredSelection) selection;
        }
        return null;
    }
}
