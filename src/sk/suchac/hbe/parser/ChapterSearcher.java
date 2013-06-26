package sk.suchac.hbe.parser;

import java.util.ArrayList;

import sk.suchac.hbe.objects.SearchResult;

public class ChapterSearcher {
	
	ArrayList<SearchResult> results = new ArrayList<SearchResult>();

	public static SearchResult search(int bookId, int actualChapter,
			String text, String searchString) {
		String cleanedText = cleanText(text);
		SearchResult result = null;
		
		if (cleanedText.indexOf(searchString) != -1) {
			result = new SearchResult();
			result.setBookId(bookId);
			result.setChapterId(actualChapter);
			
			String[] words = searchString.split(" ");
			int word1Index = text.indexOf(words[0]);
			String beforeSearchString = text.substring(0, word1Index);
			int startIndex = beforeSearchString.lastIndexOf("<b>");
			int wordLastIndex = text.indexOf(words[words.length - 1]);
			String afterSearchString = text.substring(wordLastIndex);
			int finishIndex = afterSearchString.indexOf("</b>") + wordLastIndex;
			
			result.setSample(text.substring(startIndex, finishIndex));
			
		}
		return result;
	}

	private static String cleanText(String text) {
		text = text.replaceAll("<b>\\d*</b>", "");
		text = text.replaceAll("<b>", "");
		text = text.replaceAll("</b>", "");
		return text;
	}

}
