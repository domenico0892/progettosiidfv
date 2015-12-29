package it.uniroma3.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import it.uniroma3.persistence.MongoConnection;

public class MyCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
                                                           + "|png|mp3|mp3|zip|gz))$");
    
    //insert the keywords of the query
    public static final String[] KEYWORDS = {"Rugani", "Roma"};
    
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
        	 String text = htmlParseData.getText(); 
/*          qui ci va la chiamata a boilerpipe
            costruzione delle frasi
            match -> SingleResult[] {URL, content, data, ...}                        */
        	 try {
        		 listaFrasiMatch(new URL(url));
        	 } catch (MalformedURLException | BoilerpipeProcessingException e) {
        		 e.printStackTrace();
        	 }
             
//             SingleResult s = new SingleResult (url, text);
//             s.addEntity("prova1");
//             s.addEntity("prova2");
//             this.coll.insertOne(s.singleResult2Document());
//             String html = htmlParseData.getHtml();
//             Set<WebURL> links = htmlParseData.getOutgoingUrls();
//             System.out.println("Text length: " + text.length());
//             System.out.println("Html length: " + html.length());
//             System.out.println(html.toString().trim());
//             System.out.println(text.toString());
//             System.out.println("Number of outgoing links: " + links.size());
     	}
 	}
     
     //create a collection of singleResult 
     public void listaFrasiMatch (URL url) throws BoilerpipeProcessingException {
 		List<String> phrases= new ArrayList<String>();
 		String sentence = new String();
 		SingleResult sr = new SingleResult(url.toString(), sentence);
 		int breakPoint, punto, aCapo;
 		breakPoint = 0; punto = 0; aCapo = 0;
 		String text = ArticleExtractor.INSTANCE.getText(url).trim();
 		while (breakPoint < text.length()){
 			punto = text.indexOf(". ", breakPoint);
 			aCapo = text.indexOf("\n", breakPoint);
// 				System.out.println("breakPoint "+breakPoint+" aCapo "+aCapo+" Punto "+punto);
 			if (punto < aCapo || (punto != -1 && aCapo == -1)) {
 				sentence = text.substring(breakPoint, text.indexOf(".",breakPoint)).trim();
 				breakPoint = text.indexOf('.',breakPoint)+1;
 			}
 			else if (aCapo < punto || (aCapo != -1 && punto == -1)) {
 				sentence = text.substring(breakPoint, text.indexOf("\n",breakPoint)).trim();
 				breakPoint = text.indexOf('\n',breakPoint)+1;
 			}
 			else {
 				sentence = text.substring(breakPoint, text.length()-1).trim();
 				breakPoint = text.length();
 			}
 			phrases.add(sentence);
// 			sr = new SingleResult(url.toString(), sentence);
// 			sr.setEntity(matchEntity(sentence));
// 			this.coll.insertOne(sr.singleResult2Document());
// 			for (String p : entity){
// 				System.out.print (p+"  ");
// 			}
// 			System.out.println(sentence);
 		}
 		for (String s : phrases) {
 			System.out.println (phrases.indexOf(s)+") "+s);
 		}
 	}
 	
    //returns a list of the entity of the sentence
 	public static List<String> matchEntity (String frase){
 		List<String> entity = new ArrayList<String>();
 		for (String kw : KEYWORDS) {
 			if (frase.toLowerCase().contains(kw.toLowerCase())){
 				entity.add(kw);
 			}
 		}
 		return entity;
 	}		
}