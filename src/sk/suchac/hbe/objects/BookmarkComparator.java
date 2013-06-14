package sk.suchac.hbe.objects;

import java.util.Comparator;

public class BookmarkComparator implements Comparator<Bookmark> {
	
	@Override
	public int compare(Bookmark b1, Bookmark b2) {
		long ts1 = b1.getTimestamp();
		long ts2 = b2.getTimestamp();

		if(ts1 > ts2) return 1;
		else if(ts1 < ts2) return -1;
		else return 0;
	}
}
