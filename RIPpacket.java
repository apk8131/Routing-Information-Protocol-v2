/**
 *  RIPpacket.java
 *  
 *  @author Ashutosh Katkar
 *  
 *  Its helper class to define packet format.
 */

import java.io.Serializable;

public class RIPpacket implements Serializable {

	private static final long serialVersionUID = 1L;
	String sourceIP;
	String destIP;
	int costs[] = new int[4];
	String nextHop[] = new String[4];
	String dest[] = new String[4];

	/**
	 * Constructor
	 * 
	 * @param sourceIP
	 * @param destIP
	 */
	RIPpacket(String sourceIP, String destIP) {
		this.sourceIP = sourceIP;
		this.destIP = destIP;
		costs = Router.costs;
		nextHop = Router.nextHop;
		dest = Router.dest;
	}
}
