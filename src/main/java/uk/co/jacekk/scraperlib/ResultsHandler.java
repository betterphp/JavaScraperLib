package uk.co.jacekk.scraperlib;

import java.util.List;

public interface ResultsHandler<R> {
	
	public void handleResults(List<R> results);
	
}
