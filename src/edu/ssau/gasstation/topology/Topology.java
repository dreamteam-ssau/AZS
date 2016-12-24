package edu.ssau.gasstation.topology;

/**
 * Created by andrey on 04.12.16.
 */

public class Topology {
    private TopologyItem[][] topology;

    public Topology(int height, int width) {
        this.topology = new TopologyItem[height][width];
    }

    public void setTopologyItem(TopologyItem item, int i, int j){
        if(i < topology.length && j < topology[0].length) {
            this.topology[i][j] = item;
        }
    }

    public TopologyItem getTopologyItem(int i, int j){
        if(i < topology.length && j < topology[0].length && i > -1 && j > -1) {
            return this.topology[i][j];
        }
        else return null;
    }

    public TopologyItem[][] getTopology(){
        return this.topology;
    }

    public int getWidth(){
        return this.topology.length;
    }
    public int getHeight(){
        return this.topology[0].length;
    }

    public int[] findeEntry(){
        int[] result = new int[2];
        result[0] = -1;
        result[1] = -1;
        for(int i = 0; i < topology[topology.length].length; i++){
            if(topology[topology.length - 1][i] instanceof Entry){
                result[0] = i;
                result[1] = topology.length - 1;
            }
        }
        return result;
    }
}
