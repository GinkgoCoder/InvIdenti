package preprocessing;

import Evaluation.trainingDataMatrix;
import base.pair;
import base.patent;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import org.carrot2.core.LanguageCode;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by leisun on 16/1/12.
 */

/**
 * Performs Logistic Regression on Training Patents to generate weights of features and the threshold value
 */
public class LRWithBoldDriver extends ParameterLearning{

    ArrayList<patent> training=new ArrayList<>();
    ArrayList<String> trainingID=new ArrayList<>();
    ArrayList<patent> validation=new ArrayList<>();
    ArrayList<String> validationID=new ArrayList<>();

    /**
     * Implements the <code>estimateDistanceFunction</code> method of the Parameter Learning class
     * calculates the weights of the features and threshold using Logistic Regression with Bold Driver
     * @return the computed weights of features and threshold of classification
     */
    public AbstractDistance estimateDistanceFunction(){

        seperateDataset();
        logger.info("Generating validation matrix...");
        System.out.println(validation.size());
        pair<DoubleMatrix,DoubleMatrix> validations=new trainingDataMatrix(validation,validationID,false).getPatents_Matrices();

        DoubleMatrix validationsFirstarg=validations.firstarg;
        DoubleMatrix validationsSecondarg=validations.secondarg;

        double[][] var0 = new double[numberofOptions + 1][1];
        for (int i = 0; i < numberofOptions + 1; i++) {
            var0[i][0] = 0.0;
        }
        DoubleMatrix thetas = new DoubleMatrix(var0);           //weights and threshold vector

        int maxIteration = 5000;                                //maximum number of iterations for training
        double alpha = 1.0;                                     //initial learning rate
        double lambda = 0;                                      //initial regularization parameter

        /**
         * Generates pairs of training data matrices for Logistic Regression Training
         */
        logger.info("Generating training matrix...");
        pair<DoubleMatrix,DoubleMatrix> trainingMatrice=new trainingDataMatrix(training,trainingID,false).getPatents_Matrices();
        //note: naming standards error: trainingDataMatrix

        double errorForTraining=calculateTheError(trainingMatrice.firstarg,trainingMatrice.secondarg,thetas);
        double previous_error=errorForTraining;
        double previoud_error_v=calculateTheError(validationsFirstarg,validationsSecondarg,thetas);

        logger.warn("Start the training...");

        label:
        for(int k=0;k<maxIteration;k++) {

            DoubleMatrix thetas_t = new DoubleMatrix(thetas.toArray2());
            thetas_t = updateWeights(trainingMatrice.firstarg, trainingMatrice.secondarg,thetas_t, alpha / trainingMatrice.firstarg.rows, lambda);
            errorForTraining=calculateTheError(trainingMatrice.firstarg,trainingMatrice.secondarg,thetas_t);

            if (previous_error>errorForTraining) {

                double errorforValidation=calculateTheError(validationsFirstarg,validationsSecondarg,thetas_t);
                System.out.println(errorForTraining+" "+" "+errorforValidation+" "+errorforValidation);
                // outputMatrix(thetas_t.transpose(),"as");

                /**
                 * Bold Driver Method for Logistic Regression
                 */
                if (errorForTraining<1e-10||( previous_error - errorForTraining) < 1e-10|| Math.abs((previous_error - errorForTraining) / previous_error)<1e-10||previoud_error_v<errorforValidation||alpha<1e-10) {
                    previous_error = errorForTraining;
                    previoud_error_v=errorforValidation;
                    thetas = new DoubleMatrix(thetas_t.toArray2());
                    break label;
                }
                alpha*=1.1;
                previous_error=errorForTraining;
                previoud_error_v=errorforValidation;
                thetas = new DoubleMatrix(thetas_t.toArray2());

            }   else {
                alpha/=2;
                k--;
            }
        }

        double[] weights = thetas.toArray();
        ArrayList<Double> weight = new ArrayList<>();
        int i = 1;

        for (int j = 0; j < optionsName.size(); j++) {
            if (ini.getOptionValue(optionsName.get(j))) {
                weight.add(weights[i]);
                i++;
            }
            else {
                weight.add(0.0);
            }
        }
        this.threshold=-weights[0];
        logger.debug("Finish the training...");
        return (this.generateDistanceFunction(null,weight));
    }

    /**
     * Preprocess the patents
     * @return the preprocessed patents after stop-word removal, stemming, term-frequency calculation and singular value decomposition
     */
    protected ArrayList<patent> preprocess(ArrayList<patent> patents) {

        double start=System.currentTimeMillis();
        patentPreprocessingTF preprocess = new patentPreprocessingTF(patents);
        preprocess.setLanguage(LanguageCode.ENGLISH);
        preprocess.preprocess();
        double end=System.currentTimeMillis();
        System.out.println("Preprocessing Time"+(end-start));
        return patents;
    }

    /**
     * Calculate the error of the training or the validation
     * @param X feature matrix
     * @param Y target matrix
     * @param thetas weights vector
     * @return training error for the Logistic Regression
     */
    public double calculateTheError(DoubleMatrix X,DoubleMatrix Y,DoubleMatrix thetas){

        DoubleMatrix varM=applyLogisticonData(X, thetas);
        double sum = 0;
        for (int m = 0; m < Y.rows; m++) {
            double temp = varM.get(m, 0);
            if (temp > 1) temp = 1;
            if (temp < 0) temp = 0;
            sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);
        }
        return (-sum)/X.rows;
    }

    /**
     * output the matrix
     * @param x matrix
     * @param name matrix name
     */
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

    /**
     *
     * @param path the path of the file where the <code>str</code> is to be written
     * @param follow indicates whether the file has to be appended or not
     * @param str the actual string that has to be written to the file
     */
    public void storeText(String path,boolean follow,String str){

        if (follow) {
            try {
                FileWriter w=new FileWriter(path,follow);
                w.write(str);
                w.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
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
    public DoubleMatrix updateWeights(DoubleMatrix X, DoubleMatrix Y, DoubleMatrix thetas, double alpha, double lamda) {

        DoubleMatrix varM1=applyLogisticonData(X,thetas);
        varM1.subi(Y);
        varM1 = X.transpose().mmul(varM1);
        DoubleMatrix thetas1 = new DoubleMatrix(thetas.toArray2());
        thetas1 = thetas1.put(0, 0, 0);
        varM1.muli(alpha);
        thetas1.muli(lamda * alpha);
        thetas.subi(varM1);
        thetas.subi(thetas1);
        return thetas;
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
     * overrides method in Parameter Learning class
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
                }
                else {
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
     * Separates the patents dataset into 80% training data and 20% validation data
     */
    public void seperateDataset() {

        training.clear();
        validation.clear();
        trainingID.clear();
        validationID.clear();

        ArrayList<Integer> index=new ArrayList<>();
        for(int i=0;i<patents.size();i++) {
            index.add(i);
        }
        int k=0;
        Collections.shuffle(index);
        for(int i=0;i<index.size();i++) {
            if (k<index.size()*0.2) {
                validation.add(patents.get(index.get(i)));
                validationID.add(patentsID.get(index.get(i)));
            }
            else{
                training.add(patents.get(index.get(i)));
                trainingID.add(patentsID.get(index.get(i)));
            }
            k++;
        }
        logger.info("Start to preprocessing the patents text...");
        training=preprocess(training);
        validation=preprocess(validation);
        System.out.println("Training Data Size:"+training.size());
        System.out.println("Testing Data Size:"+validation.size());
    }
}
