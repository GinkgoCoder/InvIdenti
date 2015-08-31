package preprocessing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by sunlei on 15/9/1.
 */
public class USPTOSpider implements PageProcessor
{
    private String abs;//Get the abstract of the patent

    private String claims;//Get the claims of the patent

    private String description;//Get the description of the patent

    private Site site=Site.me().setRetryTimes(3).setRetryTimes(100);

    public void process(Page page)
    {



        Document doc= Jsoup.parse(page.getHtml().toString());

        Elements e=doc.getElementsMatchingOwnText("Abstract");

        if (e==null||e.size()==0) abs=null; else this.abs=e.first().parent().nextElementSibling().ownText();
        e=doc.getElementsMatchingOwnText("Claims");

        if (e==null||e.size()==0) claims=null; else
        {
            Element current=e.first();

            while(current.nextSibling()==null) current=current.parent();
            this.claims = getTextBetweenTwoTags(current.parent(),"hr","hr");
        }
        e=doc.getElementsMatchingOwnText("Description");
        if (e==null||e.size()==0) claims=null; else
        {
            Element current=e.first();

            while(current.nextSibling()==null) current=current.parent();
            this.description = getTextBetweenTwoTags(current.parent(),"hr","hr");
        }
    }

    //Get texts between start tag and end tag.
    private static String getTextBetweenTwoTags(Element e,String start,String end)
    {
        String str="";

        Node current=e.nextSibling();

        while(!current.nodeName().equalsIgnoreCase(start))
        {
            current=current.nextSibling();
        }

        current=current.nextSibling();

        while(!current.nodeName().equalsIgnoreCase(end))
        {
            if (current instanceof TextNode) str+=((TextNode) current).text();
            current=current.nextSibling();
        }

        return str;
    }

    public String getAbs()
    {
        return abs;
    }

    public String getClaims()
    {
        return claims;
    }

    public String getDescription()
    {
        return description;
    }

    public Site getSite()
    {
        return site;
    }
}
