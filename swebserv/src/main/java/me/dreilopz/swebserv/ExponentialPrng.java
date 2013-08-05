/**
 * 
 */
package me.dreilopz.swebserv;

/**
 * @author dreilopz0
 *
 */
public interface ExponentialPrng extends PseudoRandomNumberGenerator {
	void setMean(double lambda);
	void buildExpPrng();
	double getMean();
}
