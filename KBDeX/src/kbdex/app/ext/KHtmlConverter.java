package kbdex.app.ext;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

public class KHtmlConverter {
	public static String html2text(String html) {
		Source source = new Source(html);
		TextExtractor extractor = source.getTextExtractor();
		extractor.setExcludeNonHTMLElements(true);
		String text = source.getTextExtractor().toString();
		return text;
	}
}
