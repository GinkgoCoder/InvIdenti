package clustering;

import base.ProgressBar;
import base.patent;
import base.textOperator;
import clustering.distancefunction.AbstractDistance;

import java.io.*;
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

    public SimMatrix(ArrayList<patent> patents,AbstractDistance distance,String path) {
        this.patents=patents;
        this.distance=distance;
        buildMatrix(path);

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


    private void buildMatrix(String path){
        textOperator.storeText(path,false,"");

        int totalnumber=this.patents.size()*(this.patents.size()-1)/2+this.patents.size();



        int currentnumber=0;


        for(int i=0;i<this.patents.size();i++) {
            String temp_a="";
            for (int j=0;j<=i;j++) {
                if (i==j) temp_a+="0.00;"; else {
                    double temp = distance.distance(this.patents.get(i), this.patents.get(j));
                    temp = (new BigDecimal(temp).setScale(2, RoundingMode.UP)).doubleValue();
                    temp_a+=temp+";";


                    // simMatrix.get(i).set(j, temp);
                    // simMatrix.get(j).set(i, temp);
                }



                currentnumber++;

                System.out.print("\r"+ProgressBar.barString((int)((currentnumber*100/totalnumber)))+" "+currentnumber);

            }
            textOperator.storeText(path,true,temp_a+"\n");
          //  simMatrix.add(temp_a);
        }
        System.out.println();
    }

    /**
     * Build the similarity matrix for the patents with distance function
     */
    private void buildMatrix() {

        int totalnumber=this.patents.size()*this.patents.size();



        int currentnumber=0;


        for(int i=0;i<this.patents.size();i++) {
            ArrayList<Double> temp_a=new ArrayList<>();
            for (int j=0;j<this.patents.size();j++) {
               if (i==j) temp_a.add(0.0); else if (i<j)
               {
                    double temp = distance.distance(this.patents.get(i), this.patents.get(j));
                    temp = (new BigDecimal(temp).setScale(2, RoundingMode.UP)).doubleValue();
                    temp_a.add(temp);


                    // simMatrix.get(i).set(j, temp);
                   // simMatrix.get(j).set(i, temp);
                }  else {
                  temp_a.add(simMatrix.get(j).get(i));
               }
                currentnumber++;
                System.out.print("\r"+ProgressBar.barString((int)((currentnumber*100/totalnumber)))+" "+currentnumber);

            }
            simMatrix.add(temp_a);
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
        int b,l=0;
        if (shuffledIndex==null||shuffledIndex.size()==0)
        {
            if(i>j) {
               b=i;
                l=j;
            } else {
                b=j;
                l=i;
            }
            return simMatrix.get(b).get(l);
        } else {
            if (shuffledIndex.get(i)>shuffledIndex.get(j)) {
                b=i;
                l=j;
            } else {
                b=j;
                l=i;
            }
            return simMatrix.get(shuffledIndex.get(b)).get(shuffledIndex.get(l));
        }
    }


}
