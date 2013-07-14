/**
 * 
 */
package me.dreilopz.swebserv.impl;

import me.dreilopz.swebserv.ExponentialPrng;
import me.dreilopz.swebserv.UniformPrng;
import me.dreilopz.swebserv.SwebservException;
import edu.rit.util.Random;

/**
 * @author dreilopz0
 *
 */
public class PJExponentialPrng implements ExponentialPrng {
	private double mean;
	private PJUniformPrng uniformPrng;
	
	private edu.rit.numeric.ExponentialPrng pjExpPrng;
	
	/**
	 * @return the uniformPrng
	 */
	public PJUniformPrng getUniformPrng() {
		return uniformPrng;
	}

	/**
	 * @param uniformPrng the uniformPrng to set
	 */
	public void setUniformPrng(PJUniformPrng uniformPrng) {
		this.uniformPrng = uniformPrng;
	}
	
	/**
	 * @return the mean
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * @param mean the mean to set
	 */
	public void setMean(double mean) throws SwebservException {
		if (mean < 0) {
			throw new SwebservException();
		}
		this.mean = mean;

	}

	PJExponentialPrng() {
		
	}
	
	public void setPjExpPrng() {
		this.pjExpPrng = new edu.rit.numeric.ExponentialPrng(
				((PJUniformPrng)this.uniformPrng).getPrng(), this.mean);
	}
	
	public double rand() {
		return pjExpPrng.next();
	}
}
