package uk.co.jacekk.scraperlib;

import java.util.ArrayList;

public class ScraperThread<T extends Scraper<R>, R> extends Thread implements Runnable {
	
	private ScraperQueue<T, R> queue;
	private Scraper<R> scraper;
	
	private int retries;
	private ProgressHandler progressHandler;
	
	public ScraperThread(ScraperQueue<T, R> queue, Scraper<R> scraper, int retries, ProgressHandler progressHandler){
		super(scraper.getUrl() + " scraper thread");
		
		this.queue = queue;
		this.scraper = scraper;
		
		this.retries = retries;
		this.progressHandler = progressHandler;
	}
	
	@Override
	public void run(){
		for (int i = 1; i <= this.retries; ++i){
			try{
				long start = System.currentTimeMillis();
				
				// So the scraper does not have to worry about being thread-safe.
				ArrayList<Scraper<R>> newScrapers = new ArrayList<Scraper<R>>();
				ArrayList<R> results = new ArrayList<R>();
				
				this.scraper.scrape(newScrapers, results);
				
				if (this.progressHandler != null){
					this.progressHandler.onSuccess(this.scraper, (System.currentTimeMillis() - start));
				}
				
				// Don't bother locking just to do nothing.
				if (!results.isEmpty()){
					synchronized (this.queue.resultsHandler){
						this.queue.resultsHandler.handleResults(results);
					}
				}
				
				if (!newScrapers.isEmpty()){
					synchronized (this.queue){
						this.queue.addAllScrapers(newScrapers);
					}
				}
				
				break;
			}catch (Exception e){
				long wait = 250l * (i * i);
				
				if (this.queue.progressHandler != null){
					this.queue.progressHandler.onFailure(this.scraper, e, i, this.retries, wait);
				}
				
				try{
					Thread.sleep(wait);
				}catch (InterruptedException ie){
					ie.printStackTrace();
				}
			}
		}
		
		synchronized (this.queue.threads){
			this.queue.threads.remove(this);
			this.queue.threads.notify();
		}
	}
	
}
