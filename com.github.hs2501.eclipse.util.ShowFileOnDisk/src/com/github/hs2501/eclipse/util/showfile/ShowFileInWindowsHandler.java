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

/**
 * The handler for windows environment. Here we will use the windows command 'explorer /select <Path>'.
 * 
 * @author Hemant Singh
 */
public class ShowFileInWindowsHandler extends ShowFileInHandler
{
	protected String getRuntimeCommand(String path)
	{
		return "explorer /select,  " + path; //$NON-NLS-1$
	}
}
