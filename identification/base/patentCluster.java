package base;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/9/7.
 */
public class patentCluster
{
    ArrayList<patent> patents=new ArrayList<>();
    ArrayList<String> Label;
    private int serial;

    public void setSerial(int number)
    {
        this.serial=number;
    }
    public void addPatent(patent p)
    {
        this.patents.add(p);
    }
    public ArrayList<patent> getPatents()
    {
        return patents;
    }
}
