package base;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/11/4.
 */
public abstract class indexCluster  {
    protected ArrayList<Integer> patentsIndex=new ArrayList<>();
    /**
     *
     * @return the patents index in the cluster
     */
    public ArrayList<Integer> getPatentsIndex() {
        return this.patentsIndex;
    }

    /**
     * add a new patents index into the cluster
     * @param num the new patent index
     */
    public void addInstance(int num) { this.patentsIndex.add(num);}

    public void addInstances(ArrayList<Integer> nums) { this.patentsIndex.addAll(nums);}
}
