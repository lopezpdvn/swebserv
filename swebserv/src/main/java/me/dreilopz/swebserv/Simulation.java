package me.dreilopz.swebserv;

class Simulation {
	private String name;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Simulation [getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}
		
	public String performSimulation() {
		return "swebserv. Performing simulation";
	}

}
