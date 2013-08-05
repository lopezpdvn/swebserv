package me.dreilopz.swebserv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

public class App 
{
	private static String[] clArgs;
	private static double x_L, x_U, x_D, meanServiceTime;
	private static long seed, maxRequestQueueLength, nRequests;
	private static UniformPrng uniformPrng;
	private static ExponentialPrng serverPrng;
	private static ApplicationContext appContext;
	private static String outDirPath;
	private static File outDir;
	private static final String resultsDirName = "results";
	private static File resultsDir;
	
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
		if (clArgs.length != 8) usage();
		x_L = Double.parseDouble (clArgs[0]);
		x_U = Double.parseDouble (clArgs[1]);
		x_D = Double.parseDouble (clArgs[2]);
		meanServiceTime = Double.parseDouble(clArgs[3]);
		maxRequestQueueLength = Long.parseLong(clArgs[4]);
		nRequests = Long.parseLong(clArgs[5]);
		seed = Long.parseLong(clArgs[6]);
		outDirPath = clArgs[7];
	   	appContext = 
	   			new ClassPathXmlApplicationContext("spring/beans/0000.xml");
	   	uniformPrng = (UniformPrng)appContext.getBean("uniformPrng");
	   	uniformPrng.setSeed(seed);
	   	serverPrng = (ExponentialPrng)appContext.getBean("serverPrng");
	   	serverPrng.setMean(meanServiceTime);
	   	serverPrng.buildExpPrng();
	   	
	   	outDir = new File(outDirPath);
	   	if(! (outDir.exists() && outDir.canExecute() &&
	   			outDir.canRead() && outDir.canWrite() )) {
	   		log("Invalid path!");
	   		System.exit(1);
	   	}
	   	resultsDir = new File(outDir, resultsDirName);
	   	try {
			FileUtils.deleteDirectory(resultsDir);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	   	try {
			FileUtils.forceMkdir(resultsDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private static void simulateDOSAttack() {
		XYSeries meanWaitTimeVsX =
				new XYSeries("meanWaitTimeVsX", false, true);
		XYSeries dropFractionVsX =
				new XYSeries("dropFractionVsX", false, true);
		XYSeries waitTimeStddevVsX =
				new XYSeries("waitTimeStddevVsX", false, true);
		ArrayList<Double> simResults;
		
		double x;
		for(int r = 0; (x = x_L + x_D * r) <= x_U; r++) {
			simResults = simulateWebServer(x);
			meanWaitTimeVsX.add(x, simResults.get(0), false);
			dropFractionVsX.add(x, simResults.get(1), false);
			waitTimeStddevVsX.add(x, simResults.get(2), false);
		}

		new Report(resultsDir, meanWaitTimeVsX, dropFractionVsX,
				waitTimeStddevVsX, meanServiceTime, maxRequestQueueLength,
				nRequests, seed);
		log("Results in " + resultsDir.getAbsolutePath());
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
		rt.add(webServer.getWaitTimeStddev());
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

class Report {
	private File reportDir;
	private File report;
	private File XSLT = new File("src/main/resources/report.xsl");
	private File meanWaitTimeVsXPlot, dropFractionVsXPlot;
	
	Report(File reportDir, XYSeries meanWaitTimeVsXSeries,
			XYSeries dropFractionVsXSeries, XYSeries waitTimeStddevVsX,
			double meanReqServiceTime, long maxReqQueueLength, long nReq,
			long PRNGseed) {
		this.reportDir = reportDir;
		this.report = new File(this.reportDir, "report.xml");
		this.meanWaitTimeVsXPlot = 
				new File(this.reportDir, "meanWaitTimeVsX.png");
		this.dropFractionVsXPlot = 
				new File(this.reportDir, "dropFractionVsX.png");
		
		try {
			ChartUtilities.saveChartAsPNG(
				this.meanWaitTimeVsXPlot, 
				ChartFactory.createXYLineChart(
						"Mean wait time VS Mean arrival time",
						"mean request arrival time", "mean wait time",
					new XYSeriesCollection(meanWaitTimeVsXSeries),
					PlotOrientation.VERTICAL, false, false, false),
				900, 600);
			ChartUtilities.saveChartAsPNG(
					this.dropFractionVsXPlot, 
					ChartFactory.createXYLineChart(
							"Dropped request fraction VS Mean arrival time",
							"mean request arrival time", "dropped request fraction",
						new XYSeriesCollection(dropFractionVsXSeries),
						PlotOrientation.VERTICAL, false, false, false),
					900, 600);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileUtils.copyFile(this.XSLT, 
					new File(this.reportDir, this.XSLT.getName()));
			
			DocumentBuilderFactory docFactory = 
					DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
			Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			ProcessingInstruction p = doc.createProcessingInstruction("xml-stylesheet", 
					"type=\"text/xsl\" href=\"" + this.XSLT.getName() + "\"");
			Element rootElement = doc.createElement("swebserv");
			doc.appendChild(rootElement);
			doc.insertBefore(p, rootElement);
			
			Element date = doc.createElement("date");
			date.appendChild(doc.createTextNode(new Date().toString()));
			rootElement.appendChild(date);
			
			Element meanReqServiceTimeE = doc.createElement("meanReqServiceTime");
			meanReqServiceTimeE.appendChild(doc.createTextNode(
					String.valueOf(meanReqServiceTime)));
			rootElement.appendChild(meanReqServiceTimeE);
			Element maxReqQueueLengthE = doc.createElement("maxReqQueueLength");
			maxReqQueueLengthE.appendChild(doc.createTextNode(
					String.valueOf(maxReqQueueLength)));
			rootElement.appendChild(maxReqQueueLengthE);
			Element nReqE = doc.createElement("nReq");
			nReqE.appendChild(doc.createTextNode(
					String.valueOf(nReq)));
			rootElement.appendChild(nReqE);
			Element PRNGseedE = doc.createElement("PRNGseed");
			PRNGseedE.appendChild(doc.createTextNode(
					String.valueOf(PRNGseed)));
			rootElement.appendChild(PRNGseedE);
			
			Element meanWaitTimeVsX = doc.createElement("meanWaitTimeVsX");
			meanWaitTimeVsX.appendChild(doc.createTextNode(
					this.meanWaitTimeVsXPlot.getAbsolutePath()));
			rootElement.appendChild(meanWaitTimeVsX);
			Element dropFractionVsX = doc.createElement("dropFractionVsX");
			dropFractionVsX.appendChild(doc.createTextNode(
					this.dropFractionVsXPlot.getAbsolutePath()));
			rootElement.appendChild(dropFractionVsX);
			
			Element simulations = doc.createElement("simulations");
			Element simulation;
			int nSim = meanWaitTimeVsXSeries.getItemCount();
			for(int i = 0; i < nSim; i++) {
				simulation = doc.createElement("simulation");
				simulation.setAttribute("meanReqArrivalTime",
						meanWaitTimeVsXSeries.getX(i).toString());
				simulation.setAttribute("meanWaitTime", 
						meanWaitTimeVsXSeries.getY(i).toString());
				simulation.setAttribute("waitTimeStddev", 
						waitTimeStddevVsX.getY(i).toString());
				simulation.setAttribute("reqDropFraction", 
						dropFractionVsXSeries.getY(i).toString());
				simulations.appendChild(simulation);
			}

			rootElement.appendChild(simulations);
 
			TransformerFactory transformerFactory = 
					TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			   transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(report);
 
			transformer.transform(source, result);

 
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  } catch (IOException e) {
			e.printStackTrace();
		}
	}
}

