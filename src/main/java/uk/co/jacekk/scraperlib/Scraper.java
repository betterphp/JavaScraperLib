package uk.co.jacekk.scraperlib;

import java.io.IOException;
import java.util.List;

public abstract class Scraper<R> {
	
	private String url;
	
	public Scraper(String url){
		this.url = url;
	}
	
	public String getUrl(){
		return this.url;
	}
	
	public abstract void scrape(List<Scraper<R>> newScrapers, List<R> results) throws IOException;
	
}