package sk.suchac.hbe.parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sk.suchac.hbe.objects.SearchResult;

public class ChapterSearcher {
	
	public static ArrayList<SearchResult> search(int bookId, int actualChapter,
			String text, String searchString) {
		
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		
		String[] words = searchString.split(" ");
		StringBuilder regexBuilder = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			regexBuilder.append(words[i]);
			if (i != words.length - 1) {
				regexBuilder.append("\\s+[<b>\\S*</b>]*\\s*");
			}
		}
		
		Pattern pattern = Pattern.compile(regexBuilder.toString());
	    Matcher matcher = pattern.matcher(text);
	    
	    while (matcher.find()) {
	    	SearchResult result = new SearchResult();
	    	result.setBookId(bookId);
	    	result.setChapterId(actualChapter);
	        
	        String beforeSearchString = text.substring(0, matcher.start());
			int startIndex = beforeSearchString.lastIndexOf("<b>");
			String afterSearchString = text.substring(matcher.end());
			int finishIndex = afterSearchString.indexOf("<b>");
			
			if (finishIndex == -1) {
				result.setSample(text.substring(startIndex));
			} else {
				result.setSample(text.substring(startIndex, finishIndex + matcher.end()));
			}
	        results.add(result);
	    }
	    
	    return results;
	}

}
