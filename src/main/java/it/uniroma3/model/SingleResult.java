package it.uniroma3.model;
import java.util.Date;

import org.bson.Document;

public class SingleResult {
	private Date dateCreation;
	private String url;
	private String text;
	
	public SingleResult (String url, String text) {
		this.dateCreation = new Date();
		this.url = url;
		this.text = text;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public String getUrl() {
		return url;
	}

	public String getText() {
		return text;
	}
	
	public Document singleResult2Document () {
		Document doc = new Document();
		doc.append("dateCreation", this.dateCreation);
		doc.append("url", this.url);
		doc.append("text", this.text);
		return doc;
	}
}
