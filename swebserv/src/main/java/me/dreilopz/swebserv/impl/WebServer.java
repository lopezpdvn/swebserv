/**
 * 
 */
package me.dreilopz.swebserv.impl;

import java.util.LinkedList;

import edu.rit.numeric.ListSeries;
import edu.rit.numeric.Series;
import me.dreilopz.swebserv.ExponentialPrng;
import me.dreilopz.swebserv.Request;

class WebServer implements me.dreilopz.swebserv.WebServer {
	//private double meanServiceRate;
	private double dropRatio;
	private long nReq;
	//private long seed;
	private long iDrop;
	//private UniformPrng uniformPrng;
	private ExponentialPrng serverPrng;
	private Request reqFactory;
	
	private LinkedList<Request> reqQueue;
	private long iReq;
	private Simulation sim;
	private ListSeries waitTime;
	
	private double meanWaitTime;
	private long maxReqQueueLength;
	private double waitTimeStddev;
	private int i;

//	/**
//	 * @param meanServiceRate the meanServiceRate to set
//	 */
//	public void setMeanServiceRate(double meanServiceRate) {
//		this.meanServiceRate = meanServiceRate;
//	}

	/**
	 * @param nReq the nReq to set
	 */
	public void setNReq(long nReq) {
		this.nReq = nReq;
	}
	
	public long getNReq() {
		return nReq;
	}

//	/**
//	 * @param seed the seed to set
//	 */
//	public void setSeed(long seed) {
//		this.seed = seed;
//	}

//	/**
//	 * @param uniformPrng the uniformPrng to set
//	 */
//	public void setUniformPrng(UniformPrng uniformPrng) {
//		this.uniformPrng = uniformPrng;
//	}

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
		this.meanWaitTime = wt.mean;
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
			sim.doAfter (req.getArrivalTime(), new Event()
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

	public double getMeanWaitTime() {
		// TODO Auto-generated method stub
		return meanWaitTime;
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

class Simulation
	{

//Hidden data members.

	// Minimum-priority queue of events. Uses a heap data structure. The entry
	// at index 0 is a sentinel with time = 0.0.
	private Event[] heap = new Event [1024];

	// Number of entries in the heap (including the sentinel).
	private int N = 1;

	// Simulation time.
	private double T = 0.0;

//Exported constructors.

	/**
	 * Construct a new simulation.
	 */
	public Simulation()
		{
		heap[0] = new Event() { public void perform() { } };
		heap[0].sim = this;
		heap[0].time = 0.0;
		}

//Exported operations.

	/**
	 * Returns the current simulation time.
	 *
	 * @return  Simulation time.
	 */
	public double time()
		{
		return T;
		}

	/**
	 * Schedule the given event to be performed at the given time in this
	 * simulation.
	 *
	 * @param  t      Simulation time for <TT>event</TT>.
	 * @param  event  Event to be performed.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>t</TT> is less than the current
	 *     simulation time.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>event</TT> is null.
	 */
	public void doAt
		(double t,
		 Event event)
		{
		// Verify preconditions.
		if (t < T)
			{
			throw new IllegalArgumentException
				("Simulation.doAt(): t = "+t+" less than simulation time ="+T+
				 ", illegal");
			}
		if (event == null)
			{
			throw new NullPointerException
				("Simulation.doAt(): event = null");
			}

		// Set event fields.
		event.sim = this;
		event.time = t;

		// Grow heap if necessary.
		if (N == heap.length)
			{
			Event[] newheap = new Event [N + 1024];
			System.arraycopy (heap, 0, newheap, 0, N);
			heap = newheap;
			}

		// Insert event into heap in min-priority order.
		heap[N] = event;
		siftUp (N);
		++ N;
		}

	/**
	 * Schedule the given event to be performed at a time <TT>dt</TT> in the
	 * future (at current simulation time + <TT>dt</TT>) in this simulation.
	 *
	 * @param  dt     Simulation time delta for <TT>event</TT>.
	 * @param  event  Event to be performed.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>dt</TT> is less than zero.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>event</TT> is null.
	 */
	public void doAfter
		(double dt,
		 Event event)
		{
		doAt (T + dt, event);
		}

	/**
	 * Run the simulation. At the start of the simulation, the simulation time
	 * is 0. The <TT>run()</TT> method returns when there are no more events.
	 */
	public void run()
		{
		while (N > 1)
			{
			// Extract minimum event from heap.
			Event event = heap[1];
			-- N;
			heap[1] = heap[N];
			heap[N] = null;
			if (N > 1) siftDown (1);

			// Advance simulation time and perform event.
			T = event.time;
			event.perform();
			}
		}

//Hidden operations.

	/**
	 * Sift up the heap entry at the given index.
	 *
	 * @param  c  Index.
	 */
	private void siftUp
		(int c)
		{
		double c_time = heap[c].time;
		int p = c >> 1;
		double p_time = heap[p].time;
		while (c_time < p_time)
			{
			Event temp = heap[c];
			heap[c] = heap[p];
			heap[p] = temp;
			c = p;
			p = c >> 1;
			p_time = heap[p].time;
			}
		}

	/**
	 * Sift down the heap entry at the given index.
	 *
	 * @param  p  Index.
	 */
	private void siftDown
		(int p)
		{
		double p_time = heap[p].time;
		int lc = (p << 1);
		double lc_time = lc < N ? heap[lc].time : Double.POSITIVE_INFINITY;
		int rc = (p << 1) + 1;
		double rc_time = rc < N ? heap[rc].time : Double.POSITIVE_INFINITY;
		int c;
		double c_time;
		if (lc_time < rc_time)
			{
			c = lc;
			c_time = lc_time;
			}
		else
			{
			c = rc;
			c_time = rc_time;
			}
		while (c_time < p_time)
			{
			Event temp = heap[c];
			heap[c] = heap[p];
			heap[p] = temp;
			p = c;
			lc = (p << 1);
			lc_time = lc < N ? heap[lc].time : Double.POSITIVE_INFINITY;
			rc = (p << 1) + 1;
			rc_time = rc < N ? heap[rc].time : Double.POSITIVE_INFINITY;
			if (lc_time < rc_time)
				{
				c = lc;
				c_time = lc_time;
				}
			else
				{
				c = rc;
				c_time = rc_time;
				}
			}
		}

	}

abstract class Event
	{

//Hidden data members.

	// Simulation in which this event occurs.
	Simulation sim;

	// Simulation time of this event.
	double time;

//Exported constructors.

	/**
	 * Construct a new event.
	 */
	public Event()
		{
		}

//Exported operations.

	/**
	 * Returns the simulation in which this event occurs.
	 *
	 * @return  Simulation.
	 */
	public final Simulation simulation()
		{
		return sim;
		}

	/**
	 * Returns this event's simulation time, the time when this event is
	 * scheduled to take place.
	 *
	 * @return  Simulation time.
	 */
	public final double time()
		{
		return time;
		}

	/**
	 * Schedule the given event to be performed at the given time in this
	 * event's simulation.
	 *
	 * @param  t      Simulation time for <TT>event</TT>.
	 * @param  event  Event to be performed.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>t</TT> is less than the current
	 *     simulation time.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>event</TT> is null.
	 */
	public final void doAt
		(double t,
		 Event event)
		{
		sim.doAt (t, event);
		}

	/**
	 * Schedule the given event to be performed at a time <TT>dt</TT> in the
	 * future (at current simulation time + <TT>dt</TT>) in this event's
	 * simulation.
	 *
	 * @param  dt     Simulation time delta for <TT>event</TT>.
	 * @param  event  Event to be performed.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>dt</TT> is less than zero.
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>event</TT> is null.
	 */
	public final void doAfter
		(double dt,
		 Event event)
		{
		sim.doAfter (dt, event);
		}

	/**
	 * Perform this event. Called by the {@linkplain Simulation} when the
	 * simulation time equals the time when this event is scheduled to take
	 * place.
	 */
	public abstract void perform();

	}

