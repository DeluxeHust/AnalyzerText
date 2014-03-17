package AnalyzerText;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import jvntagger.MaxentTagger;
import jvntagger.POSTagger;
import vn.hus.nlp.tokenizer.VietTokenizer;

public class TextAnalyzer {
	public ArrayList<String> str = new ArrayList<String>();
	//public ArrayList<String> verbArray = new ArrayList<String>();
	public Map<String, Integer> verbTable = new HashMap<String, Integer>();
	public static String dateStr;
	
	public static final String CONTENT_FILE = "baomoi.content.";
	public static final String TAGGED_FILE = "tagged.txt";
	public static final String TOKENIZED_FILE = "tokenized.txt";

	public String inputFile;
	public String outputFile;
	
	public TextAnalyzer (String inputFile, String outputFile) throws IOException {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		dateStr = dateFormat.format(date);
	}
	
	public void tokenAnalyze() {
		VietTokenizer tokenizer = new VietTokenizer();
		tokenizer.tokenize(CONTENT_FILE+dateStr+".txt", TOKENIZED_FILE);
	}
	
	public void tagAnalyze() throws IOException {
		String modelDir = "model/maxent";
		POSTagger tagger = null;
		tagger = new MaxentTagger(modelDir);
		String resultStr;
		BufferedReader br = new BufferedReader(new FileReader(TOKENIZED_FILE));
		PrintWriter pw = new PrintWriter(TAGGED_FILE);
		String currentLine;
		while ((currentLine = br.readLine()) !=null) {
			if(!currentLine.equals("\n")) {
				resultStr = tagger.tagging(currentLine.toLowerCase());
				pw.println(resultStr);
			}
		}
		pw.close();
	}
	
	public void verbAnalyze(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String currentLine;
		while ((currentLine = br.readLine()) !=null) {
			StringTokenizer token = new StringTokenizer(currentLine," ");
			while (token.hasMoreTokens()) str.add(token.nextToken());
		}
		
		for(int i=0;i<str.size();i++) {
			String term = str.get(i);
			if(term.endsWith("/V")) {
				String verb = term.substring(0, term.length()-2);
				
				if(verbTable.containsKey(verb)) {
					verbTable.put(verb, verbTable.get(verb)+1);
				}
				else {
					verbTable.put(verb, 1);
					//verbArray.add(verb);
				}
			}
		}
		br.close();
	}
	
	public void printVerbArray() throws IOException{
		Map<String, Integer> sorted = sortByValues(verbTable);
		int count = 0;
		PrintWriter pw = new PrintWriter("MANHVT_"+dateStr+"_VERB.tsv");
		for(Map.Entry entry:sorted.entrySet()) {
			String verb = (String) entry.getKey();
			verb = verb.replaceAll("_", " ");
			pw.println(verb + "\t" + changeWord(verb) + "\t" + entry.getValue());
			count++;
			if(count==30) break;
		}
		pw.close();
	}
	
	public String changeWord(String originalWord) {
		StringBuilder changedWord = new StringBuilder(originalWord);
		for(int i=0;i<changedWord.length();i+=2) {
			changedWord.setCharAt(i, Character.toUpperCase(changedWord.charAt(i)));
		}
//		for(int i=0;i<changedWord.length();i++) {
//			if(changedWord.charAt(i) == "_") changedWord.setCharAt(i, " ");
//		}
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
		Input ip = new Input("baomoi.tsv");
		ip.printStr(CONTENT_FILE);
		TextAnalyzer textAnalyzer = new TextAnalyzer(CONTENT_FILE+dateStr+".txt", TOKENIZED_FILE);
		textAnalyzer.tokenAnalyze();
		textAnalyzer.tagAnalyze();
		textAnalyzer.verbAnalyze(TAGGED_FILE);
		textAnalyzer.printVerbArray();
	}	
	
}

