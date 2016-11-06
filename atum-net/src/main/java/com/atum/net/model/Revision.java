package com.atum.net.model;

public interface Revision {

	public String getVersion();
	public int[] getClientPacketSizes();
	public int[] getServerPacketSizes();
	public ActionSender getActionSender();
}
