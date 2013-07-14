/**
 * 
 */
package me.dreilopz.swebserv.impl;

import java.util.LinkedList;

import edu.rit.sim.Event;
import edu.rit.sim.Simulation;
import me.dreilopz.swebserv.ExponentialPrng;
import me.dreilopz.swebserv.UniformPrng;
import me.dreilopz.swebserv.WebServer;

/**
 * @author dreilopz0
 *
 */
public class PJWebServer implements WebServer {
	private double meanServiceRate;
	private long nReq;
	private long seed;
	private UniformPrng uniformPrng;
	private ExponentialPrng reqPrng;
	private ExponentialPrng serverPrng;
	
	private LinkedList<Request> reqQueue;
	private long iReq;
	private Simulation sim;
	
	private double meanReqRate;
	/**
	 * @param meanReqRate the meanReqRate to set
	 */
	public void setMeanReqRate(double meanReqRate) {
		this.meanReqRate = meanReqRate;
	}

	/**
	 * @param meanServiceRate the meanServiceRate to set
	 */
	public void setMeanServiceRate(double meanServiceRate) {
		this.meanServiceRate = meanServiceRate;
	}

	/**
	 * @param nReq the nReq to set
	 */
	public void setnReq(long nReq) {
		this.nReq = nReq;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(long seed) {
		this.seed = seed;
	}

	/**
	 * @param uniformPrng the uniformPrng to set
	 */
	public void setUniformPrng(UniformPrng uniformPrng) {
		this.uniformPrng = uniformPrng;
	}

	/**
	 * @param reqPrng the reqPrng to set
	 */
	public void setReqPrng(ExponentialPrng reqPrng) {
		this.reqPrng = reqPrng;
	}

	/**
	 * @param serverPrng the serverPrng to set
	 */
	public void setServerPrng(ExponentialPrng serverPrng) {
		this.serverPrng = serverPrng;
	}

	public void simulate() {
		generateRequest();
		sim.run();
	}
	
	private static class Request
	{
	private long requestNumber;
	private int reqType;
	private static int i = 0;
	public Request
		(long requestNumber)
		{
		this.requestNumber = requestNumber;
		i = i == 0 ? 1 :
			i == 1 ? 2:
				i == 2 ? 0 :
					-1;
		this.reqType = i;
		}
	public String toString()
		{
		return "Request " + requestNumber + " with type " + reqType;
		}
	

	}


	private void generateRequest()
	{
	addToQueue (new Request (++ iReq));
	if (iReq < nReq)
		{
		sim.doAfter (reqPrng.rand(), new Event()
			{
			public void perform() { generateRequest(); }
			});
		}
	}

	private void addToQueue
	(Request request)
	{
	System.out.printf ("%.3f %s added to queue%n",
		sim.time(), request);
	reqQueue.add (request);
	if (reqQueue.size() == 1) startServing();
	}

	private void startServing()
	{
	System.out.printf ("%.3f Started serving %s%n",
		sim.time(), reqQueue.getFirst());
	sim.doAfter (serverPrng.rand(), new Event()
		{
		public void perform() { removeFromQueue(); }
		});
	}

	private void removeFromQueue()
	{
	System.out.printf ("%.3f %s removed from queue%n",
		sim.time(), reqQueue.removeFirst());
	if (reqQueue.size() > 0) startServing();
	}
	
	public PJWebServer createInstance(double meanReqRate,
			double meanServiceRate, long nReq, long seed,
			UniformPrng uniformPrng, ExponentialPrng reqPrng,
			ExponentialPrng serverPrng) {
		PJWebServer webServer = new PJWebServer();
		webServer.meanReqRate = meanReqRate; 
		webServer.meanServiceRate = meanServiceRate;
		webServer.nReq = nReq;
		webServer.seed = seed;
		webServer.uniformPrng = uniformPrng;
		webServer.reqPrng = reqPrng;
		webServer.serverPrng = serverPrng;
		webServer.reqQueue = new LinkedList<Request>();
		webServer.sim = new Simulation();
		return webServer;
	}
}
