package uk.co.jacekk.scraperlib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ArrayBlockingQueue;

public class ScraperQueue<T extends Scraper<R>, R> {
	
	private int maxThreads;
	private int retries;
	protected ProgressHandler progressHandler;
	protected ArrayBlockingQueue<ScraperThread<T, R>> threads;
	private ListIterator<Scraper<R>> scrapers;
	protected ResultsHandler<R> resultsHandler;
	
	public ScraperQueue(int maxThreads, int retries, ProgressHandler progressHandler, ResultsHandler<R> resultsHandler){
		if (maxThreads < 1){
			throw new IllegalArgumentException("maxThreads must be >= 1");
		}
		
		if (retries < 1){
			throw new IllegalArgumentException("retries must be >= 1");
		}
		
		this.maxThreads = maxThreads;
		this.retries = retries;
		this.progressHandler = progressHandler;
		this.resultsHandler = resultsHandler;
		this.scrapers = Collections.synchronizedList(new ArrayList<Scraper<R>>()).listIterator();
	}
	
	public ScraperQueue(int maxThreads, int retries){
		this(maxThreads, retries, null, null);
	}
	
	public void addScraper(Scraper<R> scraper){
		synchronized (this.scrapers){
			this.scrapers.add(scraper);
		}
	}
	
	public void addAllScrapers(List<Scraper<R>> scrapers){
		synchronized (this.scrapers){
			for (Scraper<R> scraper : scrapers){
				this.scrapers.add(scraper);
			}
		}
	}
	
	public void scrape(){
		this.threads = new ArrayBlockingQueue<ScraperThread<T, R>>(this.maxThreads);
		
		while (this.scrapers.hasPrevious()){
			while (this.scrapers.hasPrevious()){
				Scraper<R> scraper = this.scrapers.previous();
				
				try{
					ScraperThread<T, R> thread = new ScraperThread<T, R>(this, scraper, this.retries, this.progressHandler);
					
					this.threads.put(thread);
					
					thread.start();
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
	}
	
}
