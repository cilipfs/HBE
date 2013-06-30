package sk.suchac.hbe.parser;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sk.suchac.hbe.helpers.HtmlHelper;
import sk.suchac.hbe.objects.SearchResult;

public class SearchXmlHandler extends DefaultHandler {
	
	int bookId = -1;
	String searchString = "";
	
	private boolean insideChapter = false;
	private boolean insideVerse = false;
	private boolean insideHeading = false;
	
	private int actualChapter = -1;
	private StringBuilder text = new StringBuilder();
	
	ArrayList<SearchResult> results = new ArrayList<SearchResult>();
	
	public SearchXmlHandler(int bookId, String searchString) {
		this.bookId = bookId;
		this.searchString = searchString;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		if ("Book".equals(localName)) {
			return;
		}
		if ("Chapter".equals(localName)) {
			String chapterNumberString = atts.getValue("number");
			actualChapter = Integer.valueOf(chapterNumberString) - 1;
			insideChapter = true;
			return;
		}
		if (insideChapter && "Verse".equals(localName)) {
			insideVerse = true;
			text.append(HtmlHelper.bold(atts.getValue("number")));
			text.append(" ");
			return;
		}
		if (insideChapter && "Heading".equals(localName)) {
			insideHeading = true;
			if (insideVerse) {
				text.append(" ");
			}
			text.append(HtmlHelper.getBoldStart());
			return;
		}
		
	}
    
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if ("Chapter".equals(localName)) {
			if (insideChapter) {
				ArrayList<SearchResult> chapterResults = 
						ChapterSearcher.search(bookId, actualChapter, text.toString(), searchString);
				if (!chapterResults.isEmpty()) {
					results.addAll(chapterResults);
				}
				text = new StringBuilder();
			}
			insideChapter = false;
			return;
		}
		if (insideChapter && "Verse".equals(localName)) {
			insideVerse = false;
			text.append(" ");
			return;
		}
		if (insideChapter && "Heading".equals(localName)) {
			insideHeading = false;
			text.append(HtmlHelper.getBoldEnd());
			return;
		}
	}
    
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		if (insideVerse || insideHeading) {
			text.append(new String(ch, start, length));
			return;
		}
	}

	public ArrayList<SearchResult> getResults() {
		return results;
	}
}
