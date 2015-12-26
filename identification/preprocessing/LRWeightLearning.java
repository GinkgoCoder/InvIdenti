package preprocessing;

import base.pair;
import clustering.distancefunction.AbstractDistance;

import clustering.distancefunction.CosDistance;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by leisun on 15/11/5.
 */
public class LRWeightLearning extends ParameterLearning {

    private ArrayList<pair<int[],Double>> lrTrainingData=new ArrayList<>();

    public AbstractDistance estimateDistanceFunction() {
        System.out.println("Start Training");
        double start=System.currentTimeMillis();

        Boolean miniBatch=false;


        pair<pair<DoubleMatrix, DoubleMatrix>, pair<Integer, Integer>> batch_o=null ;
        int numberofTrainingData=patents.size()*(patents.size()-1)/2;


        ArrayList<Integer> ID1=new ArrayList<>();
        ArrayList<Integer> ID2=new ArrayList<>();

        for(int i=0;i<patents.size();i++) {
            ID1.add(i);
            ID2.add(i);
        }

       Collections.shuffle(ID1);
       Collections.shuffle(ID2);

        int starti=0;
        int startj=0;

        int maxIteration=2000;

        ArrayList<ArrayList<Double>> checkCovergence=new ArrayList<>();


        if (numberofTrainingData>=4000*7999) {
            miniBatch=true;
        } else {
            batch_o = geneerateAMiniBatchLRTrainingData(ID1, ID2, starti, startj, numberofOptions, numberofTrainingData);
        }
        /**
         * Training Data generating
         */
        //pair<DoubleMatrix,DoubleMatrix> result=this.logisticRTrainingDataGenerator();


        /**
         * Convergence check initilization
         */
        for(int i=0;i<1;i++) {
            checkCovergence.add(new ArrayList<Double>());
        }


        /**
         * Initilize the weights and threshold
         */





            double[][] var0 = new double[numberofOptions + 1][1];
            for (int i = 0; i < numberofOptions + 1; i++) {
                var0[i][0] = 1.0;
            }
            DoubleMatrix thetas = new DoubleMatrix(var0);


            thetas.transpose();

            // DoubleMatrix X=result.firstarg;
            // DoubleMatrix Y=result.secondarg;
            // ArrayList<pair<DoubleMatrix,DoubleMatrix>> xy=getBatches(X,Y,79*40);
            // X=null;
            // Y=null;


            double initialalpha = 8.1e-5*225*449;
            double alpha = initialalpha;
            double lamda = 5;

            boolean finish = false;
            int check = 0;
            double previous_error = 0;
            double initial_error = 0;



            for (int k = 0; k < maxIteration; k++) {

                starti = 0;
                startj = 0;


              //  for (int i = 0; i < numberofTrainingData; i += 10000) {

         /*
            int start=i;
            int end=i+10000-1;
            if (end>numberofTrainingData) end=numberofTrainingData;

            pair<DoubleMatrix,DoubleMatrix> p=generateDoubleMatrix(start,end);

*/

                    pair<DoubleMatrix, DoubleMatrix> p;
/*
                    if (miniBatch) {
                        pair<pair<DoubleMatrix, DoubleMatrix>, pair<Integer, Integer>> batch;
                        if (i + 10000 < numberofTrainingData) {
                            batch = geneerateAMiniBatchLRTrainingData(ID1, ID2, starti, startj, numberofOptions, 10000);
                        } else {

                            batch = geneerateAMiniBatchLRTrainingData(ID1, ID2, starti, startj, numberofOptions, numberofTrainingData - i);
                        }

                        p = batch.firstarg;
                        pair<Integer, Integer> indexes = batch.secondarg;

                        // System.out.println(p.firstarg.rows+" "+p.secondarg.rows);


                        starti = indexes.firstarg;
                        startj = indexes.secondarg + 1;

                        if (startj >= ID2.size()) {
                            starti++;
                            startj = 0;
                        }
                    } else {*/
                        p = batch_o.firstarg;
              //      }


                    DoubleMatrix thetas_t = new DoubleMatrix(thetas.toArray2());

/*
            for (int vari = 0; vari < p.firstarg.rows; vari++) {
                for (int varj = 0; varj < p.firstarg.columns; varj++) {
                    System.out.print(p.firstarg.get(vari, varj) + " ");
                }
                System.out.println();
            }

            for(int vari=0;vari<p.firstarg.rows;vari++) {
                System.out.println(p.secondarg.get(vari,0));
            }
*/

                    if (check == 0) {
                        DoubleMatrix varM1 = applyLogisticonData(p.firstarg, thetas_t);
                        double sum = 0;
                        for (int m = 0; m < p.secondarg.rows; m++) {

                            double temp = varM1.get(m, 0);


                            if (temp > 1) temp = 1;
                            if (temp < 0) temp = 0;

                            if (p.secondarg.get(m, 0) == 1) {
                                sum += Math.log(temp);
                            } else {
                                sum += Math.log(1 - temp);
                            }

                            //    sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);
                        }

                        initial_error = -sum;
                        previous_error = initial_error;

                        System.out.println("initial value:" + initial_error);
                    } else {
                        pair<DoubleMatrix, Double> var1 = updateWeights(p.firstarg, p.secondarg, thetas_t, alpha/p.firstarg.rows, lamda);
                        if (var1.secondarg / initial_error < 1e-5) break ;
                        if (2 * Math.abs(var1.secondarg - previous_error) / (var1.secondarg + previous_error + 1e-5) < 1e-5) {
                            finish=true;
                            break ;
                        }


                        thetas = var1.firstarg;
                        previous_error = var1.secondarg;

                        System.out.println(var1.secondarg + " " + check + " " + alpha);

                    }
                    check++;

/*
            if (check == 0) previous_error = var1.secondarg;


            if ((check > 0 && var1.secondarg <= previous_error) || check == 0) {

                thetas = var1.firstarg;


                //  if (check>0&&Math.abs(previous_error-var1.secondarg)/Math.abs(previous_error)<0.001) finish=true;


                previous_error = var1.secondarg;

                if (check < 50) {
                    for (int var2 = 0; var2 < checkCovergence.size(); var2++) {
                        checkCovergence.get(var2).add(var1.secondarg);
                    }
                } else {
                    for (int var2 = 0; var2 < checkCovergence.size(); var2++) {
                        checkCovergence.get(var2).remove(0);
                        checkCovergence.get(var2).add(var1.secondarg);
                        if (!finish) finish = getCovergence(checkCovergence);

                    }
                }

                System.out.print(thetas.get(2, 0) + " ");


                //System.out.println("a "+ previous_error+" "+var1.secondarg+" "+alpha);

                check++;
                //if (check>0) alpha=1.05*alpha;
            } else {
                //     System.out.println("b "+previous_error+" "+var1.secondarg+" "+alpha);
                alpha = alpha / 2;

            }
            if (finish) {

                // logger.error("Final Log Logistic:"+ var1.secondarg);
                break label;
            }

            if (!miniBatch) {

                break;
            }*/
                if (finish) break;
                }



         //   }

            logger.error("Final Log Logistic:" + previous_error);
            System.out.println("final iteration number:" + check);


            System.out.println();


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

            this.threshold = -weights[0];
            double end = System.currentTimeMillis();

            System.out.println("Time for learning:" + (end - start));


        return generateDistanceFunction(null,weight);

    }

    /**
     * Check the convergence
     * @param var0 the last 50 iteration weights values
     * @return
     */
    public boolean getCovergence(ArrayList<ArrayList<Double>> var0){
        double sum=0;


        for(ArrayList<Double> var1:var0) {
            sum+=Math.abs(Collections.max(var1)-Collections.min(var1))/Math.abs(Collections.max(var1));
        }




       // if (Math.abs(last-secondlast)/Math.max(last,secondlast)<0.01) return true; else return false;



        if (sum<0.01*var0.size()) return true; else return false;
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
    public pair<DoubleMatrix,Double> updateWeights(DoubleMatrix X,DoubleMatrix Y,DoubleMatrix thetas,double alpha,double lamda) {
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
     * @param X Similarity Matrix
     * @param Y target value matrix
     * @param batchsize batch size
     * @return the mini-Batch similarity matrices and target value matrices.
     */

    ArrayList<pair<DoubleMatrix,DoubleMatrix>> getBatches(DoubleMatrix X,DoubleMatrix Y,int batchsize) {
        ArrayList<pair<DoubleMatrix, DoubleMatrix>> result = new ArrayList<>();

        if (X.rows <= batchsize) {
            result.add(new pair<>(X, Y));
            return result;
        }

        int totallines = X.rows;

        for (int i = 0; i < totallines; i += batchsize) {
            int begin = i;
            int end = i + batchsize - 1;
            if (end >= totallines) end = totallines - 1;
            int[] var = new int[end - begin + 1];
            for (int var0 = 0; var0 <= (end - begin); var0++) {
                var[var0] = begin + var0;
            }
            //logger.error(i);
            result.add(new pair<>(new DoubleMatrix(X.getRows(var).toArray2()), new DoubleMatrix(Y.getRows(var).toArray2())));
        }

        return result;

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
     * Output a matrix
     * @param x the matrix
     * @param name the name of the matrix
     */
    public void outputMatrix(DoubleMatrix x,String name) {
        logger.error("Matrix Name:" +name);
        int var0=0;
        for (int i=0;i<x.rows;i++) {
            String temp="";
            for(int j=0;j<x.columns;j++) {

                    temp+=x.get(i,j)+" ";


            }
            logger.error(temp);
        }



    }



    public pair<pair<DoubleMatrix,DoubleMatrix>,pair<Integer,Integer>> geneerateAMiniBatchLRTrainingData(ArrayList<Integer>ID1,ArrayList<Integer>ID2,int starti,int startj,int numberofFeatures,int batchsize){
        boolean firsttry=true;

        double[][] x=new double[batchsize][numberofOptions+1];
        double[][] y=new double[batchsize][1];
        int index=0;

        int endi,endj;
        endi=endj=ID1.size();
        pair<Integer,Integer> continueIndex;
        label:
        for(int i=starti;i<ID1.size();i++) {
            for(int j=0;j<ID2.size();j++) {
                if(firsttry) {
                    j=startj;
                    firsttry=false;
                }
                if(ID2.get(j)>ID1.get(i)) {
                    x[index][0]=1.0;
                    if (this.patentsID.get(ID1.get(i)).equalsIgnoreCase(this.patentsID.get(ID2.get(j)))) {
                       y[index][0]=1.0;
                           } else {
                        y[index][0] = 0.0;


                    }


                    int var2=1;


                    for(int m=0;m<optionsName.size();m++) {
                        if (ini.getOptionValue(optionsName.get(m))) {
                            x[index][var2]=distances.get(m).distance(patents.get(ID1.get(i)), patents.get(ID2.get(j)));
                            var2++;
                        }


                    }

                    index++;
                    if(index>=batchsize) {
                        endi=i;
                        endj=j;

                        break label;
                    }
                }
            }
        }
        System.out.println();
        continueIndex=new pair<>(endi,endj);

        return new pair<>(new pair<>(new DoubleMatrix(x),new DoubleMatrix(y)),continueIndex);
    }



    /**
     * Generating the training data of the matrix type.
     */
    public void generateLRTraininngData() {

        /**
         * Clean the Training Data
         */



       this.lrTrainingData.clear();


        for (int i = 0; i < this.patents.size() - 1; i++) {
            for (int j = i + 1; j < this.patents.size(); j++) {
                int[] tempint = new int[2];

                tempint[0] = i;
                tempint[1] = j;

                double result;

                if (this.patentsID.get(i).equalsIgnoreCase(this.patentsID.get(j))) {
                    result = 1.0;
                } else {
                    result = 0.0;
                }

                 this.lrTrainingData.add(new pair<>(tempint, result));

                }
            }

    }


    /**
     * Generating a trainig data and transformed into a String
     * @param var0 the two patent number and the target value
     * @return the transformed string.
     */
    public String generateAData(pair<int[],Double> var0) {
        String temp=1.0+";";


        for(int j=0;j<optionsName.size();j++) {
            if (ini.getOptionValue(optionsName.get(j))) {

                temp+=distances.get(j).distance(patents.get(var0.firstarg[0]), patents.get(var0.firstarg[1]))+";";

            }

        }

        temp+=var0.secondarg+"\n";

        return temp;
    }



    /** Generating the training data of the matrix form
     * @return the similarity matrix and the target value vector
     */
    public pair<DoubleMatrix,DoubleMatrix> logisticRTrainingDataGenerator() {



        double[][] var0=new double[this.lrTrainingData.size()][numberofOptions+1];
        double[][] var1=new double[this.lrTrainingData.size()][1];

        int i=0;
        double sum=0;
        for(pair<int[],Double> p:this.lrTrainingData) {
            var0[i][0]=1.0;
            int var2=1;

            for(int j=0;j<optionsName.size();j++) {
                if (ini.getOptionValue(optionsName.get(j))) {

                    var0[i][var2]=distances.get(j).distance(patents.get(p.firstarg[0]), patents.get(p.firstarg[1]));

                    var2++;
                }

            }
            var1[i][0]=p.secondarg;
            i++;
        }

        DoubleMatrix X=new DoubleMatrix(var0);

        DoubleMatrix Y=new DoubleMatrix(var1);


        System.out.println("Finished Generating!");

        return new pair<>(X, Y);
    }
}