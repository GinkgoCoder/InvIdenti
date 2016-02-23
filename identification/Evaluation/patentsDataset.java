package Evaluation;

import DatasetGenerator.PatentsGenerator;
import base.pair;
import base.patent;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/11/15.
 */
public class patentsDataset {

    //IniFile ini;
    String trainingDataPath;
    String trainingTextPath;
    String infoDataPath;
    int dataSize=Integer.MAX_VALUE;
    ArrayList<patent> patents;
    ArrayList<String> patentsID;



    //not used
    public patentsDataset(String dataPath,String infoPath,int dataSize,String IDType) {
        //ini=new IniFile();

        if (!dataPath.substring(dataPath.length()-3,dataPath.length()).equalsIgnoreCase(".db"))
        {
            this.trainingDataPath=dataPath+"/trainingData.db";
        } else {
            System.out.println(dataPath);
            this.trainingDataPath=dataPath;
        }
        this.trainingTextPath=dataPath+"/PatentsText/";
        infoDataPath=infoPath;
        this.dataSize=dataSize;
        pair<ArrayList<patent>, ArrayList<String>> var0;
        PatentsGenerator patentGenerator=new PatentsGenerator(infoDataPath,trainingTextPath,trainingDataPath);
        if(!IDType.equalsIgnoreCase("Benchmark")) {
            patentGenerator.setIDType(IDType);
            var0 = patentGenerator.getTrainingPatentsWithEstimatedID("TrainingData", 1, dataSize);
        }
        else {

            var0=patentGenerator.getTrainingPatents("TrainingData", 1, dataSize);

        }
        patents=var0.firstarg;
        patentsID=var0.secondarg;
        patentGenerator.closeDatabase();
    }




    //takes 5 arguments
    //constructor of patentsDataset calls PatentsGenerator class and its methos getTrainingPatents
    public patentsDataset(String dataPath,String infoPath,String textPath,int dataSize,String IDType) {
        //ini=new IniFile();

        if (!dataPath.substring(dataPath.length()-3,dataPath.length()).equalsIgnoreCase(".db"))
        {
            this.trainingDataPath=dataPath+"/trainingData.db";
        } else {
            System.out.println(dataPath);
            this.trainingDataPath=dataPath;
        }
        this.trainingTextPath=textPath+"/";
        infoDataPath=infoPath;
        this.dataSize=dataSize;

        //data structure for patent and patentID
        pair<ArrayList<patent>, ArrayList<String>> var0;

        PatentsGenerator patentGenerator=new PatentsGenerator(infoDataPath,trainingTextPath,trainingDataPath);
        if(!IDType.equalsIgnoreCase("Benchmark")) {
            patentGenerator.setIDType(IDType);
            var0 = patentGenerator.getTrainingPatentsWithEstimatedID("TrainingData", 1, dataSize);
        }
        else {
            var0=patentGenerator.getTrainingPatents("TrainingData", 1, dataSize);

        }
        patents=var0.firstarg;
        patentsID=var0.secondarg;
        patentGenerator.closeDatabase();
    }






    //just returns the patents and patentID already retrieved on object creation by the constructor
    public pair<ArrayList<patent>,ArrayList<String>>getPatents() {

        return new pair<>(patents,patentsID);
    }

    //will be used if we want patents and patentIDs from given start value to given end value
    public pair<ArrayList<patent>,ArrayList<String>>getPatents(int start,int end) {
        ArrayList<patent> var0=new ArrayList<>();
        ArrayList<String> var1=new ArrayList<>();
        for (int i=start;i<end;i++) {
            var0.add(patents.get(i));
            var1.add(patentsID.get(i));
        }
        return new pair<>(var0,var1);
    }

    //will be used if we want the patents and patentIDs from supplied indices only
    public pair<ArrayList<patent>,ArrayList<String>>getPatents(ArrayList<Integer> indexes) {
        ArrayList<patent> var0=new ArrayList<>();
        ArrayList<String> var1=new ArrayList<>();
        for (int i=0;i<patents.size();i++) {
            if (indexes.contains(i)) {
                var0.add(patents.get(i));
                var1.add(patentsID.get(i));
            }
        }
        return new pair<>(var0,var1);
    }
}
