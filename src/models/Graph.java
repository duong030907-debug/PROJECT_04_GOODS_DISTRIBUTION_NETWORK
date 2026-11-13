package models;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private final int n;

    private int m = 0;

    private final boolean directed;

    private final int[][] matrix;

    private final List<List<Integer>> adj;

    Graph(int n, boolean directed) {
        this.directed = directed;
        if (n <= 0) {
            throw new IllegalArgumentException("n phai lon hon 0");
        } else {
            this.n = n;
            this.matrix = new int[n + 1][n + 1];
            this.adj = new ArrayList<>(n + 1);
            for (int i = 0; i < n + 1; i++) {
                adj.add(new ArrayList<>());
            }
        }
    }

 /*   !Mỗi danh sách adj.get(u) phải luôn được giữ theo thứ tự tăng dần, kể cả sau khi thêm/xóa cạnh hoặc cung.

    !Biến m phải phản ánh đúng số cạnh/cung hiện tại. Mỗi lần thêm/xóa thành công một cạnh hoặc một cung, m chỉ tăng/giảm 1; với đồ thị vô hướng không tăng/giảm m hai lần dù cập nhật hai chiều.*/
//region addEdge
    boolean addEdge(int u, int v) {
        if (u <= 0 || v <= 0 || u == v || u > n || v > n) {
            return false;
        }
        m++;
        if (directed) {
            addEdge(this.matrix, u, v);
            addEdge(this.adj, u, v);
        }else {
            addEdge(this.matrix, u, v);
            addEdge(this.adj, u, v);
            addEdge(this.matrix, v, u);
            addEdge(this.adj, v, u);
        }
        return true;
    }


    private void addEdge(List<List<Integer>> adj, int u, int v) {
            adj.get(u).add(v);
            quickSortList(adj.get(u), 0, adj.get(u).size() - 1);
    }

    private void quickSortList(List<Integer> adj, int low, int high) {
        if (low < high) {
            // Tim vi tri pivot
            int pivotIndex = partitionList(adj, low, high);

            // Sort recursive
            quickSortList(adj, low, pivotIndex - 1);   // Arr trai
            quickSortList(adj, pivotIndex + 1, high);  // Arr phai
        }

    }

    private static int partitionList(List<Integer> adj, int low, int high) {
        int pivot = adj.get(high);  // pivot high
        int i = low - 1;        // Index low

        for (int j = low; j < high; j++) {
            // If vi tri hien tai nho hon
            if (adj.get(j) <= pivot) {
                i++;
                // Swap
                swapList(adj, i, j);
            }
        }

        // Swapswap(
        swapList(adj, i + 1, high);

        return i + 1;
    }

    private static void swapList(List<Integer> adj, int i, int j) {
        if (adj.get(i) != 0) {
            int temp = adj.get(i);
            adj.set(i, adj.get(j));
            adj.set(j, temp);
        }
    }



    private void addEdge(int[][] matrix, int u, int v) {
        matrix[u][v] = 1;
    }

//endregion
//endregion
    //region removeEdge
    boolean removeEdge(int u, int v) {
        if (u <= 0 || v <= 0 ||
            matrix[u][v] == 0 || adj.get(u).get(v) == 0) {
            return false;
        }
        if (directed){
            removeEdgeMatrix(this.matrix,u,v);
            removeEdgeList(this.adj,u,v);
        }else{
            removeEdgeMatrix(this.matrix,u,v);
            removeEdgeList(this.adj,u,v);
            m--;
            removeEdgeMatrix(this.matrix,v,u);
            removeEdgeList(this.adj,v,u);
            m--;
        }
        return true;
    }

    private void removeEdgeList(List<List<Integer>> adj, int u, int v) {
        adj.get(u).remove(v);
    }

    private void removeEdgeMatrix(int[][] matrix, int u, int v) {
        matrix[u][v] = 0;
    }
//endregion
    boolean isAdjacent(int u, int v) {
        if (u <= 0 || v <= 0 || u == v || u > n || v > n) {
            throw new IllegalArgumentException("u va n phai lon hon 0 va nho hon n");
        }
        if (directed) {
            return isAdjacent(this.matrix,u,v) &&
                    isAdjacent(this.adj,u,v);
        } else {
            return (isAdjacent(this.matrix,u,v) &&
                    isAdjacent(this.adj,u,v))
                    &&(isAdjacent(this.matrix,v,u) &&
                    isAdjacent(this.adj,v,u));
        }
    }

    private boolean isAdjacent(List<List<Integer>> adj, int u, int v) {
        return adj.get(u).contains(v);
    }

    private boolean isAdjacent(int[][] matrix, int u, int v) {
        for (int i = 0; i < matrix[u].length; i++) {
            if (matrix[u][i] == 1) {
                return true;
            }
        }
        return false;
    }

    void printDegree() {
        if (directed) {
            System.out.printf("%-4s%-4s%-4s%4s", "Dinh", "degIn", "degOut", "deg");
            for (int i = 1; i < adj.size(); i++) {
                System.out.printf("%-4s%-4s%-4s%4s", i, inDegree(i), outDegree(i), degree(i));
            }
        } else {
            System.out.printf("%-4s%-4s", "Dinh", "deg");
            for (int i = 1; i < adj.size(); i++) {
                System.out.printf("%-4s%-4s", i, degree(i));
            }
        }
    }
    int degree(int v) {
        if (directed) {
            return adj.get(v).size() + inDegree(v);
        } else return adj.get(v).size();
    }       // đồ thị vô hướng

    int outDegree(int v) {
        return adj.get(v).size();
    }    // đồ thị có hướng

    int inDegree(int v) {
        int d = 0;
        for (List<Integer> integers : adj) {
            for (int j = 0; j < integers.size(); j++) {
                if (integers.contains(v)) {
                    d++;
                }
            }
        }
        return d;
    }

    void printMatrix() {
        int[][] matrix = this.matrix;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (i == 0) {
                    matrix[i][j] = j;
                }
                if (j == 0) {
                    matrix[i][j] = i;
                }
            }

        }
        for (int i = 0; i < matrix.length; i++) {
            if (i == 1) {
                System.out.println();
            }
            for (int j = 0; j < matrix[i].length; j++) {
                if (j == 0) {
                    System.out.printf("%-5s", matrix[i][j]);
                } else {
                    System.out.printf("%-2s", matrix[i][j]);
                }

            }
            System.out.println();
        }

    }

    void printAdjList() {
        System.out.printf("%-6s%-4s\n","Dinh","Ke");
        for (int k = 1; k < adj.size(); k++) {
            List<Integer> i = adj.get(k);
            System.out.printf("%-6s",k);
            for (int j : i) {
                System.out.printf("%-2s", j);
            }
            System.out.println();
        }
    }

    boolean checkConsistency() {
        if (!directed) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (matrix[i][j] == 1 && matrix[j][i] != matrix[i][j]) {
                        return false;
                    }
                }
            }
            for (int i = 1; i < adj.size(); i++) {
                for (int j = 0; i < adj.get(i).size(); i++) {
                    if (!adj.get(j).contains(adj.get(i).get(j)) && j != 0){
                        return false;
                    }
                }
            }
        }
        for (int i = 1; i < adj.size(); i++) {
            for (int j = 0; i < adj.get(i).size(); i++) {
                if (matrix[i][adj.get(i).get(j)] != 1) {
                    return false;
                }
            }
        }
        return true;
    }

}

