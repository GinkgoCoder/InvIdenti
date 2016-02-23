package base;

import org.apache.mahout.math.matrix.DoubleMatrix2D;
import preprocessing.USPTOSearch;

/**
 * Created by leisun on 15/12/1.
 */
public class patentTF {
    private String patent_number;
    private String abs;
    private String claims;
    private String description;
    /**Text vector representation of the patent**/
    private double[] td_abs;
    private double[] td_claims;
    private double[] td_des;
    private double[] td_title;
    /**Attributes of the patent**/
    private String author;
    private String firstName;
    private String lastName;
    private String title;
    private String category;
    private String assignee;
    private String asgNum;
    /**Inventor information**/
    private String lat,lng;
    private String coAuthor;
    private String country;
    private String ID;

    public patentTF(String patent_number,String abs,String claims,String description,String title,String category,String Assignee,String lastname,String firstname,String lat,String lng,String coAuthor,String country,String asgNum)
    {
        this.patent_number=patent_number;
        this.abs=abs;
        this.claims=claims;
        this.description=description;
        this.title=title;
        this.category=category;
        this.assignee=Assignee;
        this.author=firstname+" "+lastname;
        this.firstName=firstname;
        this.lastName=lastname;
        this.lat=lat;
        this.lng=lng;
        this.coAuthor=coAuthor;
        this.country=country;
        this.asgNum=asgNum;

    }

    public patentTF(String patent_number,String category,String assignee,String author){
        this.patent_number=patent_number;
        this.category=category;
        this.assignee=assignee;
        this.author=author;

        USPTOSearch searchPatent=new USPTOSearch(patent_number);
        this.setAbs(searchPatent.getAbs());
        this.setClaims(searchPatent.getClaims());
        this.setDescription(searchPatent.getDescription());
        this.setTitle(searchPatent.getTitle());

    }


    /**Set patent attributes value**/
    public void setAuthor(String author)
    {
        this.author=author;
    }

   

    public void setTd_abs(double[] M)
    {
        this.td_abs=M;
    }

    public void setTd_des(double[] M)
    {
        this.td_des=M;
    }

    public void setTd_claims(double[] M) {this.td_claims=M;}

    public void setTd_title(double[] M) { this.td_title=M;}
    
    public void setAssignee(String assignee){this.assignee=assignee;}

    public void setCategory(String category){this.assignee= category;}

    public void setAbs(String abs) {
        this.abs=abs;
    }

    public void setClaims(String claims){
        this.claims=claims;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public void setLat(String lat) {this.lat=lat;}

    public void setLng(String lng) {this.lng=lng;}

    public void setCountry(String country) {this.country=country;}

    public void setCoAuthor(String coAuthor) {this.coAuthor=coAuthor;}

    public void setID(String ID) {this.ID=ID;}

    /**Get the attribute value of the patent**/

    public String getAuthor()
    {
        return this.author;
    }
    
    public double[] getTd_abs()
    {
        return td_abs;
    }

    public double[] getTd_des()
    {
        return td_des;
    }

    public double[] getTd_claims()
    {
        return td_claims;
    }

    public double[] getTd_title()
    {
        return td_title;
    }

    public String getPatent_number()
    {
        return this.patent_number;
    }

    public String getAbs()
    {
        return this.abs;
    }

    public String getClaims()
    {
        return this.claims;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getTitle() {return this.title;}

    public String getAssignee(){return this.assignee;}

    public String getCategory(){return this.category;}

    public String getLat() {return this.lat;}

    public String getLng() {return this.lng;}

    public String getCoAuthor() {return this.coAuthor;}

    public String getCountry() {return this.country;}

    public String getFirstName() {return this.firstName;}

    public String getLastName() {return this.lastName;}

    public String getAsgNum() {return this.asgNum;}

    public String getID() {return this.ID;}

    public String toString() {
        String str="Patent Number: "+this.patent_number+"\n";
        str+="Author: "+this.getAuthor()+"\n";
        str+="Country: "+ this.getCountry()+"\n";
        str+="Abstract: "+this.getAbs()+"\n";
        str+="Claims: "+this.getClaims()+"\n";
        str+="Description: "+this.getDescription()+"\n";
        str+="Assignee: "+this.getAssignee()+"\n";
        str+="Category: "+this.getCategory()+"\n";
        str+="Lat: "+this.getLat()+"\n";
        str+="Lng: "+this.getLng()+"\n";
        str+="coAuthor: "+this.getCoAuthor();
        str+="title: "+this.getTitle();
        return str;
    }
}
