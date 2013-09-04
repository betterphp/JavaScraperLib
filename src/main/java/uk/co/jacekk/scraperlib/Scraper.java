package uk.co.jacekk.scraperlib;

import java.io.IOException;

public abstract class Scraper<R> extends Thread implements Runnable {
	
	protected String url;
	private ScraperQueue<?, R> queue;
	private int retries;
	
	public Scraper(String url){
		super(url + " scraper thread");
		
		this.url = url;
	}
	
	protected void setQueue(ScraperQueue<?, R> queue){
		this.queue = queue;
	}
	
	protected void setRetries(int retries){
		this.retries = retries;
	}
	
	@Override
	public void run(){
		for (int i = 1; i <= this.retries; ++i){
			try{
				R result = this.scrape();
				
				synchronized (this.queue.results){
					this.queue.results.add(result);
				}
				
				break;
			}catch (IOException e){
				if (this.queue.verbose){
					System.err.println("Failed to scrape " + this.url + " (" + i + "/" + this.retries + "): " + e.getMessage());
				}
				
				try{
					Thread.sleep(200l);
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
	
	public abstract R scrape() throws IOException;
	
}