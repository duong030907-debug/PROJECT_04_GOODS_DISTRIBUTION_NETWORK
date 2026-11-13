package models;

import java.util.ArrayList;
import java.util.List;

public class Network {
	record Edge(String u , String v, int weight, int capacity) {
		
	}
	private static List<Edge> graph = new ArrayList<Edge>();
	private static List<String> nodes =  new ArrayList<String>();

	public List<String> getAdj(String u){
		List<String> l = new ArrayList<String>();
		for(Edge edge : graph) {
			if(edge.u == u) {
				l.add(edge.v);
			}
			if(edge.v == u) {
				l.add(edge.u);
			}
		}
		return l;
	}
	
	public List<String> getNodes(){
		return nodes;
	}
	
	public static boolean addNode(String u){
		if(!nodes.contains(u)) {
			nodes.add(u);
			return true;
		}
		return false;
	}
	
	public static boolean addEdge(String u , String v, int weight, int capacity){
		if(weight >0 && capacity >=0) {
			graph.add(new Edge(u, v, weight, capacity));
			addNode(u);
			addNode(v);
			return true;
		}
		System.out.println("weight must be > 0 and capacity  must be >= 0");
		return false;
	}
	
	public static boolean removeEdge(String u , String v){
		boolean b = false;
		for(Edge edge: graph) {
			if(edge.u == u && edge.v == v) {
				graph.remove(edge);
				b = true;
			}
		}
		return b;
	}
	
}