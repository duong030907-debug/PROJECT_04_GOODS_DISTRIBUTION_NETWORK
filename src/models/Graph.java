package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graph {

    private final int n;
    private int m = 0;
    private final boolean directed;
    private final int[][] matrix;
    private final List<List<Integer>> adj;

    public Graph(int n, boolean directed) {
        this.directed = directed;
        if (n <= 0) {
            throw new IllegalArgumentException("n phai lon hon 0");
        } else {
            this.n = n;
            this.matrix = new int[n + 1][n + 1];
            this.adj = new ArrayList<>(n + 1);
            for (int i = 0; i <= n; i++) {
                adj.add(new ArrayList<>());
            }
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

    //region addEdge
    public boolean addEdge(int u, int v) {
        if (u <= 0 || v <= 0 || u > n || v > n) {
            return false;
        }
        if (isAdjacent(u, v)) {
            return false;
        }

        if (directed) {
            matrix[u][v] = 1;
            addToListSorted(adj.get(u), v);
        } else {
            if (u == v) { 
                if (matrix[u][v] == 1) return false;
                matrix[u][v] = 1;
                addToListSorted(adj.get(u), v);
            } else {
                matrix[u][v] = 1;
                matrix[v][u] = 1;
                addToListSorted(adj.get(u), v);
                addToListSorted(adj.get(v), u);
            }
        }
        m++;
        return true;
    }

    private void addToListSorted(List<Integer> list, int v) {
        if (!list.contains(v)) {
            list.add(v);
            Collections.sort(list);
        }
    }
    //endregion

    //region removeEdge
    public boolean removeEdge(int u, int v) {
        if (u <= 0 || v <= 0 || u > n || v > n || matrix[u][v] == 0) {
            return false;
        }

        if (directed) {
            matrix[u][v] = 0;
            adj.get(u).remove(Integer.valueOf(v));
        } else {
            matrix[u][v] = 0;
            matrix[v][u] = 0;
            adj.get(u).remove(Integer.valueOf(v));
            adj.get(v).remove(Integer.valueOf(u));
        }
        m--;
        return true;
    }
    //endregion

    public boolean isAdjacent(int u, int v) {
        if (u <= 0 || v <= 0 || u > n || v > n) {
            throw new IllegalArgumentException("u va v phai trong khoảng [1, n]");
        }
        return matrix[u][v] == 1 && adj.get(u).contains(v);
    }

    public void printDegree() {
        if (directed) {
            System.out.printf("%-6s%-6s%-6s%-6s\n", "Dinh", "degIn", "degOut", "deg");
            for (int i = 1; i <= n; i++) {
                System.out.printf("%-6d%-6d%-6d%-6d\n", i, inDegree(i), outDegree(i), degree(i));
            }
        } else {
            System.out.printf("%-6s%-6s\n", "Dinh", "deg");
            for (int i = 1; i <= n; i++) {
                System.out.printf("%-6d%-6d\n", i, degree(i));
            }
        }
    }

    public int degree(int v) {
        if (v <= 0 || v > n) return 0;
        if (directed) {
            return outDegree(v) + inDegree(v);
        } else {
            return adj.get(v).size();
        }
    }

    public int outDegree(int v) {
        if (v <= 0 || v > n) return 0;
        return adj.get(v).size();
    }

    public int inDegree(int v) {
        if (v <= 0 || v > n) return 0;
        int d = 0;
        for (int i = 1; i <= n; i++) {
            if (matrix[i][v] == 1) {
                d++;
            }
        }
        return d;
    }

    public void printMatrix() {
        System.out.printf("%-4s", " ");
        for (int j = 1; j <= n; j++) {
            System.out.printf("%-3d", j);
        }
        System.out.println();

        // Print rows
        for (int i = 1; i <= n; i++) {
            System.out.printf("%-4d", i);
            for (int j = 1; j <= n; j++) {
                System.out.printf("%-3d", matrix[i][j]);
            }
            System.out.println();
        }
    }

    public void printAdjList() {
        System.out.printf("%-6s%-6s\n", "Dinh", "Ke");
        for (int k = 1; k <= n; k++) {
            List<Integer> neighbors = adj.get(k);
            System.out.printf("%-6d", k);
            for (int j : neighbors) {
                System.out.printf("%-3d", j);
            }
            System.out.println();
        }
    }

    public boolean checkConsistency() {
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (!directed && matrix[i][j] != matrix[j][i]) {
                    return false;
                }
                boolean inList = adj.get(i).contains(j);
                boolean inMatrix = (matrix[i][j] == 1);
                if (inMatrix != inList) {
                    return false;
                }
            }
        }
        return true;
    }
}