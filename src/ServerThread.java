import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

class ServerThread extends Thread {
	
	Socket serverClient;
	int clientNumber;
	
	private volatile List<Integer> averages = new ArrayList<>();
	private volatile List<List<Integer>> data = new ArrayList<>();
	
	ServerThread(Socket inSocket, int counter) {
		serverClient = inSocket;
		clientNumber = counter;
	}
	
	public void run() {
		try {
			DataOutputStream output = new DataOutputStream(serverClient.getOutputStream());
			BufferedReader input = new BufferedReader(new InputStreamReader(serverClient.getInputStream()));

			List<Integer> t1 = new ArrayList<>();
			t1.add(15);
			t1.add(15);
			t1.add(30);
			
			List<Integer> t2 = new ArrayList<>();
			t2.add(20);
			t2.add(40);
			t2.add(60);
			
			//data.add(t1);
			//data.add(t2);

			// loop through given data and provide to worker
			for(List<Integer> list : data) {
				// remove all formatting from to string method
				String out = list.toString().replaceAll("\\s|\\[|\\]", "");
				// output the list of numbers
				output.writeBytes(out + "\n");
				// then get the average back from the input
				averages.add(Integer.parseInt(input.readLine()));
			}

			System.out.println("AVG : " + averages.toString());

			input.close();
			output.close();
			serverClient.close();
		} catch(Exception e) {
			System.out.println(e);
		} finally {
			System.out.println("Client - " + clientNumber + " exit!");
		}
	}
	
	public List<Integer> getAverages() {
		return averages;
	}
	
	public void setData(List<List<Integer>> d) {
		data = d;
	}
}