package edu.ssau.gasstation.modelling.PoolUtils;

import java.util.Arrays;
import java.util.PriorityQueue;

import edu.ssau.gasstation.topology.TopologyItem;

import static java.lang.StrictMath.abs;

public class Dijkstra {
  int[][] dist;
  Cell[][] prev;
  TopologyItem[][] topology;
  int n, m;
  Cell start;

  public Dijkstra(TopologyItem[][] topologyItem, int x, int y) {
    topology = topologyItem;
    n = topologyItem.length;
    m = topologyItem[0].length;
    prev = new Cell[n][m];
    dist = new int[n][m];
    start = new Cell(x, y, 0);
  }

  public int[][] getDist() {
    return dist;
  }

  public Cell[][] getPrev() {
    return prev;
  }

  public void dijkstra(int coordX, int coordY) {
    for (int i = 0; i < dist.length; i++) {
      Arrays.fill(dist[i], Integer.MAX_VALUE);
    }
    dist[start.x][start.y] = 0;
    PriorityQueue<Cell> q = new PriorityQueue<>();
    q.add(start);
    while (!q.isEmpty()) {
      Cell cur = q.poll();
      int x = cur.x;
      int y = cur.y;
      int curd = cur.coast;
      if (curd > dist[x][y])
        continue;

      for (int dx = -1; dx <= 1; dx++) {
        for (int dy = -1; dy <= 1; dy++) {
          if (abs(dx) == 1 && abs(dy) == 1)
            continue;
          if (dx == 0 && dy == 0)
            continue;
          int xTo = x + dx;
          int yTo = y + dy;
          if (xTo < 0 || xTo >= n || yTo < 0 || yTo >= m)
            continue;
          if (topology[xTo][yTo] != null) {
            if(coordX!=xTo || coordY!=yTo) continue;
          }
          int len = 1;
          if (dist[x][y] + len < dist[xTo][yTo]) {
            dist[xTo][yTo] = dist[x][y] + len;
            prev[xTo][yTo] = new Cell(x, y, -1);
            q.add(new Cell(xTo, yTo, dist[xTo][yTo]));
          }
        }
      }
    }
  }

  class Cell implements Comparable<Cell> {
    int x, y, coast;

    public Cell(int x, int y, int coast) {
      this.x = x;
      this.y = y;
      this.coast = coast;
    }


    @Override
    public int compareTo(Cell o) {
      return Integer.compare(this.coast, o.coast);
    }
  }
}
