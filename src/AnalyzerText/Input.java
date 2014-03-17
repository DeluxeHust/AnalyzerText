package AnalyzerText;
import java.io.*;
import java.util.*;
import java.text.*;

import vn.hus.nlp.tokenizer.VietTokenizer;

import com.google.gson.Gson;

public class Input {
	public ArrayList<String> str = new ArrayList<String>();
	public ArrayList<String> tkn = new ArrayList<String>();
	public static String dateStr;
	
	//read input .tsv file, get every token which is seperated by "\t" in a ArrayList
	public Input (String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String currentLine;
		while ((currentLine = br.readLine()) !=null) {
			if(!currentLine.equals("\n")) str.add(currentLine);
		}
		
		for(int i=0;i<str.size();i++) {
			StringTokenizer token = new StringTokenizer(str.get(i),"\t");	
			while(token.hasMoreTokens()) {
				tkn.add(token.nextToken());
			}
		}
		br.close();
	}
	
	//Get content tag from json, write in file baomoi.content.yyyyMMdd.txt
	public void printStr (String outputFile) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		dateStr = dateFormat.format(date);
		//System.out.print(dateStr);
		PrintWriter pw = new PrintWriter(outputFile+dateStr+".txt");
		for(int i=3;i<tkn.size();i+=4) {
			String line = tkn.get(i)+"\n";
			Article art = new Article();
			Gson gson = new Gson();
			art = gson.fromJson(line, Article.class);
			pw.println(art.getContent().replaceAll("\n",""));
		}
		pw.close();
	}
}

