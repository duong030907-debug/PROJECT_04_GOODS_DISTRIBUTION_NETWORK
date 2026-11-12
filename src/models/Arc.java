package models;

/**
 * Mot cung van chuyen co huong.
 *
 * <p>Capacity la so nguyen khong am (dung cho Ford-Fulkerson), weight la so
 * thuc khong am (dung cho Dijkstra). Hai thuoc tinh nay doc lap voi nhau va
 * khong bao gio bi thay doi boi thuat toan.</p>
 */
public class Arc {

    private final int u;
    private final int v;
    private final int capacity;
    private final double weight;

    public Arc(int u, int v, int capacity, double weight) {
        if (u <= 0 || v <= 0) {
            throw new IllegalArgumentException("Chi so dinh phai > 0");
        }
        if (u == v) {
            throw new IllegalArgumentException("Khong cho phep self-loop");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity phai la so nguyen >= 0");
        }
        if (weight < 0 || !Double.isFinite(weight)) {
            throw new IllegalArgumentException("Weight phai la so thuc huu han >= 0");
        }
        this.u = u;
        this.v = v;
        this.capacity = capacity;
        this.weight = weight;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getWeight() {
        return weight;
    }

    /** True neu cung nay khong the truyen hang (capacity = 0). */
    public boolean isUnavailable() {
        return capacity == 0;
    }

    @Override
    public String toString() {
        return "(" + u + " -> " + v + ", c=" + capacity + ", w=" + weight + ")";
    }
}
