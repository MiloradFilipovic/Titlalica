package utils;

public class Subtitle {
	
	String title;
	String version;
	int numberOfDiscs;
	String fps;
	String downloadURL;
	String fileURL;
	String format;
	
	public Subtitle(String title, String version, String downloadURL) {
		super();
		this.title = title;
		this.version = version;
		this.downloadURL = downloadURL;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	public int getNumberOfDiscs() {
		return numberOfDiscs;
	}

	public void setNumberOfDiscs(int numberOfDiscs) {
		this.numberOfDiscs = numberOfDiscs;
	}

	public String getFps() {
		return fps;
	}

	public void setFps(String fps) {
		this.fps = fps;
	}

	public String getFileURL() {
		return fileURL;
	}

	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
