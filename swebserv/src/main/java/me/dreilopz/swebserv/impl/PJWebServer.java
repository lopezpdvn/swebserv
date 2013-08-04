/**
 * 
 */
package me.dreilopz.swebserv.impl;

import java.util.LinkedList;

import edu.rit.numeric.ListSeries;
import edu.rit.numeric.Series;
import edu.rit.sim.Event;
import edu.rit.sim.Simulation;
import me.dreilopz.swebserv.ExponentialPrng;
import me.dreilopz.swebserv.UniformPrng;
import me.dreilopz.swebserv.WebServer;
import me.dreilopz.swebserv.Request;

public class PJWebServer implements WebServer {
	private double meanServiceRate;
	private double dropRatio;
	private long nReq;
	private long seed;
	private long iDrop;
	private UniformPrng uniformPrng;
	private ExponentialPrng reqPrng;
	private ExponentialPrng serverPrng;
	private Request reqFactory;
	
	private LinkedList<Request> reqQueue;
	private long iReq;
	private Simulation sim;
	private ListSeries waitTime;
	
	private double waitTimeMean;
	private long maxReqQueueLength;
	private double waitTimeStddev;
	private int i;

	/**
	 * @param meanServiceRate the meanServiceRate to set
	 */
	public void setMeanServiceRate(double meanServiceRate) {
		this.meanServiceRate = meanServiceRate;
	}

	/**
	 * @param nReq the nReq to set
	 */
	public void setNReq(long nReq) {
		this.nReq = nReq;
	}
	
	public long getNReq() {
		return nReq;
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
	
	private void reset() {
		iDrop = iReq = 0;
		reqQueue = new LinkedList<Request>();
		sim = new Simulation();
		waitTime = new ListSeries();
	}

	public void simulate() {
		reset();
		generateRequest();
		sim.run();
		Series.Stats wt = waitTime.stats();
		this.dropRatio = ((double)iDrop) / ((double)nReq);
		this.waitTimeMean = wt.mean;
		this.waitTimeStddev = wt.stddev;
	}

	private void generateRequest()
	{
		Request req = reqFactory.createInstance();
		req.setReqNo(++iReq);
		req.setReqType(getReqType());
		req.setStartTime(sim.time());
		addToQueue (req);
		if (iReq < nReq) {
			sim.doAfter (reqPrng.rand(), new Event()
				{
				public void perform() { generateRequest(); }
				});
			}
		}

	private void addToQueue
	(Request request)
	{
		@SuppressWarnings("unused")
		String msg = "";
		if (reqQueue.size() < this.maxReqQueueLength)
			{
			reqQueue.add (request);
			msg += "Added";
			if (reqQueue.size() == 1) startServing();
			}
		else
			{
			msg += "Dropped";
			++ iDrop;
			}
		msg += " REQUEST " + request.toString();
		//System.out.println(msg);
	}

	private void startServing() {
		sim.doAfter (serverPrng.rand(), new Event()
			{
			public void perform() { removeFromQueue(); }
			});
	}

	private void removeFromQueue()
	{
		Request req = reqQueue.removeFirst();
		waitTime.add (sim.time() - req.getStartTime());
		if (reqQueue.size() > 0) startServing();
	}

	public double getWaitTimeMean() {
		// TODO Auto-generated method stub
		return waitTimeMean;
	}

	public double getWaitTimeStddev() {
		// TODO Auto-generated method stub
		return waitTimeStddev;
	}


	public double getDropRatio() {
		// TODO Auto-generated method stub
		return dropRatio;
	}

	public void setMaxReqQueueLength(long maxReqQueueLength) {
		this.maxReqQueueLength = maxReqQueueLength;
	}
	
	public long getMaxReqQueueLength() {
		return maxReqQueueLength;
	}
	
	private int getReqType() {
		int rt = 0;
		if(i % 3 == 0) {
			rt = 0;
		} else if (i % 3 == 1) {
			rt = 1;
		} else if (i % 3 == 2) {
			rt = 2;
		}
		i++;
		return rt;
	}

	public void setReqFactory(Request reqFactory) {
		this.reqFactory = reqFactory;
	}

}


