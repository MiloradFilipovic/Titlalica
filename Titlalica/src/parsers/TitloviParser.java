package parsers;

import java.net.URL;
import java.util.ArrayList;

import main.MainFrame;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import utils.Subtitle;

public class TitloviParser extends HTMLParser {
	
	public TitloviParser(HtmlCleaner clnr, MainFrame mframe) {
		super(clnr, mframe);
	}

	@Override
	public ArrayList<Subtitle> getSubtitles(String title, String language) {
		ArrayList<Subtitle> subtitles = new ArrayList<Subtitle>();
		
		TagNode rootNode = getRootNodeForPage(1, language, title);
		
		if(rootNode != null) {
			getSubtitleData(rootNode, subtitles);
			
			ArrayList<TagNode> pageNodes = getTagsByClass(rootNode, "ul", "clear pagination");
			int pages = 1;
			if(!pageNodes.isEmpty()) {
				pages = pageNodes.get(0).getChildren().size();
			}
			mf.lblMessage.setText(mf.lblMessage.getText()+".");
			if(pages > 1) {
				for(int i=2; i<=pages; i++) {
					rootNode = getRootNodeForPage(i, language, title);
					getSubtitleData(rootNode, subtitles);
					mf.lblMessage.setText(mf.lblMessage.getText()+".");
				}
			}
			return subtitles;
		}else {
			return null;
		}
	}

	@Override
	public void getSubtitleData(TagNode rootNode, ArrayList<Subtitle> subtitles) {
		ArrayList<TagNode> listingTags = getTagsByClass(rootNode, "li", "listing");
		
		if(!listingTags.isEmpty()) {
			TagNode liNode = listingTags.get(0);
			TagNode nja = (TagNode) liNode.getChildren().get(0);
			for(int i=0; i<nja.getChildren().size(); i++) {
				TagNode titlli = (TagNode) nja.getChildren().get(i);
				TagNode namelink = (TagNode) titlli.getElementListByName("a", true).get(0);
				ArrayList<TagNode> cdTags = getTagsByClass(titlli, "span", "cd");
				ArrayList<TagNode> releaseTags = getTagsByClass(titlli, "span", "release");
				ArrayList<TagNode> fpsTags = getTagsByClass(titlli, "span", "fps");
				ArrayList<TagNode> dwTags = getTagsByClass(titlli, "a", "button");
				
				//naslov
				String ttitle = namelink.getText().toString();
				//broj cd-a
				String tCDNumber = "0";
				if(!cdTags.isEmpty()) {
					tCDNumber = cdTags.get(0).getText().toString().replace("CD", " ").trim();
				}
				//verzija
				String release = "N/A";
				if(!releaseTags.isEmpty()) {
					release = releaseTags.get(0).getText().toString().trim();
				}
				//fps
				String fps = "N/A";
				if(!fpsTags.isEmpty()) {
					fps = fpsTags.get(0).getText().toString().substring(4, fpsTags.get(0).getText().toString().length()).trim();
				}
				
				TagNode dwTagNode = dwTags.get(0);
				String dwPage = dwTagNode.getAttributeByName("href");
				
				Subtitle subtitle = new Subtitle(ttitle, release, dwPage);
				subtitle.setNumberOfDiscs(Integer.parseInt(tCDNumber));
				subtitle.setFps(fps);
				subtitle.setDownloadURL(dwPage);
				
				subtitles.add(subtitle);
			}
		}
	}

	TagNode getRootNodeForPage(int pageNumber, String language, String title) {
		URL pageURL;
		TagNode rootNode;
		try {
			String url = "http://titlovi.com/titlovi/titlovi.aspx?prijevod=" + title.replace(" ", "+") + "&jezik=" + language;
			if(pageNumber > 1) {
				url += "&stranica=" + pageNumber;
			}
			pageURL = new URL(url);
			rootNode = cleaner.clean(pageURL);
			return rootNode;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getDownloadURL(String page) {
		String dwurl = "";
		URL url; 
		TagNode root;
		try {
			url = new URL(page);
			root = cleaner.clean(url);
			
			ArrayList<TagNode> nodes = getTagsByClass(root, "a", "button s1 button-download");
			dwurl = nodes.get(0).getAttributeByName("href");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dwurl;
	}
}
