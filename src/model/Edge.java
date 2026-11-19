package model;

public class Edge {
	public String u; // Đỉnh đầu
	public String v; // Đỉnh cuối
	public int capacity; // Sức chứa tối đa
	public int weight; // Trọng số
	private int flow; // Luồng

	public Edge(String u, String v, int capacity, int weight) {
		super();
		this.u = u;
		this.v = v;
		this.capacity = capacity;
		this.weight = weight;
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

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getFlow() {
		return flow;
	}

	public void setFlow(int flow) {
		this.flow = flow;
	}

	@Override
	public String toString() {
		return u + " -> " + v + " : cap=" + capacity + ", weight=" + weight + ", flow=" + flow;
	}

}