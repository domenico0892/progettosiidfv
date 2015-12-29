package it.uniroma3.model;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

public class SingleResult {
	private Date dateCreation;
	private String url;
	private String text;
	private List<String> entity;
	
	public SingleResult (String url, String text) {
		this.dateCreation = new Date();
		this.url = url;
		this.text = text;
		this.entity = new ArrayList<String>();
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
	
	public List<String> getEntity () {
		return this.entity;
	}

	public void setEntity(List<String> entity) {
		this.entity = entity;
	}
	
	public Document singleResult2Document () {
		Document doc = new Document();
		doc.append("dateCreation", this.dateCreation);
		doc.append("url", this.url);
		doc.append("text", this.text);
		doc.append("entity", this.entity);
		return doc;
	}
}
