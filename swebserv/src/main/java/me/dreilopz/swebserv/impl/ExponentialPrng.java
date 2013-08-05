package me.dreilopz.swebserv.impl;

import me.dreilopz.swebserv.UniformPrng;

public class ExponentialPrng implements me.dreilopz.swebserv.ExponentialPrng {
	private double mean;

	private UniformPrng uniformPrng;

	@Override
	public double rand() {
		return -Math.log(this.uniformPrng.rand())/this.mean;
	}

	@Override
	public void setMean(double mean) {
		this.mean = mean;
		
	}

	@Override
	public void buildExpPrng() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getMean() {
		// TODO Auto-generated method stub
		return this.mean;
	}
	
	public void setUniformPrng(UniformPrng uniformPrng) {
		this.uniformPrng = uniformPrng;
	}
}
