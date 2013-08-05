package me.dreilopz.swebserv.impl;

public class UniformPrng implements me.dreilopz.swebserv.UniformPrng {
	private long seed;
	private transient long p0, p1, p2, p3, p4, p5, p6, p7;
	private transient long p8, p9, pa, pb, pc, pd, pe, pf;
	private static double D_2_POW_NEG_64;
	
	static {
	double x = 2.0;
	x *= x; // 2^2
	x *= x; // 2^4
	x *= x; // 2^8
	x *= x; // 2^16
	x *= x; // 2^32
	x *= x; // 2^64
	D_2_POW_NEG_64 = 1.0 / x;
	}

	@Override
	public long getSeed() {
		return 0;
	}

	public void setSeed
		(long seed)
		{
		this.seed = hash (seed);
		}

	public double rand()
		{
		++ seed;
		//return hash (seed);
		return (double) (hash (seed)) * D_2_POW_NEG_64 + 0.5;
		}

	private static long hash
		(long x)
		{
		x = 3935559000370003845L * x + 2691343689449507681L;
		x = x ^ (x >>> 21);
		x = x ^ (x << 37);
		x = x ^ (x >>> 4);
		x = 4768777513237032717L * x;
		x = x ^ (x << 20);
		x = x ^ (x >>> 41);
		x = x ^ (x << 5);
		return x;
		}

	}
