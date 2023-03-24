import java.net.*;
import java.io.*;
import java.util.*;
public class WeatherServer{

	public static void main(String[] args) throws Exception {
		/* args list
		 * 0: input path
		 * 1: number of threads
		 * */
		
		// final list of averages
		List<Integer> averages = new ArrayList<>();
		
		// no error checking here, just be careful
		int threads = Integer.parseInt(args[1]);
		// read in input from csv file, adapted from https://www.baeldung.com/java-csv-file-array
		List<List<String>> records = new ArrayList<>();
	  	try (Scanner scanner = new Scanner(new File(args[0]));) {
	  		while (scanner.hasNextLine()) {
	  			List<String> values = new ArrayList<String>();
	  			try (Scanner rowScanner = new Scanner(scanner.nextLine())) {
	  				rowScanner.useDelimiter(",");
	  				while (rowScanner.hasNext()) {
	  					values.add(rowScanner.next());
	  				}
	  			}
	  			// we only care about the records covering TMAX
	  			if(values.get(2).equals("TMAX"))
	  				records.add(values);
	  		}
	  	}

	  	Map<String, List<Integer>> data = new HashMap<>();
	  	Map<String, Integer> output = new HashMap<>();
	  	
	  	// Maps all temperature values into unique keys based on weather station ID + month
	  	for(List<String> line : records) {
	  		// formatted key: stationID, MM
	  		String key = line.get(0) + ", " + line.get(1).substring(4,6);
	  		// if key doesn't already exist, create new list
	  		if(!data.containsKey(key))
	  			data.put(key, new ArrayList<Integer>());

	  		// then add our temperature value
  			data.get(key).add(Integer.parseInt(line.get(3)));
	  	}
	  	
	  	System.out.println(data.get("ITE00100550, 01"));
	  	
	  	List<List<Integer>> test = new ArrayList<>();

	  	for(List<Integer> p : data.values()) {
	  		test.add(p);
	  	}
	  	
	  	// lets try splitting evenly / # of threads
	  	
	  	// next step is to split up based on id-month
	  	
	  	/* weather data
	  	 * 0: weather station id
	  	 * 1: date in format (YYYYMMDD)
	  	 * 2: temp type
	  	 * 3: temp value
	  	 * */
	  	
	  	
		// socket stuff
		try {
			// list of server threads
			List<ServerThread> serverThreads = new ArrayList<>();
			ServerSocket server = new ServerSocket(8888);
			int counter = 0;
			
			System.out.println("Server started ...");
			
			// wait for all workers to connect
			while(counter < threads) {
				counter++;
				Socket serverClient = server.accept();
				// add thread to list with client inside
				serverThreads.add(new ServerThread(serverClient, counter));
			}
			// then start all of them
			for(ServerThread thread : serverThreads) {
				// start thread
				System.out.println(" >> Client No: " + thread.clientNumber + " started!");
				thread.setData(test);
				thread.start();
			}

			// wait for each thread to finish, then add its results to a list
			for(ServerThread thread : serverThreads) {
				thread.join();
				averages.addAll(thread.getAverages());
			}
			
			// this is the final list
			System.out.println(averages.toString());
			
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}

// comparator class to sort the list of strings based on first item
class Sorter implements Comparator<List<String>> {
	
	public int compare(List<String> s1, List<String> s2) {
		return s1.get(0).compareTo(s2.get(0));
	}
}
