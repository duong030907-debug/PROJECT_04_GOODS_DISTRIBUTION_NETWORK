package models;

public class Edge {
	public String u;
	public String v;
	public int weight;
	public int capacity;
	private int flow;

	public Edge(String u, String v, int capacity, int weight) {
		this.u = u;
		this.v = v;
		this.capacity = capacity;
		this.weight = weight;
		this.flow = 0;
	}

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

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getFlow() {
		return flow;
	}

	public void setFlow(int flow) {
		this.flow = flow;
	}

	@Override
	public String toString() {
		return String.format("(%s -> %s) [Cap: %d, Weight: %.1f, Flow: %d]", u, v, capacity, weight, flow);
	}
}
