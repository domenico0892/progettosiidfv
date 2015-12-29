package it.uniroma3.model;

import java.util.Set;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import it.uniroma3.persistence.MongoConnection;

public class MyCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
                                                           + "|png|mp3|mp3|zip|gz))$");
    
    private MongoCollection<Document> coll;
    
    public MyCrawler () {
    	super();
    	MongoConnection m = new MongoConnection();
    	MongoDatabase d = m.getMongoClient().getDatabase("prova_crawler");
    	this.coll = d.getCollection("prova_crawler");
    	
    }
    

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
     @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
         return !FILTERS.matcher(href).matches()
                && href.startsWith("http://www.ansa.it");
     }

     /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
     @Override
     public void visit(Page page) {
         String url = page.getWebURL().getURL();
         System.out.println("URL: " + url);

         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String text = htmlParseData.getText(); //qui ci va la chiamata a boilerpipe
             //costruzione delle frasi
             //match -> SingleResult[] {URL, content, data, ...}
             SingleResult s = new SingleResult (url, text);
             s.addEntity("prova1");
             s.addEntity("prova2");
             this.coll.insertOne(s.singleResult2Document());
             String html = htmlParseData.getHtml();
             Set<WebURL> links = htmlParseData.getOutgoingUrls();
             System.out.println("Text length: " + text.length());
             System.out.println("Html length: " + html.length());
             //System.out.println(html.toString().trim());
             //System.out.println(text.toString());
             System.out.println("Number of outgoing links: " + links.size());
         }
    }
}