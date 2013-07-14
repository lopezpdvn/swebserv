package me.dreilopz.swebserv;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
	   	ApplicationContext appContext = 
	       		new ClassPathXmlApplicationContext("spring/beans/0000.xml");

//	   	Simulation sim = (Simulation)appContext.getBean("simulation");
	   	log("START of swebserv.App");
//        log(sim.toString());
//        log(sim.performSimulation());

	   	
	   	UniformPrng uniformPrng = (UniformPrng)appContext.getBean("uniformPrng");
	   	ExponentialPrng expPrng = (ExponentialPrng)appContext.getBean("expPrng");
	   	
//	   	for(int i = 0; i <= 9; i++) {
//	   		log(""+uniformPrng.rand());
//	   		log(""+expPrng.rand());
//	   	}
	   	
	   	ssWebServer.main(new String[] {"8", "10", "100", "142857"});
	   	
	   	WebServer webServer = (WebServer)appContext.getBean("webServer");
	   	webServer.simulate();
	   	
    	log("END");
    }
    
    static void log(String msg) {
    	msg = "[" + (new java.util.Date()).toString() + "] " + msg;
    	System.out.println(msg);
    }
}
