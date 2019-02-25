import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Names {

	private JSONObject dataTotal = new JSONObject();
	private JSONParser dataParser = new JSONParser();
	private File inputFile = new File("..\\names.json");
	private TreeSet<Grouping> sortedSet = new TreeSet<>();
	
	public Names() {
		
		//Scan the JSON input file and copy the entire content
		try(Scanner scanner = new Scanner(this.inputFile)){
			
			StringBuilder collector = new StringBuilder();
			while(scanner.hasNext()) {
				collector.append(scanner.nextLine());
			}
			
			//Parsing the entire content into one JSONObject 
			this.dataTotal = (JSONObject)dataParser.parse(collector.toString());
			
			//Extracting the relevant data as a JSONArray
			JSONArray array = (JSONArray)this.dataTotal.get("data");
			
			/*
			 * HashMap will store each name and the amount of times
			 * that name had occurred in all records
			 */
			HashMap<String,Integer> namesCounter = new HashMap<>();
			
			//Traversal through the entire JSONArray and extracting all names
			String name = "";
			int nameIndex = 11;
			
			for(int each=0; each < array.size(); each++) {
				/*
				 * Each element in array is a nested array, so we have to 
				 * access the name by using an index
				 */
				JSONArray eachDataSet = (JSONArray)array.get(each);
				name = eachDataSet.get(nameIndex).toString().toLowerCase();
				
				if(namesCounter.containsKey(name)) {
					namesCounter.put(name, namesCounter.get(name)+1);
				}else {
					namesCounter.put(name,1);
				}
			}
			
			//Storing the data as groupings and sorting it
			namesCounter.entrySet()
						.stream()
						.map(entry -> new Grouping(entry.getKey(),entry.getValue()))
						.forEach(grouping -> this.sortedSet.add(grouping));;
			
			//Display some results 
			System.out.println(this.getTotalNameCount());
			System.out.println(this.getMostCommon(5));
			this.listAllNames();
			
		} catch (FileNotFoundException | ParseException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Lists all names found in the data set
	 */
	public void listAllNames() {
		this.sortedSet.forEach(grouping -> System.out.println(grouping));
	}
	
	/**
	 * 
	 * @return amount of names found in the data set
	 */
	public int getTotalNameCount() {
		return this.sortedSet.stream()
				   			 .mapToInt(each -> each.occurrences)
				   			 .sum();
	}
	
	/**
	 * 
	 * @param amount specifies how many names will be returned
	 * @return most commonly appeared names
	 */
	public ArrayList<Grouping> getMostCommon(int amount){
		ArrayList<Grouping> list = new ArrayList<Grouping>();
		
		Iterator<Grouping> iterator = this.sortedSet.iterator();
		while(amount > 0 && iterator.hasNext()) {
			list.add(iterator.next());
			amount--;
		}
		
		return list;
	}
	
	class Grouping implements Comparable<Grouping>{
		private String name;
		private int occurrences;
		
		public Grouping(String name,int occurrences) {
			this.name = name;
			this.occurrences = occurrences;
		}

		@Override
		public int compareTo(Grouping other) {
			//Multiplied by -1 to reverse the natural ordering
			return -1*((Integer)this.occurrences).compareTo(other.occurrences);
		}
		
		@Override
		public String toString() {
			return this.name +":" + this.occurrences;
		}
		
	}
	
	
	public static void main(String[] args) {
		new Names();
	}
}
