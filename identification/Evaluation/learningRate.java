package Evaluation;

import base.pair;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

/**
 * Created by leisun on 15/12/22.
 */
public class learningRate {


    public learningRate(int numberofOptions,DoubleMatrix X,DoubleMatrix Y) {

        double maxStep=Double.MIN_VALUE;

        double[][] var0 = new double[numberofOptions + 1][1];
        for (int i = 0; i < numberofOptions + 1; i++) {
            var0[i][0] = 1.0;
        }

        DoubleMatrix thetas = new DoubleMatrix(var0);


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


        System.out.println("error"+initial_error);

        for(double step=0;step<20;step+=0.1){

            DoubleMatrix thetas_t=new DoubleMatrix(thetas.toArray2());
            double error=updateWeights(X,Y,thetas_t,step/X.rows,0).secondarg;
            System.out.println(step+" "+(initial_error-error));
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
}
