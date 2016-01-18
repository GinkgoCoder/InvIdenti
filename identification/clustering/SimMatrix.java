package clustering;

import base.ProgressBar;
import base.patent;
import clustering.distancefunction.AbstractDistance;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by sunlei on 15/10/27.
 */
public class SimMatrix {

    ArrayList<ArrayList<Double>> simMatrix=new ArrayList<>();

    ArrayList<patent> patents;

    public ArrayList<String> patentsID;

    private ArrayList<Integer> shuffledIndex;
    double threshold;

    AbstractDistance distance;

    public SimMatrix(ArrayList<patent> patents,AbstractDistance distance) {
        this.patents=patents;
        this.distance=distance;


        buildMatrix();



    }

    public ArrayList<Integer> getShuffledIndex(){
        return this.shuffledIndex;
    }

    public void setShuffledIndex(ArrayList<Integer> shuffledIndex) {
        this.shuffledIndex=shuffledIndex;
    }

    public SimMatrix(String Matrix_path)  {
        FileReader f= null;
        try {
            f = new FileReader(Matrix_path);
            BufferedReader br=new BufferedReader(f);
            String var0=br.readLine();
            while(var0!=null) {
                String[] var1=var0.split(";");
                ArrayList<Double> var3=new ArrayList<>();
                for(String var2:var1) {
                    var3.add(Double.parseDouble(var2));
                }
                this.simMatrix.add(var3);
                var0=br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Build the similarity matrix for the patents with distance function
     */
    private void buildMatrix() {

        int totalnumber=this.patents.size()*(this.patents.size()-1)/2;

        for(int i=0;i<this.patents.size();i++) {
            ArrayList<Double> temp=new ArrayList<>();
            for (int j=0;j<this.patents.size();j++) {
                temp.add(0.0);
            }
            simMatrix.add(temp);
        }

        int currentnumber=0;


        for(int i=0;i<this.patents.size()-1;i++) {
            for (int j=i+1;j<this.patents.size();j++) {
                double temp=distance.distance(this.patents.get(i),this.patents.get(j));
                temp= (new BigDecimal(temp).setScale(2, RoundingMode.UP)).doubleValue();
                simMatrix.get(i).set(j,temp);
                simMatrix.get(j).set(i,temp);
                currentnumber++;
                System.out.print("\r"+ProgressBar.barString((int)((currentnumber*100/totalnumber))));

            }
        }
        System.out.println();
    }

    /**
     * Store Matrix into a file
     * @param Path file Path
     */
    public void storeMatrix(String Path){
        try {
            FileWriter fileWriter=new FileWriter(Path);
            String temp="";
            for(ArrayList<Double> var0:simMatrix) {
                temp="";
                for(double var1:var0) {

                    temp+=var1+";";
                }
                temp+="\n";
                fileWriter.write(temp);
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buildMatrix(double threshold,ArrayList<String> patentsID) {

        for(int i=0;i<this.patents.size();i++) {
            ArrayList<Double> temp=new ArrayList<>();
            for (int j=0;j<this.patents.size();j++) {
                temp.add(0.0);
            }
            simMatrix.add(temp);
        }

        for(int i=0;i<this.patents.size()-1;i++) {
            for (int j=i+1;j<this.patents.size();j++) {
                double temp=distance.distance(this.patents.get(i),this.patents.get(j));

                if (temp>threshold&&patentsID.get(i).equalsIgnoreCase(patentsID.get(j))) {
                double sum=0;


                    sum+=distance.weightLastName*distance.compareName(patents.get(i).getLastName(),patents.get(j).getLastName());
                    sum+=distance.weightFirstName*distance.compareName(patents.get(i).getFirstName(),patents.get(j).getFirstName());

                    if(sum<threshold) {
                        System.out.println(patents.get(i).getLastName()+" "+patents.get(i).getFirstName());
                        System.out.println(patents.get(j).getLastName()+" "+patents.get(j).getFirstName());
                    }
                }
                simMatrix.get(i).set(j,temp);
                simMatrix.get(j).set(i,temp);
            }
        }
    }

    public  ArrayList<ArrayList<Double>> getSimMatrix() {
        return simMatrix;
    }
    /**
     *
     * @param i patents i
     * @param j patens j
     * @return the similarity between the patent i and patent j
     */
    public double getSimbetweenPatents(int i,int j) {

        if (shuffledIndex.size()==0||shuffledIndex==null)
        return simMatrix.get(i).get(j); else {
            return simMatrix.get(shuffledIndex.get(i)).get(shuffledIndex.get(j));
        }
    }

}
