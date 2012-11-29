package net.sp2p.eclipse.util.showfile;

public class ShowFileInWindowsHandler extends ShowFileInHandler
{
	protected String getRuntimeCommand(String path)
	{
		return "explorer /select,  " + path;
	}
}
