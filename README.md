# PROJECT 04 - GOODS DISTRIBUTION NETWORK

**Môn học:** Lý thuyết đồ thị (CSC106400)
**Nội dung:** Phân tích năng lực vận chuyển và tuyến giao hàng bằng Ford-Fulkerson và Dijkstra.

Chương trình Console bằng Java, chạy được từ một điểm vào duy nhất `Main`:

- Đọc graph có hướng từ file: mỗi cung có **capacity** (số nguyên ≥ 0) và **weight** (số thực ≥ 0).
- **Ford-Fulkerson** (BFS / Edmonds-Karp) chạy trên **residual graph mới** mỗi lần, không sửa graph gốc.
- **Dijkstra** chạy trên **graph gốc**, chỉ dùng cung `capacity > 0`, tính Distance/Predecessor cho mọi đỉnh reachable.
- Hiển thị augmenting path, bottleneck, residual graph, flow trên cung gốc, verify flow, shortest path.
- 5 màn hình menu, 9 testcase tự động (Expected/Actual/Pass).

---

## 1. Yêu cầu môi trường

- JDK **17 trở lên** (khuyến nghị JDK 21).
- Không dùng thư viện ngoài; chỉ dùng JDK chuẩn.
- Console hỗ trợ UTF-8 (chương trình in bằng tiếng Việt không dấu để an toàn trên mọi terminal).

Kiểm tra:

```bat
javac -version
java -version
```

---

## 2. Cách chạy nhanh

### Windows (Command Prompt)

```bat
run.bat
```

Chạy self-test:

```bat
run.bat --selftest
```

### Windows (PowerShell)

```powershell
.\run.ps1
.\run.ps1 --selftest
```

Nếu Windows chặn script do Execution Policy:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 --selftest
```

### Linux / macOS

```bash
chmod +x run.sh
./run.sh
./run.sh --selftest
```

### Biên dịch / chạy thủ công

Windows PowerShell (đứng tại thư mục project):

```powershell
javac --release 17 -encoding UTF-8 -d bin (Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName)
java -cp bin Main
java -cp bin Main --selftest
```

Linux / macOS:

```bash
mkdir -p bin
find src -name "*.java" -print0 | xargs -0 javac --release 17 -encoding UTF-8 -d bin
java -cp bin Main
```

### Eclipse

1. `File > Import > Existing Projects into Workspace`, chọn thư mục project.
2. Mở `src/Main.java`.
3. `Run As > Java Application`.
4. Có thể truyền tham số `--selftest` trong `Run Configurations > Arguments`.

> Lưu ý: `module-info.java` đã được bỏ để `Main` ở package mặc định chạy được bằng `java -cp bin Main` trên mọi JDK. Nếu giảng viên yêu cầu mô-đun hóa, có thể thêm lại và export các package `algorithm, io, models, testing, ui, utils`.

---

## 3. Cấu trúc thư mục

```
PROJECT_04_GOODS_DISTRIBUTION_NETWORK/
├── src/
│   ├── Main.java                  # Điểm vào duy nhất
│   ├── algorithm/
│   │   ├── FordFulkerson.java     # Ford-Fulkerson + BFS, trace reverse arc
│   │   └── Dijkstra.java          # Dijkstra trên graph gốc
│   ├── io/
│   │   └── InputOuputManager.java # Parser + validation + catalog dataset
│   ├── models/
│   │   ├── Arc.java               # Cung (u,v,capacity,weight)
│   │   ├── Graph.java             # Graph gốc (read-only với thuật toán)
│   │   ├── ResidualGraph.java     # residual capacity + flow
│   │   └── Network.java           # Dataset + map tên đỉnh ↔ id
│   ├── testing/
│   │   └── TestRunner.java        # 9 testcase tự động
│   ├── ui/
│   │   └── UI.java                # 5 màn hình menu Console
│   └── utils/
│       └── Utils.java             # Controller, hiển thị, verify
├── data/                          # Dataset chính + demo + đặc biệt + invalid
├── bin/                           # Class đã biên dịch (có thể xóa và build lại)
├── run.bat / run.ps1 / run.sh
└── README.md
```

---

## 4. Định dạng dataset

Mỗi dòng:

```
u v capacity weight
```

- `u`, `v`: tên đỉnh, không chứa khoảng trắng (`WH`, `A`, `ST`, ...).
- `capacity`: số nguyên `>= 0`.
- `weight`: số thực hữu hạn `>= 0`.
- Dòng bắt đầu bằng `#` là chú thích; dòng trống bị bỏ qua.
- Quy ước: graph có hướng, **không self-loop**, **không cung trùng**, **không dùng đồng thời `(u,v)` và `(v,u)`**.
- `capacity = 0` là tuyến **không khả dụng**: không truyền flow và Dijkstra bỏ qua.

Ví dụ:

```
WH A 15 4.5
A ST 10 2.5
```

Source mặc định là `WH`, Sink mặc định là `ST` (có thể chọn đỉnh khác trong menu chạy).

---

## 5. Dataset đi kèm

| Mã | File | V | E | Ý nghĩa | Kết quả kỳ vọng |
|---|---|---:|---:|---|---|
| MAIN | `main_dataset.txt` | 20 | 34 | Dataset chính | Max flow WH→ST = **35**; Dijkstra = **20.5** |
| DEMO | `demo_dataset.txt` | 10 | 13 | Minh họa | Max flow = **27**; Dijkstra = **7.5** (WH→A→C→ST) |
| REV | `residual_reverse_update_dataset.txt` | 6 | 7 | Bắt buộc dùng cung ngược residual | Max flow = **8**; đường tăng thứ 3 là `WH→C→B→A[R]→D→ST` |
| FLOW0 | `source_sink_disconnected_dataset.txt` | 6 | 4 | WH không tới được ST | Max flow = **0**; Dijkstra **NO PATH** |
| ZERO | `zero_capacity_arc_dataset.txt` | 5 | 5 | Có cung capacity = 0 | Max flow = **10**; flow WH→B = 0; Dijkstra = **9.0** |
| NOPATH | `dijkstra_no_path_dataset.txt` | 4 | 3 | Dijkstra không có đường | Max flow = **0**; Dijkstra **NO PATH** |

Dataset không hợp lệ (dùng cho testcase TC07):

| File | Vi phạm |
|---|---|
| `invalid_capacity_dataset.txt` | capacity âm |
| `invalid_weight_dataset.txt` | weight âm |
| `invalid_duplicate_dataset.txt` | cung trùng |
| `invalid_reverse_dataset.txt` | tồn tại đồng thời `(u,v)` và `(v,u)` |
| `invalid_selfloop_dataset.txt` | self-loop |
| `invalid_format_dataset.txt` | sai số cột |

---

## 6. Menu chương trình

```
MAIN MENU
├─ [1] Quản lý dữ liệu mạng vận chuyển
│    ├─ [1] Nạp dữ liệu từ file
│    ├─ [2] Xem tổng hợp graph gốc + capacity/weight
│    ├─ [3] Chọn dataset mẫu / kiểm thử
│    └─ [4] Reload hoặc Clear dataset hiện tại
├─ [2] Chạy Maximum Flow / Dijkstra
│    ├─ [1] Ford-Fulkerson
│    ├─ [2] Dijkstra
│    └─ [3] Chạy cả hai
├─ [3] Kết quả / Kiểm chứng
│    ├─ [1] Max Flow và flow trên cung gốc
│    ├─ [2] Residual graph / augmenting paths / bottleneck
│    ├─ [3] Dijkstra: Distance/Predecessor, Path/Total Cost
│    └─ [4] Verify flow + phân tích tổng hợp
├─ [4] Kiểm thử / Hướng dẫn
│    ├─ [1] Chạy một testcase
│    ├─ [2] Chạy tất cả testcase
│    ├─ [3] Pass/Fail
│    ├─ [4] Định dạng input / hướng dẫn thuật toán
│    └─ [5] Thông tin project
└─ [0] Thoát
```

- Tại menu con, `0` luôn là **Quay lại**; tại Main Menu, `0` là **Thoát**.
- Nhập sai không làm crash: chương trình thông báo và yêu cầu nhập lại.
- Khi chưa nạp dữ liệu, các chức năng cần graph hiển thị **NOT LOADED**.
- Đổi dataset/reload/clear sẽ **xóa residual và kết quả cũ** để tránh hiển thị dữ liệu cũ.

---

## 7. Testcase tự động

Chạy `run.bat --selftest` hoặc menu 5 → [2]. Kết quả hiện tại: **9/9 PASS**.

| Mã | Nội dung | Kỳ vọng |
|---|---|---|
| TC01 | Ford-Fulkerson trên MAIN | max flow = 35, verify flow PASS |
| TC02 | Residual reverse update | max flow = 8, có dùng cung ngược `[R]` |
| TC03 | Source-Sink không reachable | max flow = 0, Dijkstra NO PATH |
| TC04 | Cung capacity = 0 | flow WH→B = 0, max flow = 10, Dijkstra = 9.0 |
| TC05 | Dijkstra bình thường | WH→A→C→ST, cost = 7.5 |
| TC06 | Dijkstra NO PATH | Không có path giả |
| TC07 | Dataset không hợp lệ | 6/6 bị từ chối đúng loại lỗi |
| TC08 | Source = Sink | Max flow bị từ chối; Dijkstra cost = 0, path chỉ gồm Source |
| TC09 | Dijkstra MAIN + chiều ngược | WH→ST cost 20.5; A→WH NO PATH |

---

## 8. Ghi chú thiết kế

- `Graph` chỉ lưu dữ liệu gốc (`capacity`, `weight`); không có flow.
- `ResidualGraph` được tạo mới trong mỗi lần `FordFulkerson.run(...)`, lưu `residualCap` và `flow`.
- Khi tăng flow trên `(u,v)`:
  - `residualCap[u][v] -= bottleneck`
  - `residualCap[v][u] += bottleneck`
  - nếu `(u,v)` là cung vật lý: `flow[u][v] += bottleneck`
  - nếu `(u,v)` là cung ngược residual (tức `(v,u)` vật lý): `flow[v][u] -= bottleneck`
- Dijkstra **không** dùng residual/flow; chỉ dùng `weight` và điều kiện `capacity > 0`.
- `verifyFlow` kiểm tra:
  1. `0 <= f(u,v) <= c(u,v)`
  2. Bảo toàn luồng tại đỉnh trung gian
  3. Tổng flow ra Source = tổng flow vào Sink

---

## 9. Xử lý lỗi / trạng thái

| Trạng thái | Ý nghĩa |
|---|---|
| `SUCCESS` | Dijkstra tìm được đường |
| `NO PATH` | Target không reachable qua cung `capacity > 0` |
| `MAX FLOW REACHED` | Không còn augmenting path |
| `MAX FLOW = 0` | Source không tới được Sink (kết quả hợp lệ) |
| `INVALID FOR DIJKSTRA` | Phát hiện weight âm/không hữu hạn |
| `INVALID CAPACITY` | capacity âm hoặc không nguyên |
| `INVALID INPUT` | File/menu/tên đỉnh không hợp lệ |
| `NOT LOADED` | Chưa nạp dataset |

---

## 10. Ghi chú nộp bài

- Chạy `run.bat --selftest` để có minh chứng 9/9 PASS.
- Có thể dùng menu 5 → [2] để chụp màn hình Pass/Fail cho báo cáo.
- Các placeholder trong báo cáo (`[Tên nhóm]`, `[Thành viên]`, ...) cần được cập nhật trước khi nộp.
