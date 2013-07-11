package me.dreilopz.swebserv;

import edu.rit.numeric.ExponentialPrng;
import edu.rit.sim.Event;
import edu.rit.sim.Simulation;
import edu.rit.util.Random;

import java.util.Iterator;
import java.util.LinkedList;
public class ssWebServer
	{
	private static double lambda;
	private static double mu;
	private static int R;
	private static long seed;
	private static Random prng;
	private static ExponentialPrng requestPrng;
	private static ExponentialPrng serverPrng;
	private static LinkedList<Request> requestQueue;
	private static int requestCount;
	private static Simulation sim;

	public static void main
		(String[] args)
		{
		if (args.length != 4) usage();
		lambda = Double.parseDouble (args[0]);
		mu = Double.parseDouble (args[1]);
		R = Integer.parseInt (args[2]);
		seed = Long.parseLong (args[3]);
		prng = Random.getInstance (seed);
		requestPrng = new ExponentialPrng (prng, lambda);
		serverPrng = new ExponentialPrng (prng, mu);
		requestQueue = new LinkedList<Request>();
		requestCount = 0;
		sim = new Simulation();
		generateRequest();
		sim.run();
	}

	private static class Request
		{
		private int requestNumber;
		private int reqType;
		private static int i = 0;
		public Request
			(int requestNumber)
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
	

	private static void generateRequest()
		{
		addToQueue (new Request (++ requestCount));
		if (requestCount < R)
			{
			sim.doAfter (requestPrng.next(), new Event()
				{
				public void perform() { generateRequest(); }
				});
			}
		}

	private static void addToQueue
		(Request request)
		{
		System.out.printf ("%.3f %s added to queue%n",
			sim.time(), request);
		requestQueue.add (request);
		if (requestQueue.size() == 1) startServing();
		}

	private static void startServing()
		{
		System.out.printf ("%.3f Started serving %s%n",
			sim.time(), requestQueue.getFirst());
		sim.doAfter (serverPrng.next(), new Event()
			{
			public void perform() { removeFromQueue(); }
			});
		}

	private static void removeFromQueue()
		{
		System.out.printf ("%.3f %s removed from queue%n",
			sim.time(), requestQueue.removeFirst());
		if (requestQueue.size() > 0) startServing();
		}

	private static void usage()
		{
		System.err.println
			("Usage: java WebServer01 <lambda> <mu> <R> <seed>");
		System.exit (1);
		}
	}
