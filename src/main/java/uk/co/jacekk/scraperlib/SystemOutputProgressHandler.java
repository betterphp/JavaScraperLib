package uk.co.jacekk.scraperlib;

import uk.co.jacekk.scraperlib.ProgressHandler;
import uk.co.jacekk.scraperlib.Scraper;

public class SystemOutputProgressHandler implements ProgressHandler {
	
	@Override
	public synchronized void onSuccess(Scraper<?> scraper, long time){
		System.out.println(scraper.getUrl() + " - Done (" + time + " ms)");
	}
	
	@Override
	public synchronized void onFailure(Scraper<?> scraper, Exception cause, int attempt, int maxRetries, long wait){
		System.err.println(scraper.getUrl() + " - Failed (" + attempt + "/" + maxRetries + ") [waiting " + wait + " ms]: " + cause.getMessage());
	}
	
}
