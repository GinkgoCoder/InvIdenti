package preprocessing;

/**
 * Created by leisun on 15/12/7.
 *
 * @deprecated not currently in use in the module/project
 */
public class cache {

/*
    package preprocessing;

    import base.pair;
    import clustering.distancefunction.AbstractDistance;

    import org.jblas.DoubleMatrix;
    import org.jblas.MatrixFunctions;

    import java.io.File;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Collections;


    public class LRWeightLearning extends ParameterLearning {

        private ArrayList<pair<int[],Double>> lrTrainingData=new ArrayList<>();

        public AbstractDistance estimateDistanceFunction() {

            this.generateLRTraininngData();
            int maxIteration=1000;

            ArrayList<ArrayList<Double>> checkCovergence=new ArrayList<>();



            pair<DoubleMatrix,DoubleMatrix> result=this.logisticRTrainingDataGenerator();

            for(int i=0;i<result.firstarg.columns;i++) {
                checkCovergence.add(new ArrayList<Double>());
            }

            double[][] var0=new double[numberofOptions+1][1];
            for(int i=0;i<numberofOptions+1;i++) {
                var0[i][0]=1.0;
            }

            double previous_error=Double.MAX_VALUE;
            double previous_e=0;
            // ArrayList<Double> errors=new ArrayList<>();
            DoubleMatrix thetas=new DoubleMatrix(var0);
            thetas.transpose();
            outputMatrix(thetas.transpose(),"asd");
            DoubleMatrix X=result.firstarg;
            DoubleMatrix Y=result.secondarg;
            ArrayList<pair<DoubleMatrix,DoubleMatrix>> xy=getBatches(X,Y,79*40);
            X=null;
            Y=null;

            logger.warn("Batch number: "+xy.size());

            double alpha=0.1*79*40;
            double lamda=0;


            boolean finish=false;
            int check=0;

            for(int k=0;k<maxIteration;k++) {



                for(pair<DoubleMatrix,DoubleMatrix> p:xy) {



                    DoubleMatrix thetas_p = new DoubleMatrix(thetas.toArray2());
                    //outputMatrix(thetas.transpose(),"before");

                    pair<DoubleMatrix,Double> var1=updateWeights(p.firstarg,p.secondarg,thetas,alpha/p.firstarg.rows,lamda);

                    thetas=var1.firstarg;
                    previous_error=var1.secondarg;

                    // outputMatrix(thetas.transpose(),"after");

                    //   System.out.println();

                    //DoubleMatrix thetas_t = new DoubleMatrix(thetas_p.toArray2());

                    //thetas_t = MatrixFunctions.absi(thetas_t);
                    //thetas_p = MatrixFunctions.absi(thetas_p.subi(thetas));
                    //thetas_p.divi(thetas_t);

                    if (check<50) {
                        for(int var2=0;var2<checkCovergence.size();var2++) {
                            checkCovergence.get(var2).add(thetas.get(var2,0));
                        }
                    } else {
                        for(int var2=0;var2<checkCovergence.size();var2++) {
                            checkCovergence.get(var2).remove(0);
                            checkCovergence.get(var2).add(thetas.get(var2,0));
                            finish=getCovergence(checkCovergence);
                        }
                    }

                    check++;




           if (thetas_p.sum() / thetas_p.rows < 0.001) {
                System.out.println();
                System.out.println(k);
                finish=true;
                break;
            }

                    if (finish) {

                        System.out.println("final iteration number:"+check);
                        break;
                    }
                }
                if (finish) break;
            }

            System.out.println();
            System.out.println("Final correcteness: "+previous_error);


            double[] weights=thetas.toArray();

            ArrayList<Double> weight=new ArrayList<>();
            int i=1;

            for(int j=0;j<optionsName.size();j++) {
                if(ini.getOptionValue(optionsName.get(j))) {
                    //logger.warn(optionsName.get(j)+weights[i]);
                    weight.add(weights[i]);
                    i++;
                } else {
                    weight.add(0.0);
                }

            }

            this.threshold=-weights[0];
            return generateDistanceFunction(null,weight);
        }


        public boolean getCovergence(ArrayList<ArrayList<Double>> var0){
            double sum=0;

            for(ArrayList<Double> var1:var0) {
                sum+= Collections.max(var1)-Collections.min(var1);
            }


            //System.out.print(sum+" ");

            if (sum<0.01*var0.size()) return true; else return false;
        }

        public pair<DoubleMatrix,Double> updateWeights(DoubleMatrix X,DoubleMatrix Y,DoubleMatrix thetas,double alpha,double lamda) {
            DoubleMatrix varM1=applyLogisticonData(X,thetas);
            double error=0;
            double sum=0;
            for (int m = 0; m < Y.rows; m++) {

                double temp=varM1.get(m,0);
                if (temp>1) temp=1;
                if (temp<0) temp=0;

                sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);

            }

            System.out.print(-sum+" ");
            varM1.subi(Y);


            DoubleMatrix error_t=new DoubleMatrix(varM1.toArray2());





            MatrixFunctions.absi(error_t);



            DoubleMatrix errorM=new DoubleMatrix(varM1.toArray2());
            errorM=MatrixFunctions.absi(errorM);
            error=errorM.sum()/errorM.rows;


            varM1 = X.transpose().mmul(varM1);


            // DoubleMatrix thetas1 = new DoubleMatrix(thetas.toArray2());

            //thetas1 = thetas1.put(0, 0, 0);


            varM1.muli(alpha);


            // thetas1.muli(lamda * alpha);

            thetas.subi(varM1);
            //  thetas.subi(thetas1);
            // outputMatrix(thetas.transpose(),"final result");

            return new pair<>(thetas,error);

        }

        ArrayList<pair<DoubleMatrix,DoubleMatrix>> getBatches(DoubleMatrix X,DoubleMatrix Y,int batchsize){
            ArrayList<pair<DoubleMatrix,DoubleMatrix>> result=new ArrayList<>();

            if (X.rows<=batchsize) {
                result.add(new pair<>(X,Y));
                return result;
            }

            int totallines=X.rows;

            for(int i=0;i<totallines;i+=batchsize) {
                int begin=i;
                int end=i+batchsize-1;
                if  (end>=totallines) end=totallines-1;
                int[] var=new int[end-begin+1];
                for(int var0=0;var0<=(end-begin);var0++) {
                    var[var0]=begin+var0;
                }
                //logger.error(i);
                result.add(new pair<>(new DoubleMatrix(X.getRows(var).toArray2()),new DoubleMatrix(Y.getRows(var).toArray2())));
            }

            return result;

        }


        public DoubleMatrix applyLogisticonData(DoubleMatrix X,DoubleMatrix thetas) {




            DoubleMatrix varM1 = new DoubleMatrix(X.transpose().toArray2());


            varM1 = varM1.transpose().mmul(thetas);




            DoubleMatrix varM2 = new DoubleMatrix(varM1.rows, varM1.columns);

            varM2.subi(varM1);


            MatrixFunctions.expi(varM2);


            varM2.addi(1);

            // outputMatrix(varM2,"asd");
            //

            DoubleMatrix varM3 = new DoubleMatrix(varM2.rows, varM2.columns);
            varM3.addi(1);

            varM3.divi(varM2);

            // outputMatrix(varM3.transpose(),"check");

            return varM3;

        }

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

        public void generateLRTraininngData() {
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



    public void writeTrainingData(pair<int[],Double> result) {
        String temp=1.0+"|";
        int var2=1;

        for(int j=0;j<optionsName.size();j++) {
            if (ini.getOptionValue(optionsName.get(j))) {

                // var0[i][var2]=distances.get(j).distance(patents.get(p.firstarg[0]), patents.get(p.firstarg[1]));
                temp+=distances.get(j).distance(patents.get(p.firstarg[0]), patents.get(p.firstarg[1]))+"|";

                var2++;
            }

        }
    }


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
   */

}
/**
 * //**
 *
*/
