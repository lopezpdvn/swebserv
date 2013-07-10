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
	   	Simulation sim = (Simulation)appContext.getBean("simulation");
	   	log("START of swebserv.App");
        log(sim.toString());
        log(sim.performSimulation());
    	log("END");

    }
    
    static void log(String msg) {
    	msg = "[" + (new java.util.Date()).toString() + "] " + msg;
    	System.out.println(msg);
    }
}
