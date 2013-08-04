package me.dreilopz.swebserv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App 
{
	private static String[] clArgs;
	private static double x_L, x_U, x_D, meanServiceTime;
	private static long seed, maxRequestQueueLength, nRequests;
	private static UniformPrng uniformPrng;
	private static ExponentialPrng serverPrng;
	private static ApplicationContext appContext;
	
    public static void main( String[] args )
    {
    	clArgs = args;
    	configure();
	   	simulateDOSAttack();
    }
    
    private static void log(String msg) {
    	//msg = "[" + (new java.util.Date()).toString() + "] " + msg;
    	System.out.println(msg);
    }
    
    private static void configure() {
		if (clArgs.length != 7) usage();
		x_L = Double.parseDouble (clArgs[0]);
		x_U = Double.parseDouble (clArgs[1]);
		x_D = Double.parseDouble (clArgs[2]);
		meanServiceTime = Double.parseDouble(clArgs[3]);
		maxRequestQueueLength = Long.parseLong(clArgs[4]);
		nRequests = Long.parseLong(clArgs[5]);
		seed = Long.parseLong(clArgs[6]);
	   	appContext = 
	   			new ClassPathXmlApplicationContext("spring/beans/0000.xml");
	   	uniformPrng = (UniformPrng)appContext.getBean("uniformPrng");
	   	uniformPrng.setSeed(seed);
	   	serverPrng = (ExponentialPrng)appContext.getBean("serverPrng");
	   	serverPrng.setMean(meanServiceTime);
	   	serverPrng.buildExpPrng();
    }
    
    private static void simulateDOSAttack() {
		XYSeries meanWaitTimeVsX =
				new XYSeries("meanWaitTimeVsX", false, true);
		XYSeries dropFractionVsX =
				new XYSeries("dropFractionVsX", false, true);
		ArrayList<Double> simResults;
		System.out.printf(
			"Mu = %.3f, Q = %d, R = %d, seed = %d%n", meanServiceTime,
			maxRequestQueueLength, nRequests, seed);
		System.out.printf ("        --Wait time---  Drop%n");
		System.out.printf ("x  Mean    Stddev  Fraction%n");
		

		
		double x;
		for(int r = 0; (x = x_L + x_D * r) <= x_U; r++) {
			simResults = simulateWebServer(x);
			meanWaitTimeVsX.add(x, simResults.get(0), false);
			dropFractionVsX.add(x, simResults.get(1), false);
		}
		
		try {
			ChartUtilities.saveChartAsPNG(
				new File("meanWaitTimeVsx.png"), 
				ChartFactory.createXYLineChart("0", "1", "2",
					new XYSeriesCollection(meanWaitTimeVsX),
					PlotOrientation.VERTICAL, true, false, false),
				600, 600);
			ChartUtilities.saveChartAsPNG(
					new File("dropFractionVsx.png"), 
					ChartFactory.createXYLineChart("0", "1", "2",
						new XYSeriesCollection(dropFractionVsX),
						PlotOrientation.VERTICAL, true, false, false),
					600, 600);
		} catch (IOException e) {
			e.printStackTrace();
		}

    }
    
    private static ArrayList<Double> simulateWebServer(double meanReqTime) {
    	ArrayList<Double> rt = new ArrayList<Double>();

	   	ExponentialPrng reqPrng = (ExponentialPrng)appContext.getBean("reqPrng");
	   	reqPrng.setMean(meanReqTime);
	   	reqPrng.buildExpPrng();	   	
	   	Request reqFactory = (Request)appContext.getBean("reqFactory");
		reqFactory.setReqPrng(reqPrng);
	   	WebServer webServer = (WebServer)appContext.getBean("webServer");
		webServer.setMaxReqQueueLength(maxRequestQueueLength);
		webServer.setNReq(nRequests);
		webServer.setServerPrng(serverPrng);  	
		webServer.setReqFactory(reqFactory);
		webServer.simulate();
		
		rt.add(webServer.getMeanWaitTime());
		rt.add(webServer.getDropRatio());
		System.out.printf ("%-8.3f%-8.3f%-8.3f%-8.3f%n",
				meanReqTime, webServer.getMeanWaitTime(),
				webServer.getWaitTimeStddev(), webServer.getDropRatio());
		return rt;
    }
    
	private static void usage()
	{
		log("Usage: java swebserv.App <meanArrivalTime_L> <meanArrivalTime_U> "+
			 "<meanArrivalTime_D> <meanServiceTime> " + 
				"<maxRequestQueueLenth> <nRequests>");
		System.exit (1);
	}
}
