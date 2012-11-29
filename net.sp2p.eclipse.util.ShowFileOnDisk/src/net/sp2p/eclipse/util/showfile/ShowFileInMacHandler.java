package net.sp2p.eclipse.util.showfile;

public class ShowFileInMacHandler extends ShowFileInHandler
{

	protected String getRuntimeCommand(String path)
	{
		return "open -R " + path;
	}

}
