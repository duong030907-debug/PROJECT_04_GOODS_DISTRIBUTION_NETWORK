package models;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dataset da nap: do thi goc + anh xa ten dinh <-> chi so + Source/Sink.
 */
public class Network {

    private final String datasetName;
    private final String fileName;
    private final Graph graph;
    private final List<String> vertices;
    private final Map<String, Integer> ids;
    private final int sourceId;
    private final int sinkId;

    public Network(String datasetName, String fileName, Graph graph,
            List<String> vertices, Map<String, Integer> ids,
            int sourceId, int sinkId) {
        this.datasetName = datasetName;
        this.fileName = fileName;
        this.graph = graph;
        this.vertices = List.copyOf(vertices);
        this.ids = Collections.unmodifiableMap(new LinkedHashMap<>(ids));
        this.sourceId = sourceId;
        this.sinkId = sinkId;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getFileName() {
        return fileName;
    }

    public Graph getGraph() {
        return graph;
    }

    public List<String> getVertices() {
        return vertices;
    }

    public int vertexCount() {
        return vertices.size();
    }

    public int arcCount() {
        return graph.getM();
    }

    public int zeroCapacityCount() {
        return graph.getZeroCapacityCount();
    }

    public boolean hasVertex(String name) {
        return name != null && ids.containsKey(name);
    }

    /** Tra ve chi so dinh, hoac -1 neu khong ton tai. */
    public int idOf(String name) {
        if (name == null) {
            return -1;
        }
        Integer id = ids.get(name);
        return id == null ? -1 : id;
    }

    public String nameOf(int id) {
        if (id <= 0 || id > vertices.size()) {
            return "?";
        }
        return vertices.get(id - 1);
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getSinkId() {
        return sinkId;
    }

    public String getSourceName() {
        return nameOf(sourceId);
    }

    public String getSinkName() {
        return nameOf(sinkId);
    }

    public String summary() {
        return String.format("Dataset=%s | V=%d | E=%d | zero-capacity=%d | Source=%s | Sink=%s",
                datasetName, vertexCount(), arcCount(), zeroCapacityCount(),
                getSourceName(), getSinkName());
    }
}
