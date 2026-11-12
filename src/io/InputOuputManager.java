package io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import models.Graph;
import models.Network;

/**
 * Nap dataset va kiem tra tinh hop le cua du lieu.
 *
 * <p>Dinh dang moi dong: {@code u v capacity weight}. Dong bat dau bang '#'
 * la chu thich. Vi du: {@code WH A 15 4.5}.</p>
 */
public class InputOuputManager {

    public record DatasetInfo(String code, String label, String fileName) {
        public String menuLine(int index) {
            return String.format("[%d] %-7s %-38s %s", index, code, label, fileName);
        }
    }

    public static final List<DatasetInfo> VALID_DATASETS = List.of(
            new DatasetInfo("MAIN", "Dataset chinh (>=18 dinh, >=30 cung)", "main_dataset.txt"),
            new DatasetInfo("DEMO", "Dataset minh hoa (10 dinh)", "demo_dataset.txt"),
            new DatasetInfo("REV", "Residual reverse update", "residual_reverse_update_dataset.txt"),
            new DatasetInfo("FLOW0", "Source-Sink khong reachable", "source_sink_disconnected_dataset.txt"),
            new DatasetInfo("ZERO", "Cung capacity = 0", "zero_capacity_arc_dataset.txt"),
            new DatasetInfo("NOPATH", "Dijkstra NO PATH", "dijkstra_no_path_dataset.txt"));

    public static final List<String> INVALID_DATASETS = List.of(
            "invalid_capacity_dataset.txt",
            "invalid_weight_dataset.txt",
            "invalid_duplicate_dataset.txt",
            "invalid_reverse_dataset.txt",
            "invalid_selfloop_dataset.txt",
            "invalid_format_dataset.txt");

    private static final Pattern INTEGER = Pattern.compile("[+-]?\\d+");
    private static final Pattern REAL = Pattern.compile(
            "[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?");

    public static class LoadResult {
        private final Network network;
        private final List<String> errors;
        private final String resolvedPath;

        private LoadResult(Network network, List<String> errors, String resolvedPath) {
            this.network = network;
            this.errors = errors;
            this.resolvedPath = resolvedPath;
        }

        public static LoadResult success(Network network, String path) {
            return new LoadResult(network, List.of(), path);
        }

        public static LoadResult failure(List<String> errors, String path) {
            return new LoadResult(null, List.copyOf(errors), path);
        }

        public boolean isSuccess() {
            return network != null;
        }

        public Network getNetwork() {
            return network;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getResolvedPath() {
            return resolvedPath;
        }
    }

    /** Thu muc data: tim tu thu muc lam viec roi di len cac thu muc cha. */
    public static Path dataDirectory() {
        Path current = Path.of("").toAbsolutePath();
        for (Path dir = current; dir != null; dir = dir.getParent()) {
            Path data = dir.resolve("data");
            if (Files.isDirectory(data)) {
                return data;
            }
        }
        return current.resolve("data");
    }

    public static Path resolveDatasetPath(String input) {
        Path direct = Path.of(input);
        if (Files.isRegularFile(direct)) {
            return direct;
        }
        Path inData = dataDirectory().resolve(input);
        if (Files.isRegularFile(inData)) {
            return inData;
        }
        return direct;
    }

    public static String datasetName(Path file) {
        String name = file.getFileName().toString();
        return name.endsWith(".txt") ? name.substring(0, name.length() - 4) : name;
    }

    /** Nap mot file dataset. Tra ve loi chi tiet thay vi nem exception. */
    public LoadResult load(String pathInput) {
        List<String> errors = new ArrayList<>();
        if (pathInput == null || pathInput.isBlank()) {
            return LoadResult.failure(List.of("INVALID INPUT: duong dan rong"), "");
        }
        Path file = resolveDatasetPath(pathInput.trim());
        if (!Files.isRegularFile(file)) {
            return LoadResult.failure(
                    List.of("INVALID INPUT: khong tim thay file " + file.toAbsolutePath()), file.toString());
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return LoadResult.failure(
                    List.of("INVALID INPUT: khong doc duoc file (" + ex.getMessage() + ")"), file.toString());
        }

        record RawArc(String u, String v, int capacity, double weight) {
        }

        List<RawArc> rawArcs = new ArrayList<>();
        Map<String, Integer> ids = new LinkedHashMap<>();
        Set<String> edges = new LinkedHashSet<>();

        for (int i = 0; i < lines.size(); i++) {
            int lineNo = i + 1;
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split("\\s+");
            if (parts.length != 4) {
                errors.add("Dong " + lineNo + ": dinh dang sai, can 4 cot 'u v capacity weight'");
                continue;
            }
            String u = parts[0];
            String v = parts[1];
            String capText = parts[2];
            String weightText = parts[3];

            if (u.equals(v)) {
                errors.add("Dong " + lineNo + ": self-loop (" + u + "," + v + ") khong hop le");
                continue;
            }
            if (!INTEGER.matcher(capText).matches()) {
                errors.add("Dong " + lineNo + ": capacity '" + capText + "' phai la so nguyen >= 0");
                continue;
            }
            int capacity;
            try {
                capacity = Integer.parseInt(capText);
            } catch (NumberFormatException ex) {
                errors.add("Dong " + lineNo + ": capacity '" + capText + "' vuot mien so nguyen");
                continue;
            }
            if (!REAL.matcher(weightText).matches()) {
                errors.add("Dong " + lineNo + ": weight '" + weightText + "' phai la so thuc >= 0");
                continue;
            }
            double weight;
            try {
                weight = Double.parseDouble(weightText);
            } catch (NumberFormatException ex) {
                errors.add("Dong " + lineNo + ": weight '" + weightText + "' khong hop le");
                continue;
            }
            if (capacity < 0) {
                errors.add("Dong " + lineNo + ": capacity " + capacity + " am (phai >= 0)");
                continue;
            }
            if (weight < 0 || !Double.isFinite(weight)) {
                errors.add("Dong " + lineNo + ": weight " + weightText + " khong hop le (phai huu han >= 0)");
                continue;
            }

            String key = u + "\u0000" + v;
            String reverseKey = v + "\u0000" + u;
            if (edges.contains(key)) {
                errors.add("Dong " + lineNo + ": cung (" + u + "," + v + ") bi trung");
                continue;
            }
            if (edges.contains(reverseKey)) {
                errors.add("Dong " + lineNo + ": ton tai dong thoi (" + u + "," + v
                        + ") va (" + v + "," + u + ") - vi pham quy uoc dataset");
                continue;
            }
            edges.add(key);
            ids.putIfAbsent(u, ids.size() + 1);
            ids.putIfAbsent(v, ids.size() + 1);
            rawArcs.add(new RawArc(u, v, capacity, weight));
        }

        if (ids.isEmpty()) {
            errors.add("Dataset rong: khong co cung hop le nao");
        }
        if (!errors.isEmpty()) {
            return LoadResult.failure(errors, file.toString());
        }

        Graph graph = new Graph(ids.size(), true);
        for (RawArc raw : rawArcs) {
            int u = ids.get(raw.u());
            int v = ids.get(raw.v());
            if (!graph.addArc(u, v, raw.capacity(), raw.weight())) {
                errors.add("Khong them duoc cung (" + raw.u() + "," + raw.v() + ")");
            }
        }
        if (!errors.isEmpty()) {
            return LoadResult.failure(errors, file.toString());
        }

        List<String> vertexNames = new ArrayList<>(ids.keySet());
        int source = ids.getOrDefault("WH", 1);
        int sink = ids.getOrDefault("ST", vertexNames.size());
        Network network = new Network(datasetName(file), file.getFileName().toString(),
                graph, vertexNames, ids, source, sink);
        return LoadResult.success(network, file.toString());
    }
}
