package uk.co.jacekk.scraperlib;

import java.io.IOException;

public abstract class Scraper<R> extends Thread implements Runnable {
	
	private String url;
	private ScraperQueue<Scraper<R>, R> queue;
	private int retries;
	
	public Scraper(String url){
		super(url + " scraper thread");
		
		this.url = url;
	}
	
	public String getUrl(){
		return this.url;
	}
	
	protected void setQueue(ScraperQueue<Scraper<R>, R> queue){
		this.queue = queue;
	}
	
	protected void setRetries(int retries){
		this.retries = retries;
	}
	
	@Override
	public void run(){
		for (int i = 1; i <= this.retries; ++i){
			try{
				long start = System.currentTimeMillis();
				R result = this.scrape();
				
				if (this.queue.progressHandler != null){
					this.queue.progressHandler.onSuccess(this, (System.currentTimeMillis() - start));
				}
				
				synchronized (this.queue.results){
					this.queue.results.add(result);
				}
				
				break;
			}catch (IOException e){
				long wait = 250l * (i * i);
				
				if (this.queue.progressHandler != null){
					this.queue.progressHandler.onFailure(this, e, i, this.retries, wait);
				}
				
				try{
					Thread.sleep(wait);
				}catch (InterruptedException ie){
					ie.printStackTrace();
				}
			}catch (Exception e){
				if (this.queue.progressHandler != null){
					this.queue.progressHandler.onFailure(this, e, i, this.retries, 0);
				}
				
				break;
			}
		}
		
		synchronized (this.queue.threads){
			this.queue.threads.remove(this);
			this.queue.threads.notify();
		}
	}
	
	public abstract R scrape() throws IOException;
	
}