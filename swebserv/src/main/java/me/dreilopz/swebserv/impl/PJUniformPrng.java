/**
 * 
 */
package me.dreilopz.swebserv.impl;

import me.dreilopz.swebserv.UniformPrng;
import edu.rit.util.Random;

class PJUniformPrng implements UniformPrng {
	private long seed = 0L;
	private Random prng;
	
	/**
	 * @return the prng
	 */
	public Random getPrng() {
		return prng;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(long seed) {
		this.seed = seed;
		this.prng = Random.getInstance(this.seed);
	}

	PJUniformPrng() {
	}
	
	public double rand() {
		return prng.nextDouble();
	}
	
	public long getSeed() {
		return seed;
	}
}
