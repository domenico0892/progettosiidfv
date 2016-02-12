package it.uniroma3.model;

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

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
	private WebDriver driver; 
//	private WebDriver driverIframe;

	public MyCrawler () {
		super();
		MongoConnection m = new MongoConnection();
		MongoDatabase d = m.getMongoClient().getDatabase("pagine");
		this.coll = d.getCollection("pagine");
		FileReader reader;
		try {
			reader = new FileReader("config.json");
	        JSONParser jsonParser = new JSONParser();		 
			JSONObject cj = (JSONObject) jsonParser.parse(reader);
			DesiredCapabilities caps = DesiredCapabilities.phantomjs();
			caps.setJavascriptEnabled(false);
			caps.setCapability("takesScreenshot", true);
			ArrayList<String> cliArgsCap = new ArrayList<String>();
			cliArgsCap.add("--web-security=false");
			cliArgsCap.add("--ssl-protocol=any");
			cliArgsCap.add("--ignore-ssl-errors=true");
			caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
			caps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS,new String[] { "--logLevel=2" });
			caps.setCapability(
					PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
					cj.get("phantomjs")
					);
			this.driver = new  PhantomJSDriver(caps);
//			this.driverIframe = new  PhantomJSDriver(caps);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			this.driver.get(url);
			String pageS = this.driver.getPageSource();
//			List<WebElement> iframeElements = this.driver.findElements(By.tagName("iframe"));
//			for(WebElement we : iframeElements){
//				String iframeId = we.getAttribute("id");
//				String iframeSrc = we.getAttribute("src");
//				if (iframeId != null && !iframeId.equals("") && iframeSrc != null && iframeSrc.contains("http")){
//					System.out.println("ID "+iframeId);
//					System.out.println("SRC "+iframeSrc);
//					try {
//						this.driver.switchTo().frame(iframeId);
//						String iframeS = this.driver.getPageSource();
//						pageS = manipolareHtml(pageS,iframeId, iframeS);
//						this.driver.switchTo().defaultContent();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					try {
//						this.driverIframe.get(iframeSrc);
//						String iframeS = this.driverIframe.getPageSource();
//						pageS = manipolareHtml(pageS,iframeId, iframeS);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					
//				}
//			}
			
			URL url_parsed;
			try {
				url_parsed = new URL (url);
				Document doc = new Document();
				doc.append("url", url);
				doc.append("host", url_parsed.getHost());
				doc.append("html", pageS);
				System.out.println("INSERITO\n\n");
				this.coll.insertOne(doc);
			} catch (MalformedURLException e) {
				System.out.println("NON INSERITO\n\n");
				e.printStackTrace();
			}
		}
	}
	
//	private String manipolareHtml(String pageS, String iframeId, String iframeS) throws IOException {
//		org.jsoup.nodes.Document doc = Jsoup.parse(pageS);
//		doc.select("iframe#"+iframeId).after(iframeS);
//		doc.select("iframe#"+iframeId).remove();
//		return doc.toString();
//	}
}