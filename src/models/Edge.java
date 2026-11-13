package models;

public class Edge {
	public String u ;
	public String v;
	public int weight;
	public String getU() {
		return u;
	}

	public void setU(String u) {
		this.u = u;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public double capacity;

	public Edge(String u, String v, int weight, double capacity) {
		super();
		this.u = u;
		this.v = v;
		this.weight = weight;
		this.capacity = capacity;
	}
}
