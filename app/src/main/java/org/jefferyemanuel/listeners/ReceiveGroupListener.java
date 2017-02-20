package org.jefferyemanuel.listeners;

import java.util.ArrayList;

/*interface used to notify any observers that a connection to reddit has yielded the group results*/
public interface ReceiveGroupListener {

	void goupListingAcquired(ArrayList<String> list);
	
	
	
}
