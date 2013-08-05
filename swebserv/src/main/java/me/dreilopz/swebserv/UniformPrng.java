/**
 * 
 */
package me.dreilopz.swebserv;

/**
 * @author dreilopz0
 *
 */
public interface UniformPrng extends PseudoRandomNumberGenerator {
	long getSeed();
	void setSeed(long seed);
}
