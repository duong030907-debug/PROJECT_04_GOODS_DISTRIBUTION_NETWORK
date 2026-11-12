package models;

import java.util.List;

/**
 * Mang du (residual graph) cua mot lan chay Ford-Fulkerson.
 *
 * <p>Moi doi tuong ResidualGraph duoc tao moi tu capacity cua {@link Graph}.
 * No luu residual capacity (ca cung thuan lan cung nguoc) va flow tren cac
 * cung vat ly. Do thi goc khong bi thay doi.</p>
 */
public class ResidualGraph {

    private final int n;
    private final Graph originalGraph;
    private final int[][] residualCap;
    private final int[][] flow;

    public ResidualGraph(Graph originalGraph) {
        if (originalGraph == null) {
            throw new IllegalArgumentException("Do thi goc khong duoc null");
        }
        this.originalGraph = originalGraph;
        this.n = originalGraph.getN();
        this.residualCap = new int[n + 1][n + 1];
        this.flow = new int[n + 1][n + 1];

        for (Arc arc : originalGraph.getAllArcs()) {
            if (arc.getCapacity() > 0) {
                residualCap[arc.getU()][arc.getV()] = arc.getCapacity();
            }
        }
    }

    public int getN() {
        return n;
    }

    public Graph getOriginalGraph() {
        return originalGraph;
    }

    public int getResidualCapacity(int u, int v) {
        if (u <= 0 || v <= 0 || u > n || v > n) {
            return 0;
        }
        return residualCap[u][v];
    }

    public int getFlow(int u, int v) {
        if (u <= 0 || v <= 0 || u > n || v > n) {
            return 0;
        }
        return flow[u][v];
    }

    /**
     * Tang flow doc theo mot duong tang.
     *
     * <p>Voi moi buoc u -> v: residualCap[u][v] giam bottleneck va
     * residualCap[v][u] tang bottleneck. Neu (u,v) la cung vat ly thi flow
     * tang; neu (u,v) chi la cung nguoc du (nghia la (v,u) la cung vat ly)
     * thi flow tren (v,u) giam.</p>
     */
    public void augmentPath(List<Integer> path, int bottleneck) {
        if (path == null || path.size() < 2 || bottleneck <= 0) {
            return;
        }
        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
            int v = path.get(i + 1);
            residualCap[u][v] -= bottleneck;
            residualCap[v][u] += bottleneck;

            if (originalGraph.hasArc(u, v)) {
                flow[u][v] += bottleneck;
            } else if (originalGraph.hasArc(v, u)) {
                flow[v][u] -= bottleneck;
            }
        }
    }

    /** True neu (u,v) khong phai cung vat ly nhung (v,u) thi co. */
    public boolean isResidualReverseArc(int u, int v) {
        return !originalGraph.hasArc(u, v) && originalGraph.hasArc(v, u)
                && residualCap[u][v] > 0;
    }

    /**
     * Kiem chung flow: 0 <= f(u,v) <= c(u,v), khong co flow tren cung khong
     * ton tai, bao toan luong tai cac dinh trung gian.
     */
    public boolean verifyFlow(int source, int sink) {
        for (int u = 1; u <= n; u++) {
            for (int v = 1; v <= n; v++) {
                int f = flow[u][v];
                if (originalGraph.hasArc(u, v)) {
                    int c = originalGraph.getCapacity(u, v);
                    if (f < 0 || f > c) {
                        return false;
                    }
                } else if (f != 0) {
                    return false;
                }
            }
        }
        for (int u = 1; u <= n; u++) {
            if (u == source || u == sink) {
                continue;
            }
            int inflow = 0;
            int outflow = 0;
            for (int i = 1; i <= n; i++) {
                inflow += flow[i][u];
                outflow += flow[u][i];
            }
            if (inflow != outflow) {
                return false;
            }
        }
        return getSourceOutflow(source) == getSinkInflow(sink);
    }

    public int getSourceOutflow(int source) {
        int outflow = 0;
        for (int v = 1; v <= n; v++) {
            if (originalGraph.hasArc(source, v)) {
                outflow += flow[source][v];
            }
        }
        return outflow;
    }

    public int getSinkInflow(int sink) {
        int inflow = 0;
        for (int u = 1; u <= n; u++) {
            if (originalGraph.hasArc(u, sink)) {
                inflow += flow[u][sink];
            }
        }
        return inflow;
    }
}
