package uk.co.jacekk.scraperlib;

public interface ProgressHandler {
	
	public void onSuccess(Scraper<?> scraper, long time);
	
	public void onFailure(Scraper<?> scraper, Exception cause, int attempt, int maxRetries, long wait);
	
}
