package me.dreilopz.swebserv;

import java.io.File;
import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.io.FileUtils;

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
	   	performDOSAttack();
    	//simulateWebServer(50.0);
    	//test();
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
	   	appContext = new ClassPathXmlApplicationContext("spring/beans/0000.xml");
	   	uniformPrng = (UniformPrng)appContext.getBean("uniformPrng");
	   	serverPrng = (ExponentialPrng)appContext.getBean("serverPrng");
    }
    
    private static void performDOSAttack() {
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
	   	WebServer webServer = (WebServer)appContext.getBean("webServer");
		webServer.setReqPrng(reqPrng);
		webServer.simulate();
		System.out.printf ("%-8.3f%-8.3f%-8.3f%-8.3f%n",
				meanReqRate, webServer.getWaitTimeMean(),
				webServer.getWaitTimeStddev(), webServer.getDropRatio());
    }
    
	private static void usage()
	{
		log("Usage: java WebServer02 <lambda_L> <lambda_U> "+
			 "<lambda_D> <mu> <Q> <R> <seed>");
		System.exit (1);
	}
	
	private static void test() {
		// Compare uniformPrngs
		edu.rit.util.Random uniformPrngOrig = edu.rit.util.Random.getInstance (142857);
		edu.rit.numeric.ExponentialPrng serverPrngOrig = 
				new edu.rit.numeric.ExponentialPrng(uniformPrngOrig, 10.0);
		edu.rit.numeric.ExponentialPrng reqPrngOrig = 
				new edu.rit.numeric.ExponentialPrng(uniformPrngOrig, 50.0);
		
		
	   	reqPrng = (ExponentialPrng)appContext.getBean("reqPrng");
	   	reqPrng.setMean(50.0);
	   	reqPrng.buildExpPrng();
		
		StringBuilder str = new StringBuilder();
		int i;
		for(i = 0; i < 100; i++) {
			str.append(reqPrngOrig.next());
			str.append(",");
			str.append(reqPrng.rand());
			str.append("\n");
		}
		try {
			FileUtils.writeStringToFile(new File("out.txt"), str.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
