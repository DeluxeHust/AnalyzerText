package AnalyzerText;
//class contains an article form which was crawled and stored in json file
public class Article {
	private String abstrack;
	private String category;
	private String content;
	private Long crawl;
	private String crawler;
	private Boolean htmlenc;
	
	class img_urls { 
		public String description;
		public String url;
		
		public img_urls () {
			//no args constructor
		}
	}

	private String isoLanguageCode;
	private String media;
	private Long pub;
	private String region;
	private String source_url;
	private String[] tags;
	private String title;
	private String url;
	private Boolean valid;
	
	public Article () {
		//no args constructor
	}
	
	//get content tag
	public String getContent () {
		return content;		
	}
}
