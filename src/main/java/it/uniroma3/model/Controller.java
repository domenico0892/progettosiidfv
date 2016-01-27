package it.uniroma3.model;

import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	public static void main(String[] args) throws Exception {
		 FileReader reader = new FileReader("config.json");
         JSONParser jsonParser = new JSONParser();		 
		 JSONObject cj = (JSONObject) jsonParser.parse(reader);

		switch(args.length) {
		case 0:
			System.out.println("Utilizzo: inserire url ben formati separati da spazio");
			break;
		default:
			
			String crawlStorageFolder = (String)cj.get("crawler");
			int numberOfCrawlers = 1;

			CrawlConfig config = new CrawlConfig();
			config.setCrawlStorageFolder(crawlStorageFolder);

			/*
			 * Instantiate the controller for this crawl.
			 */
			PageFetcher pageFetcher = new PageFetcher(config);
			RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
			RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
			CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

			/*
			 * For each crawl, you need to add some seed urls. These are the first
			 * URLs that are fetched and then the crawler starts following links
			 * which are found in these pages
			 */
			//controller.addSeed("http://www.ics.uci.edu/~lopes/");
			//controller.addSeed("http://www.ics.uci.edu/~welling/");
			for (int i=0;i<args.length;i++)
				controller.addSeed(args[i]);

			/*
			 * Start the crawl. This is a blocking operation, meaning that your code
			 * will reach the line after this only when crawling is finished.
			 */
			controller.start(MyCrawler.class, numberOfCrawlers);
		}
	}
}