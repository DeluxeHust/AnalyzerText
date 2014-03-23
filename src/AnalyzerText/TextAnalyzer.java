package AnalyzerText;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import com.google.gson.Gson;

import jvntagger.MaxentTagger;
import jvntagger.POSTagger;
import vn.hus.nlp.tokenizer.VietTokenizer;

public class TextAnalyzer {
	private static final String CONTENT_FILE = "baomoi.content.";
	private static final String TOKENIZED_FILE = "tokenized.txt";
	
	private Map<String, Integer> verbTable = new HashMap<String, Integer>();
	private static String dateStr;
	
	private String inputFile;
	
	public TextAnalyzer (String inputFile) throws IOException {
		this.inputFile = inputFile;
		
		//get current Date
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		dateStr = dateFormat.format(date);
	}
	
	public void writeContentToFile(String outputFile) throws IOException{
		//Open Input, Output stream
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		PrintWriter pw = new PrintWriter(outputFile+dateStr+".txt");
		
		String currentLine;
		String[] token = null;
		String content;
		
		//read line, get content from json, write to output file
		while ((currentLine = br.readLine()) !=null) {
			if(!currentLine.equals("\n")) token = currentLine.split("\t");
			Article art = new Article();
			Gson gson = new Gson();
			art = gson.fromJson(token[3], Article.class);
			content = art.getContent();
			content = content.replace("\n", "");
			content = content.replace("<strong>", "");
			content = content.replace("</strong>", "");
			pw.println(content);
			pw.flush();
		}
		//Close Input, Output stream
		br.close();
		pw.close();
	}
	
	//tokenize content file
	public void tokenAnalyze() {
		VietTokenizer tokenizer = new VietTokenizer();
		tokenizer.tokenize(CONTENT_FILE+dateStr+".txt", TOKENIZED_FILE);
	}
	
	public void verbAnalyze() throws IOException {
		String modelDir = "model/maxent";
		POSTagger tagger = null;
		tagger = new MaxentTagger(modelDir);
	
		BufferedReader br = new BufferedReader(new FileReader(TOKENIZED_FILE));
		String currentLine, taggedLine;
		String[] token;
		
		while ((currentLine = br.readLine()) !=null) {
			if(!currentLine.equals("\n")) {
				taggedLine = tagger.tagging(currentLine.toLowerCase());				//tag tokenized word in the line
				token = taggedLine.split(" ");										//split tagged word in token list	
				
				//get verb and put in verbTable
				for(int i=0;i<token.length;i++) {
					if(token[i].endsWith("/V")) {
						String verb = token[i].substring(0,token[i].length()-2);
						
						if(verbTable.containsKey(verb)) {
							verbTable.put(verb, verbTable.get(verb)+1);
						}
						else {
							verbTable.put(verb, 1);
						}
					}
				}
			}
		}
		br.close();
	}
	
	//Write the most 30 common verb to file  
	public void writeVerbTableToFile() throws IOException{
		Map<String, Integer> sorted = sortByValues(verbTable);
		int count = 0;
		PrintWriter pw = new PrintWriter("MANHVT_"+dateStr+"_VERB.tsv");
		
		for(Map.Entry entry : sorted.entrySet()) {
			String verb = (String) entry.getKey();
			verb = verb.replace("_", " ");
			pw.println(verb + "\t" + changeWord(verb) + "\t" + entry.getValue());
			pw.flush();
			count++;
			if(count==30) break;
		}
		pw.close();
	}
	
	//change character 1,3,5,7,9 to UpperCase
	public String changeWord(String originalWord) {
		StringBuilder changedWord = new StringBuilder(originalWord);
		for(int i=0;i<changedWord.length();i+=2) {
			if(i>8) break;
			else {
				changedWord.setCharAt(i, Character.toUpperCase(changedWord.charAt(i)));
			}
		}
		return changedWord.toString();
	}
	
	/*
	 * Java method to sort Map in Java by value e.g. HashMap or Hashtable throw
	 * NullPointerException if Map contains null values It also sort values even
	 * if they are duplicates
	 */
	public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(Map<K, V> map) {
		List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {

			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		// LinkedHashMap will keep the keys in the order they are inserted
		// which is currently sorted on natural ordering
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public static void main (String[] args) throws IOException {	
		TextAnalyzer textAnalyzer = new TextAnalyzer("baomoi.tsv");
		textAnalyzer.writeContentToFile(CONTENT_FILE);
		textAnalyzer.tokenAnalyze();
		textAnalyzer.verbAnalyze();
		textAnalyzer.writeVerbTableToFile();	
	}	
	
}

