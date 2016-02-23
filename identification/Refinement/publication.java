package Refinement;

import base.textOperator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;


/**
 * Created by leisun on 16/1/21.
 */
public class publication {

    private String title;
    private String ID;
    private String authorID;
    private String abstractText;
    private String affiliation;

    public publication(String path){


        try {
            File f=new File(path);
            Document doc= null;
            doc = Jsoup.parse(f,"UTF-8");
            this.ID=doc.getElementsByTag("ID").get(0).text();
            this.title=doc.getElementsByTag("title").get(0).text();

            Elements var0=doc.getElementsByTag("abstract");
            if (var0.size()>0) {
                abstractText=textOperator.getFirstWords(300,var0.get(0).text());
            } else {
                abstractText=null;
            }

            var0=doc.getElementsByTag("authorID");
            if (var0.size()>0) {
                this.authorID=var0.get(0).text();
            } else {
                this.authorID=null;
            }

            var0=doc.getElementsByTag("affiliation");

            if (var0.size()>0) {
                this.affiliation=var0.get(0).text();
            } else {
                this.affiliation=null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }





    }


    public String getTitle(){
        return this.title;
    }

    public String getAuthorID(){
        return this.authorID;
    }

    public String getID() {
        return this.ID;
    }

    public String getAbstractText() {
        return this.abstractText;
    }

    public String getAffiliation() {
        return this.affiliation;
    }


    public static void main(String[] args){
        File f=new File("/Users/leisun/Desktop/ThesisData/ES/PBMED/Publication/JOHNSON_TC/");
        File[] fs=f.listFiles();
        for (File var0:fs) {
            publication p = new publication(var0.getAbsolutePath());
            System.out.println(p.getID() + "|| " + p.getTitle() + "|| " + p.getAffiliation() + "|| " + p.getAuthorID());
            System.out.println(p.getAbstractText());
        }
    }
}
