package it.uniroma3.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.ArticleSentencesExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import it.uniroma3.persistence.MongoConnection;

public class MyCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
			+ "|png|mp3|mp3|zip|gz))$");

	//insert the keywords of the query
	public static final String[] KEYWORDS = {"politica","renzi"};

	private MongoCollection<Document> coll;
	private WebDriver driver;

	public MyCrawler () {
		super();
		MongoConnection m = new MongoConnection();
		MongoDatabase d = m.getMongoClient().getDatabase("pagine");
		this.coll = d.getCollection("pagine");
		
		Capabilities caps = new DesiredCapabilities();
		((DesiredCapabilities) caps).setJavascriptEnabled(true);                
		((DesiredCapabilities) caps).setCapability("takesScreenshot", true);  
		((DesiredCapabilities) caps).setCapability(
				PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				"/Users/Domenico/Development/Librerie/phantomjs-2.0.1-macosx/bin/phantomjs"
				);
		this.driver = new  PhantomJSDriver(caps);
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
		String ref = referringPage.getWebURL().getURL();
		URL refUrl;
		try {
			refUrl = new URL(ref);
			String match = "http://" + refUrl.getHost().toLowerCase();
			String href = url.getURL().toLowerCase();
			return !FILTERS.matcher(href).matches()
					&& href.startsWith(match);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 * @throws BoilerpipeProcessingException 
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		System.out.println("URL: " + url);
		if (page.getParseData() instanceof HtmlParseData) {
			//HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			//String html = htmlParseData.getHtml();
			this.driver.get(url);
			URL url_parsed;
			try {
				url_parsed = new URL (url);
				Document doc = new Document();
				doc.append("url", url);
				doc.append("host", url_parsed.getHost());
				doc.append("html", this.driver.getPageSource());
				this.coll.insertOne(doc);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
			
			
			
			//System.out.println(html);
//			String text = htmlParseData.getText(); 
			/*
			try {
				//listaFrasiMatch(new URL(url));
				String text = ArticleSentencesExtractor.INSTANCE.getText(url);
				System.out.println("testo:" + text);
			} catch (BoilerpipeProcessingException e) {
				e.printStackTrace();
			}
			
*/
//	         String html = htmlParseData.getHtml();
//	         Set<WebURL> links = htmlParseData.getOutgoingUrls();
//	         System.out.println("Text length: " + text.length());
//	         System.out.println("Html length: " + html.length());
//	         System.out.println(html.toString().trim());
//	         System.out.println(text.toString());
//	         System.out.println("Number of outgoing links: " + links.size());

	//create a collection of singleResult 
	public void listaFrasiMatch (URL url) throws BoilerpipeProcessingException {
//		List<String> phrases= new ArrayList<String>();
		String sentence;
		SingleResult sr;
		int breakPoint, punto, aCapo;
		breakPoint = 0; punto = 0; aCapo = 0;
		String text = ArticleExtractor.INSTANCE.getText(url).trim();
		while (breakPoint < text.length()){
			punto = text.indexOf(". ", breakPoint);
			aCapo = text.indexOf("\n", breakPoint);
//			System.out.println("breakPoint "+breakPoint+" aCapo "+aCapo+" Punto "+punto);
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
//			phrases.add(sentence);
			List<String> entity = matchEntity(sentence);
			if (!entity.isEmpty()) {
				sr = new SingleResult(url.toString(), sentence);
				sr.setEntity(matchEntity(sentence));
				this.coll.insertOne(sr.singleResult2Document());
				System.out.println("SENTENCE: "+sr.getText());
				System.out.print("ENTITY: ");
				for (String e : sr.getEntity()) {
					System.out.print (e+" ");
				}
				System.out.println("\n");
			}
		}
		/*STAMPA L'ARRAY DI TUTTE LE FRASI DELLA PAGINA*/		
//		for (String s : phrases) {
//			System.out.println (phrases.indexOf(s)+") "+s);
//		}
	}

	 		


	//returns a list of the entity of the sentence
	public static List<String> matchEntity (String frase){
		List<String> entity = new ArrayList<String>();
		for (String kw : KEYWORDS) {
			Pattern my_pattern = Pattern.compile("([^a-z]"+kw+"[^a-z])|(^"+kw+"[^a-z])");
			Matcher m = my_pattern.matcher(frase.toLowerCase());
			if (m.find()){
				entity.add(kw);
			}
		}
		return entity;
	}		
}