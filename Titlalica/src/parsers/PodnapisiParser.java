package parsers;

import java.net.URL;
import java.util.ArrayList;

import main.MainFrame;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import utils.Subtitle;

public class PodnapisiParser extends HTMLParser {

	public PodnapisiParser(HtmlCleaner clnr, MainFrame mframe) {
		super(clnr, mframe);
	}

	@Override
	public ArrayList<Subtitle> getSubtitles(String title, String language) {
		ArrayList<Subtitle> subtitles = new ArrayList<Subtitle>();
		
		int lang = Integer.parseInt(language);
		
		TagNode rootNode = getRootNodeForPage(1, lang, title);
		if(rootNode != null) {
			ArrayList<TagNode> pageTags = getTagsByClass(rootNode, "button", "selector");
			int pages = Integer.parseInt(String.valueOf(pageTags.get(0).getText().toString().charAt(0)));
			
			if(pages>0) {
				getSubtitleData(rootNode, subtitles);
				
				mf.lblMessage.setText(mf.lblMessage.getText()+".");
				
				if (pages > 1) {
					for(int s=2; s<=pages; s++) {
						TagNode root = getRootNodeForPage(s, lang, title);
						getSubtitleData(root, subtitles);
						mf.lblMessage.setText(mf.lblMessage.getText()+".");
					}
				}
			}
			return subtitles; 
		}else {
			return null;
		}
	}

	@Override
	public void getSubtitleData(TagNode rootNode, ArrayList<Subtitle> subtitles) {
		ArrayList<TagNode> tableTags = getTagsByClass(rootNode, "table", "list first_column_title");
		TagNode trows[] = tableTags.get(0).getElementsByName("tr", true);
		
		for(int k=1; k<trows.length; k++) {
			TagNode tdata[] = trows[k].getElementsByName("td", true);
			ArrayList<TagNode> divss = getTagsByClass(tdata[0], "div", "list_div2");
			TagNode div = divss.get(0);
			
			//naslov
			ArrayList<TagNode> titles = getTagsByClass(div, "a", "subtitle_page_link");
			String titl = titles.get(0).getText().toString();
			//url
			ArrayList<TagNode> urls = getTagsByClass(div, "a", "subtitle_page_link");
			String dwURL = "http://www.podnapisi.net" + urls.get(0).getAttributeByName("href");
			//verzija
			ArrayList<TagNode> releases = getTagsByClass(div, "span", "release");
			String release = "";
			if(!releases.isEmpty()) {
				release = releases.get(0).getAttributeByName("html_title").replace("<br/>", ",").replace("&amp;", "&");
			}
			
			Subtitle subtitle = new Subtitle(titl, release, dwURL);
			
			//FPS
			subtitle.setFps(tdata[1].getText().toString());
			//broj cd-a
			subtitle.setNumberOfDiscs(Integer.parseInt(tdata[4].getText().toString()));
			
			subtitles.add(subtitle);
		}
	}

	@Override
	TagNode getRootNodeForPage(int pageNumber, int language, String title) {
		URL pageURL;
		TagNode rootNode;
		try {
			pageURL = new URL("http://www.podnapisi.net/hr/ppodnapisi/search?sJ=" + language + "&sS=&sO=&sT=-1&sM=0&sA=0&sK=" + title.replace(" ", "+") +
							 "&sOA=0&sOT=0&sOL=0&sOI=0&sOE=0&sOD=0&sOH=0&sY=&sOCS=0&sFT=0&sR=&sTS=&sTE=&sAKA=1&sH=&sI=&tbsl=1&asdp=0&page=" + pageNumber);
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
			
			ArrayList<TagNode> nodes = getTagsByClass(root, "a", "button big download");
			dwurl = "http://www.podnapisi.net" + nodes.get(0).getAttributeByName("href");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dwurl;
	}
}
