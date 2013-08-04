package me.dreilopz.swebserv;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App 
{
	private static String[] clArgs;
	private static double lambda_L, lambda_U, lambda_D;
	private static UniformPrng uniformPrng;
	private static ExponentialPrng reqPrng;
	private static ExponentialPrng serverPrng;
	private static ApplicationContext appContext;
	
    public static void main( String[] args )
    {
    	clArgs = args;
    	configure();
	   	simulateDOSAttack();
    }
    
    private static void log(String msg) {
    	msg = "[" + (new java.util.Date()).toString() + "] " + msg;
    	System.out.println(msg);
    }
    
    private static void configure() {
		if (clArgs.length != 3) usage();
		lambda_L = Double.parseDouble (clArgs[0]);
		lambda_U = Double.parseDouble (clArgs[1]);
		lambda_D = Double.parseDouble (clArgs[2]);
	   	appContext = new ClassPathXmlApplicationContext("spring/beans/0000.xml");
	   	uniformPrng = (UniformPrng)appContext.getBean("uniformPrng");
	   	serverPrng = (ExponentialPrng)appContext.getBean("serverPrng");
    }
    
    private static void simulateDOSAttack() {
		double lambda;
		WebServer webServer = (WebServer)appContext.getBean("webServer");
		System.out.printf(
			"Mu = %.3f, Q = %d, R = %d, seed = %d%n", serverPrng.getMean(),
			webServer.getMaxReqQueueLength(), webServer.getNReq(),
			uniformPrng.getSeed());
		System.out.printf ("        --Wait time---  Drop%n");
		System.out.printf ("Lambda  Mean    Stddev  Fraction%n");
		for(int r = 0; (lambda = lambda_L + lambda_D * r) <= lambda_U; r++) {
			simulateWebServer(lambda);
		}
    }
    
    private static void simulateWebServer(double meanReqRate) {
	   	reqPrng = (ExponentialPrng)appContext.getBean("reqPrng");
	   	reqPrng.setMean(meanReqRate);
	   	reqPrng.buildExpPrng();
	   	Request reqFactory = (Request)appContext.getBean("reqFactory");
		reqFactory.setReqPrng(reqPrng);
	   	WebServer webServer = (WebServer)appContext.getBean("webServer");
		webServer.setReqFactory(reqFactory);
		webServer.simulate();
		System.out.printf ("%-8.3f%-8.3f%-8.3f%-8.3f%n",
				meanReqRate, webServer.getWaitTimeMean(),
				webServer.getWaitTimeStddev(), webServer.getDropRatio());
    }
    
	private static void usage()
	{
		log("Usage: java WebServer02 <lambda_L> <lambda_U> "+
			 "<lambda_D>");
		System.exit (1);
	}
}
