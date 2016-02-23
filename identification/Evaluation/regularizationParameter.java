package Evaluation;

import base.pair;
import base.patent;
import clustering.distancefunction.CosDistance;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;
import preprocessing.IniFile;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sunlei on 15/12/26.
 */
public class regularizationParameter {
    DoubleMatrix X;
    DoubleMatrix Y;
    ArrayList<patent> patents;
    ArrayList<String> patentsID;
    IniFile ini=new IniFile();
    int numberofOptions;
    int number;
    int numberofPatents;
    int K=5;

    public regularizationParameter(ArrayList<patent> patents,ArrayList<String> patentsID, int numberofOptions,int num){
        number=num;
        this.numberofPatents=patents.size();
        this.patents=patents;
        this.patentsID=patentsID;
        this.numberofOptions=numberofOptions;

        storeText("RegularizationParameter"+num+".txt",false,"");
        storeText("RegularizationParameterWeights"+num+".txt",false,"");
        double[] result;
       for(double lambada=0.0;lambada<=6.0;lambada+=0.1) {
           storeText("RegularizationParameterWeights"+num+".txt",true,lambada+"\n");
           result = crossValidation(lambada);
           storeText("RegularizationParameter"+num+".txt",true,lambada+" "+result[0]+" "+result[1]+"\n");
       }


    }

    public double[] crossValidation(double lambda){
        double[] result=new double[2];
        ArrayList<patent> training_p=new ArrayList<>();
        ArrayList<patent> validation_p=new ArrayList<>();
        ArrayList<String> training_IDs=new ArrayList<>();
        ArrayList<String> validation_IDs=new ArrayList<>();

        ArrayList<Double> error=new ArrayList<>();
        for(int start=0;start<numberofPatents;start+=(numberofPatents/5)) {
            int end=start+(numberofPatents/5)-1;
            if(end>numberofPatents-1) {
                end=numberofPatents-1;
            }

            training_p.clear();
            training_IDs.clear();
            validation_p.clear();
            validation_p.clear();

            for(int i=0;i<numberofPatents;i++) {
                if(i>=start&&i<=end) {
                    validation_p.add(patents.get(i));
                    validation_IDs.add(patentsID.get(i));
                } else {
                    training_IDs.add(patentsID.get(i));
                    training_p.add(patents.get(i));
                }
            }

            pair<DoubleMatrix,DoubleMatrix> training=new trainingDataMatrix(training_p,training_IDs,false).getPatents_Matrices();
            pair<DoubleMatrix,DoubleMatrix> validation=new trainingDataMatrix(validation_p,validation_IDs,false).getPatents_Matrices();

            DoubleMatrix X1=training.firstarg;
            DoubleMatrix Y1=training.secondarg;
            DoubleMatrix X2=validation.firstarg;
            DoubleMatrix Y2=validation.secondarg;

          System.out.println();

           error.add (training(X1,Y1,X2,Y2,lambda,start/(numberofPatents/5)+1));



        }

        result[0]=0;
        for(double d:error) {
            result[0]+=d;

        }
        result[0]/=K;
        result[1]=0;
        for(double d:error) {
            result[1]+=(d-result[0])*(d-result[0]);
        }

        result[1]/=K;
        result[1]=Math.sqrt(result[1]);

        return result;
    }

    public double training(DoubleMatrix X,DoubleMatrix Y,DoubleMatrix X1,DoubleMatrix Y1,double lambda,int numofIter){

        System.out.println();
        System.out.println("The "+numofIter+"th Iteration for "+lambda);



        double[][] var0 = new double[numberofOptions + 1][1];
        for (int i = 0; i < numberofOptions + 1; i++) {
            var0[i][0] = 1.0;
        }
        DoubleMatrix thetas = new DoubleMatrix(var0);
        int maxIteration=5000;
        double alpha=9.99;

        //Calculate the initial error;
        DoubleMatrix varM1 = applyLogisticonData(X, thetas);
        double sum = 0;
        for (int m = 0; m < Y.rows; m++) {

            double temp = varM1.get(m, 0);


            if (temp > 1) temp = 1;
            if (temp < 0) temp = 0;

            if (Y.get(m, 0) == 1) {
                sum += Math.log(temp);
            } else {
                sum += Math.log(1 - temp);
            }

            //    sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);
        }

        double initial_error = -sum;
        double previous_error=-sum;
        double relative_change=0;

        for(int k=0;k<maxIteration;k++) {
            DoubleMatrix thetas_t = new DoubleMatrix(thetas.toArray2());
            pair<DoubleMatrix, Double> var1 = updateWeights(X, Y, thetas_t, alpha / X.rows, lambda);
            double error = var1.secondarg;
            relative_change=2 * Math.abs(var1.secondarg - previous_error) / (var1.secondarg + previous_error + 1e-4);
            if ( relative_change< 3e-4) {
                thetas=new DoubleMatrix(thetas_t.toArray2());
                previous_error=error;
                break;
            }
            previous_error=error;
            System.out.print("\r"+relative_change+" "+k);
            thetas=new DoubleMatrix(thetas_t.toArray2());

        }

        System.out.println();

        ArrayList<String> optionsName=ini.getOptionsNames();

        double[] weights = thetas.toArray();

        String tempS="";
        for(double d:weights) {
            tempS+=d+" ";
        }
        tempS+="\n";

        storeText("RegularizationParameterWeights"+this.number+".txt",true,tempS);






        varM1 = applyLogisticonData(X1, thetas);
        sum = 0;
        for (int m = 0; m < Y1.rows; m++) {

            double temp = varM1.get(m, 0);


            if (temp > 1) temp = 1;
            if (temp < 0) temp = 0;

            sum+=(temp-Y1.get(m,0))*(temp-Y1.get(m,0));

            //    sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);
        }


        return sum;
    }


    public void outputMatrix(DoubleMatrix x,String name) {
        System.out.println("Matrix Name:" +name);
        int var0=0;
        for (int i=0;i<x.rows;i++) {
            String temp="";
            for(int j=0;j<x.columns;j++) {

                temp+=x.get(i,j)+" ";


            }
            System.out.println(temp);
        }



    }



    public void storeText(String path,boolean follow,String str){
        if (follow) {
            try {
                FileWriter w=new FileWriter(path,follow);
                w.write(str);
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileWriter w=new FileWriter(path,follow);
                w.write(str);
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }








    /**
     * Update the weights and threshold
     * @param X Similarity matrix
     * @param Y target value matrix
     * @param thetas weights and threshold vector
     * @param alpha learning rate
     * @param lamda regularization factor
     * @return updated weights and threshold vector
     *
     */
    public pair<DoubleMatrix,Double> updateWeights(DoubleMatrix X, DoubleMatrix Y, DoubleMatrix thetas, double alpha, double lamda) {


        DoubleMatrix varM1=applyLogisticonData(X,thetas);


        double error=0;
        varM1.subi(Y);
        DoubleMatrix error_M=new DoubleMatrix(varM1.toArray2());

        //error=MatrixFunctions.absi(error_M).sum()/X.rows;

        varM1 = X.transpose().mmul(varM1);


        DoubleMatrix thetas1 = new DoubleMatrix(thetas.toArray2());

        thetas1 = thetas1.put(0, 0, 0);

        varM1.muli(alpha);


        thetas1.muli(lamda * alpha);

        thetas.subi(varM1);
        thetas.subi(thetas1);

        varM1=applyLogisticonData(X,thetas);

        double sum=0;
        for (int m = 0; m < Y.rows; m++) {

            double temp=varM1.get(m,0);


            if (temp>1) temp=1;
            if (temp<0) temp=0;

            if (Y.get(m,0)==1) {
                sum+=Math.log(temp);
            } else {
                sum+=Math.log(1-temp);
            }

            //    sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);
        }

        return new pair<>(thetas,-sum);

    }

    /**
     * Apply sigmoid function on the similarity matrix
     * @param X the similarity matrix
     * @param thetas the weights and the threshold
     * @return the Matrix after applying the sigmoid function on the similarity matrix
     */

    public DoubleMatrix applyLogisticonData(DoubleMatrix X,DoubleMatrix thetas) {

        DoubleMatrix varM1 = new DoubleMatrix(X.transpose().toArray2());



        varM1 = varM1.transpose().mmul(thetas);

        DoubleMatrix varM2 = new DoubleMatrix(varM1.rows, varM1.columns);

        varM2.subi(varM1);

        MatrixFunctions.expi(varM2);

        varM2.addi(1);

        DoubleMatrix varM3 = new DoubleMatrix(varM2.rows, varM2.columns);
        varM3.addi(1);

        varM3.divi(varM2);



        return varM3;

    }

    /**
     * Generate a distance function based on a arraylist of weights and a arraylist of index
     * @param attrIndex distance function index
     * @param weights distance function weights
     * @return the generated distance function
     */
    public CosDistance generateDistanceFunction(ArrayList<Integer> attrIndex, ArrayList<Double> weights) {
        CosDistance var0=new CosDistance();
        if (attrIndex!=null) {
            boolean[] var1=new  boolean[this.ini.getOptionsNames().size()];
            for(int i=0;i<this.ini.getOptionsNames().size();i++) {
                if (attrIndex.contains(i)) {
                    var1[i]=true;
                } else {
                    var1[i]=false;
                }
            }
            var0.setOptions(var1);
        }
        if (weights!=null&&weights.size()>=this.ini.getOptionsNames().size()) {
            double[] var2=new double[this.ini.getOptionsNames().size()];
            for(int i=0;i<this.ini.getOptionsNames().size();i++) {
                var2[i]=weights.get(i);
            }

            var0.setWeights(var2);
        }



        return var0;
    }
}
