/**
 * 
 */
package me.dreilopz.swebserv.impl;

import me.dreilopz.swebserv.ExponentialPrng;
import me.dreilopz.swebserv.UniformPrng;

class PJExponentialPrng implements ExponentialPrng {
	private double mean;
	private UniformPrng uniformPrng;
	
	private edu.rit.numeric.ExponentialPrng expPrng;
	
	/**
	 * @return the mean
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * @param mean the mean to set
	 */
	public void setMean(double mean) {
		this.mean = mean;
	}

	PJExponentialPrng() {
		
	}
	
	public void buildExpPrng() {
		this.expPrng = new edu.rit.numeric.ExponentialPrng(
				((PJUniformPrng)this.uniformPrng).getPrng(), this.mean);
	}
	
	public double rand() {
		return expPrng.next();
	}

	public void setUniformPrng(UniformPrng uniformPrng) {
		this.uniformPrng = uniformPrng;
		
	}
}
