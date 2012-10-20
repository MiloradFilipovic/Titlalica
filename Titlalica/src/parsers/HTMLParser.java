package parsers;

import java.util.ArrayList;

import main.MainFrame;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import utils.Subtitle;

public class HTMLParser {

	HtmlCleaner cleaner = new HtmlCleaner();
	MainFrame mf;
	
	HTMLParser(HtmlCleaner clnr, MainFrame mframe) {
		cleaner = clnr;
		mf = mframe;
	}
	
	public ArrayList<Subtitle> getSubtitles(String title, String language) {
		return null;
	}
	
	public void getSubtitleData(TagNode rootNode, ArrayList<Subtitle> subtitles) {
	}
	
	ArrayList<TagNode> getTagsByClass(TagNode root, String tagName, String CSSClassname) {
        ArrayList<TagNode> divList = new ArrayList<TagNode>();

        TagNode divElements[] = root.getElementsByName(tagName, true);
        for (int i = 0; divElements != null && i < divElements.length; i++) {
            String classType = divElements[i].getAttributeByName("class");
            if (classType != null && classType.equals(CSSClassname)) {
                divList.add(divElements[i]);
            }
        }
        return divList;
    }
	
	TagNode getRootNodeForPage(int pageNumber, int language, String title) {
		return null;
	}
	
	public String getDownloadURL(String page) {
		return null;
	}
	
}
