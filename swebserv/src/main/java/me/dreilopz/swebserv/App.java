package me.dreilopz.swebserv;

import me.dreilopz.swebserv.impl.PJWebServer;
import me.dreilopz.swebserv.impl.PJWebServer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
	private static String[] clArgs;
	private static double lambda_L, lambda_U, lambda_D, mu;
	private static long Q, R, seed;
	private static UniformPrng uniformPrng;
	private static ExponentialPrng reqPrng;
	private static ExponentialPrng serverPrng;
	private static ApplicationContext appContext;
	
    public static void main( String[] args )
    {
    	clArgs = args;
    	configure();

//	   	Simulation sim = (Simulation)appContext.getBean("simulation");
	   	log("START of swebserv.App");
////        log(sim.toString());
////        log(sim.performSimulation());
//
//	   	
//	   	UniformPrng uniformPrng = (UniformPrng)appContext.getBean("uniformPrng");
//	   	ExponentialPrng expPrng = (ExponentialPrng)appContext.getBean("expPrng");
//	   	
////	   	for(int i = 0; i <= 9; i++) {
////	   		log(""+uniformPrng.rand());
////	   		log(""+expPrng.rand());
////	   	}
//	   	
//	   	ssWebServer.main(new String[] {"8", "10", "100", "142857"});
//	   	
//	   	WebServer webServer = (WebServer)appContext.getBean("webServer");
//	   	webServer.simulate();
	   	
	   	performDOSAttack();
	   	
    	log("END");
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
		//mu = Double.parseDouble (clArgs[3]);
		//Q = Integer.parseInt (clArgs[4]);
		//R = Integer.parseInt (clArgs[5]);
		//seed = Long.parseLong (clArgs[6]);
		
	   	appContext = new ClassPathXmlApplicationContext("spring/beans/0000.xml");
	   	uniformPrng = (UniformPrng)appContext.getBean("uniformPrng");

    }
    
    static void performDOSAttack() {
		double lambda;
		WebServer webServer;
		System.out.printf(
			"Mu = %.3f, Q = %d, R = %d, seed = %d%n", mu, Q, R, seed);
		System.out.printf ("        --Wait time---  Drop%n");
		System.out.printf ("Lambda  Mean    Stddev  Fraction%n");
		for(int r = 0; (lambda = lambda_L + lambda_D * r) <= lambda_U; r++) {
//		for(int r = 0; r <= 0; r++) {
//		   	serverPrng = (ExponentialPrng)appContext.getBean("serverPrng");
		   	reqPrng = (ExponentialPrng)appContext.getBean("reqPrng");
		   	reqPrng.setMean(10.0);
		   	reqPrng.buildExpPrng();
			webServer = (WebServer)appContext.getBean("webServer");
//			webServer.setUniformPrng(uniformPrng);
//			webServer.setServerPrng(serverPrng);
			webServer.setReqPrng(reqPrng);
//			webServer.setMaxReqQueueLength(Q);
//			webServer.setNReq(R);
			webServer.simulate();
			System.out.printf ("%-8.3f%-8.3f%-8.3f%-8.3f%n",
					lambda, webServer.getWaitTimeMean(),
					webServer.getWaitTimeStddev(), webServer.getDropRatio());
		}
		
    }
    
	private static void usage()
	{
		log("Usage: java WebServer02 <lambda_L> <lambda_U> "+
			 "<lambda_D> <mu> <Q> <R> <seed>");
		System.exit (1);
	}
}
