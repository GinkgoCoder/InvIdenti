package Evaluation;

import base.pair;
import base.patent;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import preprocessing.ParameterLearning;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/11/15.
 */
public class Training {
    private ArrayList<patent> patents;
    private ArrayList<String> patentsID;
    private ParameterLearning method;

    public Training(ArrayList<patent> patents, ArrayList<String> patentsID, ParameterLearning method) {
        this.patents=patents;
        this.patentsID=patentsID;
        this.method=method;
    }

    public pair<AbstractDistance,Double> estimateParameter() {

        method.setInitialization(true);

        //Initializes Parameter Learning by setting the patents and patentsID arraylists
        method.initilize(patents,patentsID,true);
        CosDistance distance=(CosDistance)method.estimateDistanceFunction();
        return new pair<>(distance,method.getThreshold());
    }

}
