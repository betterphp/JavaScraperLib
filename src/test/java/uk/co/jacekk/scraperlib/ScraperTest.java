package uk.co.jacekk.scraperlib;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ScraperTest {
	
	@Test
	public void test(){
		ScraperQueue<SimpleScraper, String> queue = new ScraperQueue<ScraperTest.SimpleScraper, String>(2, 4, new StreamOutputProgressHandler());
		
		queue.addScraper(new SimpleScraper("http://jacekk.co.uk/ip.php", 0));
		
		queue.scrape();
		
		Assert.assertEquals(10, queue.getResults().size());
		
		for (String result : queue.getResults()){
			Assert.assertNotEquals("", result);
		}
	}
	
	private class SimpleScraper extends Scraper<String> {
		
		private int totalResults;
		
		public SimpleScraper(String url, int totalResults){
			super(url);
			
			this.totalResults = totalResults;
		}
		
		@Override
		public void scrape(List<Scraper<String>> newScrapers, List<String> results) throws IOException {
			HttpURLConnection connection = (HttpURLConnection) (new URL(this.getUrl())).openConnection();
			
			BufferedInputStream stream = new BufferedInputStream(connection.getInputStream());
			InputStreamReader reader = new InputStreamReader(stream);
			CharBuffer buffer = CharBuffer.allocate(1024);
			
			while (reader.read(buffer) != -1);
			
			results.add(buffer.toString());
			
			if (this.totalResults < 9){
				newScrapers.add(new SimpleScraper(this.getUrl(), this.totalResults + 1));
			}
		}
		
	}
	
}
