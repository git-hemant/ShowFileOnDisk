/** 
 *	Eclipse Plugin ShowFileOnDisk
 *  Copyright (C) 2012  Hemant Singh
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
package net.sp2p.eclipse.util.showfile;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class ShowFileInHandler implements IHandler
{
	protected abstract String getRuntimeCommand(String path);
	
    public void addHandlerListener(IHandlerListener handlerListener)
    {
    	// Do nothing.
    }

    public void dispose()
    {
    	// Do nothing.
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbenchSite site = HandlerUtil.getActiveSite(event);
        if (site == null) {
            return null;
        }
        IStructuredSelection selection = getStructuredSelection(event);
        Object elem = selection.getFirstElement();
        IResource res = null;
        if (elem instanceof IAdaptable) {
            res = (IResource) ((IAdaptable)elem).getAdapter(IResource.class);
        }
        if (elem instanceof IResource) {
            res = (IResource) elem;
        }
        if (res != null) {
            String path = res.getRawLocation().toOSString();
            try {
                Runtime.getRuntime().exec(getRuntimeCommand(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected IStructuredSelection getStructuredSelection(ExecutionEvent event) throws ExecutionException
    {
        Object selection = HandlerUtil.getVariableChecked(event, ISources.ACTIVE_CURRENT_SELECTION_NAME);
        if ((selection instanceof IStructuredSelection) && (!((IStructuredSelection) selection).isEmpty())) {
            return (IStructuredSelection) selection;
        }
        return null;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public boolean isHandled()
    {
        return true;
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener)
    {
    	// Do nothing.
    }

}
