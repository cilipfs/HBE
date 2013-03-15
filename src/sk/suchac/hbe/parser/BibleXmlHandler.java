package sk.suchac.hbe.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sk.suchac.hbe.helpers.HtmlHelper;

public class BibleXmlHandler extends DefaultHandler {
	
	private int chapterId = -1;
	private String bookTitle = "";
	private String bookAbbreviation = "";
	
	private boolean insideChapter = false;
	private boolean insideVerse = false;
	private boolean insideHeading = false;
	
	private StringBuilder htmlOutput = new StringBuilder();
	
	public BibleXmlHandler(int chapterId) {
		this.chapterId = chapterId;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		if ("Book".equals(localName)) {
			bookTitle = atts.getValue("title");
			bookAbbreviation = atts.getValue("abbreviation");
			htmlOutput.append(HtmlHelper.bold(bookTitle)).append(" ").append(HtmlHelper.bold(chapterId + 1));
			htmlOutput.append(HtmlHelper.getSkipLine());
			return;
		}
		if ("Chapter".equals(localName)) {
			String chapterNumberString = atts.getValue("number");
			String searchedChapterNumber = String.valueOf(chapterId + 1);
			if (chapterNumberString.compareTo(searchedChapterNumber) == 0) {
				insideChapter = true;
			}
			return;
		}
		if (insideChapter && "Verse".equals(localName)) {
			insideVerse = true;
			htmlOutput.append(HtmlHelper.bold(atts.getValue("number")));
			htmlOutput.append(HtmlHelper.getNewLine());
			return;
		}
		if (insideChapter && "Heading".equals(localName)) {
			insideHeading = true;
			if (insideVerse) {
				htmlOutput.append(HtmlHelper.getNewLine());
			}
			htmlOutput.append(HtmlHelper.getBoldStart());
			return;
		}
		
	}
    
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if ("Chapter".equals(localName)) {
			if (insideChapter) {
				throw new SAXException("Chapter successfully parsed.");
			}
			insideChapter = false;
			return;
		}
		if (insideChapter && "Verse".equals(localName)) {
			insideVerse = false;
			htmlOutput.append(HtmlHelper.getNewLine());
			return;
		}
		if (insideChapter && "Heading".equals(localName)) {
			insideHeading = false;
			htmlOutput.append(HtmlHelper.getBoldEnd());
			htmlOutput.append(HtmlHelper.getNewLine());
			return;
		}
		
	}
    
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		//String charactersString = new String(ch, start, length);
		
		if (insideVerse || insideHeading) {
			htmlOutput.append(new String(ch, start, length));
			return;
		}
	}
	
	public String getHtmlOutput() {
		return htmlOutput.toString();
	}
	
	public String getActualBookTitleAndChapter() {
		return bookTitle + " " + (chapterId + 1);
	}
	
	public String getActualBookAbbreviationAndChapter() {
		return bookAbbreviation + " " + (chapterId + 1);
	}
	
}
