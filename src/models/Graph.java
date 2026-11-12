package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Do thi co huong don: moi cap dinh co toi da mot cung theo mot chieu.
 *
 * <p>Do thi goc chi luu capacity va weight. No khong bao gio bi thay doi boi
 * Ford-Fulkerson; moi trang thai flow/residual nam trong {@link ResidualGraph}.</p>
 */
public class Graph {

    private final int n;
    private final boolean directed;
    private final int[][] capMatrix;
    private final double[][] weightMatrix;
    private final boolean[][] present;
    private final List<List<Arc>> adjArcs;
    private final List<Arc> allArcs;
    private int m;
    private int zeroCapacityCount;

    public Graph(int n, boolean directed) {
        if (n <= 0) {
            throw new IllegalArgumentException("So dinh phai > 0");
        }
        this.n = n;
        this.directed = directed;
        this.capMatrix = new int[n + 1][n + 1];
        this.weightMatrix = new double[n + 1][n + 1];
        this.present = new boolean[n + 1][n + 1];
        this.adjArcs = new ArrayList<>(n + 1);
        this.allArcs = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            adjArcs.add(new ArrayList<>());
        }
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public boolean isDirected() {
        return directed;
    }

    public int getCapacity(int u, int v) {
        if (!valid(u) || !valid(v)) {
            return 0;
        }
        return capMatrix[u][v];
    }

    public double getWeight(int u, int v) {
        if (!valid(u) || !valid(v) || !present[u][v]) {
            return -1.0;
        }
        return weightMatrix[u][v];
    }

    /** Cung vat ly (u,v) co ton tai trong do thi goc hay khong. */
    public boolean hasArc(int u, int v) {
        return valid(u) && valid(v) && present[u][v];
    }

    public Arc getArc(int u, int v) {
        if (!hasArc(u, v)) {
            return null;
        }
        for (Arc arc : adjArcs.get(u)) {
            if (arc.getV() == v) {
                return arc;
            }
        }
        return null;
    }

    public boolean addArc(int u, int v, int capacity, double weight) {
        if (!valid(u) || !valid(v) || u == v) {
            return false;
        }
        if (present[u][v]) {
            return false; // khong cho cung trung
        }
        if (capacity < 0 || weight < 0 || !Double.isFinite(weight)) {
            return false;
        }

        capMatrix[u][v] = capacity;
        weightMatrix[u][v] = weight;
        present[u][v] = true;

        Arc arc = new Arc(u, v, capacity, weight);
        adjArcs.get(u).add(arc);
        allArcs.add(arc);
        m++;
        if (capacity == 0) {
            zeroCapacityCount++;
        }

        if (!directed) {
            capMatrix[v][u] = capacity;
            weightMatrix[v][u] = weight;
            present[v][u] = true;
            Arc reverse = new Arc(v, u, capacity, weight);
            adjArcs.get(v).add(reverse);
            allArcs.add(reverse);
        }
        return true;
    }

    public List<Arc> getOutgoingArcs(int u) {
        if (!valid(u)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(adjArcs.get(u));
    }

    public List<Arc> getAllArcs() {
        return Collections.unmodifiableList(allArcs);
    }

    public int getZeroCapacityCount() {
        return zeroCapacityCount;
    }

    public int outDegree(int u) {
        return valid(u) ? adjArcs.get(u).size() : 0;
    }

    private boolean valid(int u) {
        return u > 0 && u <= n;
    }

    public void printGraphSummary() {
        System.out.println("Directed Graph | V = " + n + " | E = " + m
                + " | Zero-capacity arcs = " + zeroCapacityCount);
    }
}
