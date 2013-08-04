/**
 * 
 */
package me.dreilopz.swebserv;

/**
 * @author dreilopz0
 *
 */
public interface ExponentialPrng {
	double rand();
	void setMean(double lambda);
	void buildExpPrng();
	double getMean();
}
