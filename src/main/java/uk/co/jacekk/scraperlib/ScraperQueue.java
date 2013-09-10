package uk.co.jacekk.scraperlib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class ScraperQueue<T extends Scraper<R>, R> {
	
	private int maxThreads;
	private int retries;
	protected ProgressHandler progressHandler;
	protected ArrayBlockingQueue<T> threads;
	private ArrayList<T> scrapers;
	protected List<R> results;
	
	public ScraperQueue(int maxThreads, int retries, ProgressHandler progressHandler){
		if (maxThreads < 1){
			throw new IllegalArgumentException("maxThreads must be >= 1");
		}
		
		if (retries < 1){
			throw new IllegalArgumentException("retries must be >= 1");
		}
		
		this.maxThreads = maxThreads;
		this.retries = retries;
		this.progressHandler = progressHandler;
		this.scrapers = new ArrayList<T>();
	}
	
	public ScraperQueue(int maxThreads, int retries){
		this(maxThreads, retries, null);
	}
	
	public void addScraper(T scraper){
		this.scrapers.add(scraper);
	}
	
	public void scrape(){
		this.threads = new ArrayBlockingQueue<T>(this.maxThreads);
		this.results = Collections.synchronizedList(new ArrayList<R>(this.scrapers.size()));
		
		for (T scraper : this.scrapers){
			try{
				this.threads.put(scraper);
				
				scraper.setQueue(this);
				scraper.setRetries(this.retries);
				scraper.start();
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		
		synchronized (this.threads){
			while (!this.threads.isEmpty()){
				try{
					this.threads.wait();
				}catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public List<R> getResults(){
		return this.results;
	}
	
}
