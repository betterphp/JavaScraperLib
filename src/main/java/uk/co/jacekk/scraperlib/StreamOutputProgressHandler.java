package uk.co.jacekk.scraperlib;

import java.io.PrintStream;

import uk.co.jacekk.scraperlib.ProgressHandler;
import uk.co.jacekk.scraperlib.Scraper;

public class StreamOutputProgressHandler implements ProgressHandler {
	
	private PrintStream outputStream;
	private PrintStream errorStream;
	
	public StreamOutputProgressHandler(PrintStream outputStream, PrintStream errorStream){
		this.outputStream = outputStream;
		this.errorStream = errorStream;
	}
	
	public StreamOutputProgressHandler(){
		this(System.out, System.err);
	}
	
	@Override
	public synchronized void onSuccess(Scraper<?> scraper, long time){
		this.outputStream.println(scraper.getUrl() + " - Done (" + time + " ms)");
	}
	
	@Override
	public synchronized void onFailure(Scraper<?> scraper, Exception cause, int attempt, int maxRetries, long wait){
		this.errorStream.println(scraper.getUrl() + " - Failed (" + attempt + "/" + maxRetries + ") [waiting " + wait + " ms]: " + cause.getMessage());
	}
	
}
