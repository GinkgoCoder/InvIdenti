package Evaluation;

import base.pair;
import base.patent;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;
import preprocessing.IniFile;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by leisun on 15/12/30.
 */
public class BatchSize {

    int batchSize=100;
    int pqthreshold=5;

    ArrayList<patent> patents=new ArrayList<>();
    ArrayList<String> patentsID=new ArrayList<>();

    ArrayList<patent> training=new ArrayList<>();
    ArrayList<String> trainingID=new ArrayList<>();

    ArrayList<patent> validation=new ArrayList<>();
    ArrayList<String> validationID=new ArrayList<>();

    ArrayList<patent> testing=new ArrayList<>();
    ArrayList<String> testingID=new ArrayList<>();

    IniFile ini=new IniFile();


    int numberofOptions;
    int numberofPatents;

    ArrayList<AbstractDistance> distances;

    int K=5;

    ArrayList<Double> times=new ArrayList<>();

    public BatchSize(ArrayList<patent> patents,ArrayList<String> patentsID,int numberofOptions){
        this.patents=patents;
        this.patentsID=patentsID;
        numberofPatents=patents.size();
        this.numberofOptions=numberofOptions;
        seperateDataset(0,numberofPatents/K+1);
        generateSeperatedDisFunctions();


        pair<DoubleMatrix,DoubleMatrix> trainings=new trainingDataMatrix(training,trainingID,false).getPatents_Matrices();
        pair<DoubleMatrix,DoubleMatrix> validations=new trainingDataMatrix(validation,validationID,false).getPatents_Matrices();
        pair<DoubleMatrix,DoubleMatrix> testings=new trainingDataMatrix(testing,testingID,false).getPatents_Matrices();

        DoubleMatrix X1=trainings.firstarg;
        DoubleMatrix Y1=trainings.secondarg;
        DoubleMatrix X2=testings.firstarg;
        DoubleMatrix Y2=testings.secondarg;
        DoubleMatrix X3=validations.firstarg;
        DoubleMatrix Y3=validations.secondarg;

        ArrayList<Double> times=new ArrayList<>();
        for(int i=500;i<=1000;i+=50) {
         double time=System.currentTimeMillis();
            training(X1,Y1,X2, Y2, X3, Y3, 0, 0);
            double endtime=System.currentTimeMillis();
            times.add(endtime-time);


        }
        for(double d:times) {
            System.out.println(d+" ");
        }

}



    public pair<pair<DoubleMatrix,DoubleMatrix>,pair<Integer,Integer>> geneerateAMiniBatchLRTrainingData(int starti,int startj,int batchsize){
        boolean first=true;

        double[][] x=new double[batchsize][numberofOptions+1];
        double[][] y=new double[batchsize][1];
        int index=0;

        int endi,endj;
        endi=endj=patents.size();

        ArrayList<String> optionsName=ini.getOptionsNames();
        pair<Integer,Integer> continueIndex;
        label:
        for(int i=starti;i<patents.size();i++) {
            for(int j=0;j<patents.size();j++) {
                    if(first) {
                        j=startj;
                        first=false;
                    }

                if (i>j) {
                    x[index][0] = 1.0;
                    if (this.patentsID.get(i).equalsIgnoreCase(this.patentsID.get(j))) {
                        y[index][0] = 1.0;
                    } else {
                        y[index][0] = 0.0;


                    }


                    int var2 = 1;


                    for (int m = 0; m < optionsName.size(); m++) {

                        if (ini.getOptionValue(optionsName.get(m))) {
                            x[index][var2] = distances.get(m).distance(patents.get(i), patents.get(j));
                            var2++;
                        }
                    }

                    index++;
                }
                    if(index>=batchsize) {
                        endi=i;
                        endj=j;

                        break label;
                    }
                }
        }



        continueIndex=new pair<>(endi,endj);

        return new pair<>(new pair<>(new DoubleMatrix(x),new DoubleMatrix(y)),continueIndex);
    }



    public double training(DoubleMatrix X,DoubleMatrix Y,DoubleMatrix X1,DoubleMatrix Y1,DoubleMatrix X2,DoubleMatrix Y2,double lambda,int numofIter){

        System.out.println();
        System.out.println("The "+numofIter+"th Iteration for "+lambda);
        System.out.println(X1.rows+" "+X2.rows);
        ArrayList<Double> errors=new ArrayList<>();
        int errorBatchSize=5;

        double[][] var0 = new double[numberofOptions + 1][1];
        for (int i = 0; i < numberofOptions + 1; i++) {
            var0[i][0] = 1.0;
        }
        DoubleMatrix thetas = new DoubleMatrix(var0);
        int maxIteration=10000;
        double alpha=9.99;

        //Calculate the initial error;

        DoubleMatrix varM1 = applyLogisticonData(X2, thetas);
        double sum = 0;
        for (int m = 0; m < Y2.rows; m++) {

            double temp = varM1.get(m, 0);


            if (temp > 1) temp = 1;
            if (temp < 0) temp = 0;

            sum+=(temp-Y2.get(m,0))*(temp-Y2.get(m,0));

            //    sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);
        }


        double relative_change=0;


        pair<pair<DoubleMatrix, DoubleMatrix>, pair<Integer, Integer>> batch;
        int num=0;
        int updates=0;
        double minerror=Double.MAX_VALUE;
        label:

        for(int k=0;k<maxIteration;k++) {


            int starti,startj;
            starti=startj=0;

            for(int i=0;i<patents.size()*(patents.size()-1)/2;i+=batchSize){
                if (i+batchSize<patents.size()*(patents.size()-1)/2) {
                    batch=geneerateAMiniBatchLRTrainingData(starti,startj,batchSize);
                } else {
                    batch=geneerateAMiniBatchLRTrainingData(starti,startj,patents.size()*(patents.size()-1)/2-i);
                }
                starti=batch.secondarg.firstarg;
                startj=batch.secondarg.secondarg+1;
                if (startj>patents.size()) {
                    starti++;
                    startj=0;
                }
                DoubleMatrix thetas_t = new DoubleMatrix(thetas.toArray2());

                pair<DoubleMatrix, Double> var1 = updateWeights(batch.firstarg.firstarg, batch.firstarg.secondarg, X2,Y2,thetas_t, alpha / batch.firstarg.firstarg.rows, lambda);

                thetas = new DoubleMatrix(thetas_t.toArray2());

                num+=batchSize;
                if (num>=X2.rows) {
                    double errorForValidation=calculateTheError(X2,Y2,thetas);
                    double errorForTraining=calculateTheError(X,Y,thetas);
                    if (errorForValidation<minerror) minerror=errorForValidation;

                    System.out.println(errorForTraining+" "+errorForValidation);

                    if (updates<errorBatchSize) {
                        updates++;

                        errors.add(errorForValidation);
                    } else {
                        errors.remove(0);
                        errors.add(var1.secondarg);


                        if (k > 0) {

                            double PQ=calculatePQ(minerror,errors);
                            System.out.println("PQ"+PQ+" "+minerror);
                            if (PQ>5) break label;
                        }
                    }



                    num=0;
                }




            }


        }

        System.out.println();

        ArrayList<String> optionsName=ini.getOptionsNames();

        double[] weights = thetas.toArray();



        ArrayList<Double> weight = new ArrayList<>();
        int i = 1;

        for (int j = 0; j < optionsName.size(); j++) {
            if (ini.getOptionValue(optionsName.get(j))) {
                //logger.warn(optionsName.get(j)+weights[i]);
                weight.add(weights[i]);
                i++;
            } else {
                weight.add(0.0);
            }

        }

        double threshold = -weights[0];

        System.out.println(this.generateDistanceFunction(null,weight));

        System.out.println("Threshold:"+threshold);


        return sum;
    }


    public double calculatePQ(double minerror,ArrayList<Double> errors){

        double GL=100*(errors.get(errors.size()-1)/minerror-1);

        double PQ=0;
        double sum=0;
        for(double d:errors) {

            sum+=d;
        }

        PQ=GL/(100*(sum/(Collections.min(errors)*errors.size())-1));



        return PQ;

    }

    public double calculateTheError(DoubleMatrix X,DoubleMatrix Y,DoubleMatrix thetas){
        DoubleMatrix varM=applyLogisticonData(X, thetas);

        double sum = 0;
        for (int m = 0; m < Y.rows; m++) {

            double temp = varM.get(m, 0);


            if (temp > 1) temp = 1;
            if (temp < 0) temp = 0;

            sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);

        }

        return -sum;

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
    public pair<DoubleMatrix,Double> updateWeights(DoubleMatrix X, DoubleMatrix Y,DoubleMatrix X1,DoubleMatrix Y1, DoubleMatrix thetas, double alpha, double lamda) {


        DoubleMatrix varM1=applyLogisticonData(X,thetas);


        varM1.subi(Y);


        varM1 = X.transpose().mmul(varM1);


        DoubleMatrix thetas1 = new DoubleMatrix(thetas.toArray2());

        thetas1 = thetas1.put(0, 0, 0);

        varM1.muli(alpha);


        thetas1.muli(lamda * alpha);

        thetas.subi(varM1);
        thetas.subi(thetas1);


        varM1=applyLogisticonData(X1,thetas);

        double sum=0;
        for (int m = 0; m < Y1.rows; m++) {

            double temp=varM1.get(m,0);


            if (temp>1) temp=1;
            if (temp<0) temp=0;

            sum += Y1.get(m, 0) * Math.log(temp) + (1 - Y1.get(m, 0)) * Math.log(1-temp);


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
    public CosDistance generateDistanceFunction(ArrayList<Integer> attrIndex,ArrayList<Double> weights) {
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

    /**
     * geneerate all the seperated distance functions based on the options needed
     */

    public void generateSeperatedDisFunctions(){
        ArrayList<String> optionsName=ini.getOptionsNames();


        this.distances=new ArrayList<>();
        int var0=0;

        for(int i=0;i<optionsName.size();i++) {

            ArrayList<Integer> var1 = new ArrayList<>();
            var1.add(i);

            distances.add(this.generateDistanceFunction(var1, null));

            if (ini.getOptionValue(optionsName.get(i))) var0++;

        }

        this.numberofOptions=var0;
    }




    public void seperateDataset(int start,int end) {

        training.clear();
        testing.clear();
        validation.clear();
        trainingID.clear();
        testingID.clear();
        validationID.clear();

        ArrayList<patent> temp_p=new ArrayList<>();
        ArrayList<String> temp_i=new ArrayList<>();

        int k=0;
        for(int i=0;i<this.patents.size();i++) {
            if (i>=start&&i<=end) {
                testing.add(patents.get(i));
                testingID.add(patentsID.get(i));
            } else {
                if (k<end-start+1) {
                    validation.add(patents.get(i));
                    validationID.add(patentsID.get(i));
                } else {
                    trainingID.add(patentsID.get(i));
                    training.add(patents.get(i));
                }
                k++;
            }
        }
    }




}
