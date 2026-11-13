"""Model the project's algorithms on its datasets to obtain the numbers used in the report.

Mirrors src/algorithms/FordFulkerson.java (BFS augmenting paths over a residual
network rebuilt from the original capacities) and src/algorithms/Dijkstra.java
(weights on the original graph, only arcs with capacity > 0).
"""
import sys
from collections import deque

sys.stdout.reconfigure(encoding='utf-8')


def load(path):
    arcs = []
    with open(path, encoding='utf-8') as fh:
        for line in fh:
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            u, v, c, w = line.split()
            arcs.append((u, v, int(c), float(w)))
    return arcs


def max_flow(arcs, s, t):
    res = {}
    order = {}
    nodes = []
    for u, v, c, w in arcs:
        for n in (u, v):
            if n not in nodes:
                nodes.append(n)
        res.setdefault(u, {})[v] = c
        res.setdefault(v, {}).setdefault(u, 0)
        order.setdefault(u, [])
        if v not in order[u]:
            order[u].append(v)
    flow = 0
    paths = []
    steps = 0
    while True:
        parent = {s: None}
        q = deque([s])
        while q:
            u = q.popleft()
            if u in res:
                for v in order.get(u, []):
                    if v not in parent and res[u][v] > 0:
                        parent[v] = u
                        q.append(v)
            if t in parent:
                break
        if t not in parent:
            break
        # bottleneck
        path = []
        cur = t
        b = 10**9
        while cur is not None:
            path.append(cur)
            p = parent[cur]
            if p is None:
                break
            b = min(b, res[p][cur])
            cur = p
        path.reverse()
        cur = t
        while parent[cur] is not None:
            p = parent[cur]
            res[p][cur] -= b
            res[cur][p] += b
            cur = p
        flow += b
        steps += 1
        paths.append((path, b))
    return flow, paths, res


def dijkstra(arcs, s, t):
    adj = {}
    for u, v, c, w in arcs:
        if c > 0:
            adj.setdefault(u, []).append((v, w))
    dist = {s: 0.0}
    prev = {}
    done = set()
    import heapq
    pq = [(0.0, s)]
    while pq:
        d, u = heapq.heappop(pq)
        if u in done:
            continue
        done.add(u)
        for v, w in adj.get(u, []):
            nd = d + w
            if nd < dist.get(v, float('inf')):
                dist[v] = nd
                prev[v] = u
                heapq.heappush(pq, (nd, v))
    if t not in dist:
        return None, dist
    path = []
    cur = t
    while cur != s:
        path.append(cur)
        cur = prev[cur]
    path.append(s)
    path.reverse()
    return (path, dist[t]), dist


for name in ('flow_demo', 'logistics_main', 'ds_rev', 'ds_flow0', 'ds_zero', 'ds_nopath'):
    arcs = load(f'datasets/{name}.txt')
    vset = sorted({x for a in arcs for x in (a[0], a[1])})
    print('=' * 70)
    print(name, 'V=', len(vset), 'E=', len(arcs), 'zero-cap=', sum(1 for a in arcs if a[2] == 0))
    s, t = ('WH', 'ST') if name in ('flow_demo', 'logistics_main') else ('S', 'T')
    flow, paths, res = max_flow(arcs, s, t)
    print(f'max flow {s}->{t} =', flow, '| augmentations =', len(paths))
    for p, b in paths:
        print('   ', ' -> '.join(p), '| bottleneck', b)
    dijk, dist = dijkstra(arcs, s, t)
    if dijk:
        print('dijkstra', ' -> '.join(dijk[0]), 'cost', dijk[1])
    else:
        print('dijkstra NO PATH')
    print('distances:', {k: round(v, 1) for k, v in sorted(dist.items())})
