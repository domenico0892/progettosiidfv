package it.uniroma3.persistence;

import com.mongodb.MongoClient;

public class MongoConnection {

	private MongoClient mongoClient;

	public MongoConnection () {
		this.mongoClient = new MongoClient ("localhost", 27017 );
	}
	
	public MongoClient getMongoClient() {
		return this.mongoClient;
	}
}
