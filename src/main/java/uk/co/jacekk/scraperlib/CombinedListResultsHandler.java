package uk.co.jacekk.scraperlib;

import java.util.ArrayList;
import java.util.List;

public class CombinedListResultsHandler<R> implements ResultsHandler<R> {
	
	private ArrayList<R> results;
	
	public CombinedListResultsHandler(){
		this.results = new ArrayList<R>();
	}
	
	@Override
	public void handleResults(List<R> results){
		this.results.addAll(results);
	}
	
	public List<R> getResults(){
		return this.results;
	}
	
}
