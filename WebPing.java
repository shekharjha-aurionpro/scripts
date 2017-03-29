import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WebPing implements Runnable {
	private List<Long> executionValues = new ArrayList<Long>();
	private List<Long> connectionValues = new ArrayList<Long>();

	public static void main(String[] args) throws Exception {
		WebPing webPing = new WebPing();
		Runtime.getRuntime().addShutdownHook(new Thread(webPing));
		while (true) {
			webPing.executeTest(args.length < 1 ? "www.google.com" : args[0],
					args.length < 2 ? 80 : Integer.parseInt(args[1]));
			Thread.sleep(args.length < 3 ? 100 : Integer.parseInt(args[2]));
		}
	}

	public void executeTest(String serverName, int port) throws Exception {
		long executionStart = System.currentTimeMillis();
		try {
			long connectStart = System.currentTimeMillis();
			Socket sock = new Socket(serverName, port);
			sock.getOutputStream().write("h".getBytes());
			sock.getInputStream().available();
			sock.close();
			long connectEnd = System.currentTimeMillis();
			long connectTime = (connectEnd - connectStart);
			connectionValues.add(connectTime);
		} catch (java.io.IOException e) {
			System.out.println("Can't connect to " + serverName + ":" + port);
			System.out.println(e);
		}
		long executionEnd = System.currentTimeMillis();
		long executionTime = (executionEnd - executionStart);
		executionValues.add(executionTime);
	}

	public void run() {
		long numberOfExecution = executionValues.size();
		System.out.println("Total Execution              : " + numberOfExecution);
		Collections.sort(executionValues, new LongComparator());
		Collections.sort(connectionValues, new LongComparator());
		System.out.println("Execution time (max/avg/median/min) : " + executionValues.get(executionValues.size() - 1)
				+ " / " + mean(executionValues) + " / " + median(executionValues) + " / " + executionValues.get(0));
		System.out.println("Connection time (max/avg/median/min) : " + connectionValues.get(connectionValues.size() - 1)
				+ " / " + mean(connectionValues) + " / " + median(connectionValues) + " / " + connectionValues.get(0));
	}

	public long mean(List<Long> values) {
		long sum = 0;
		for (int i = 0; i < values.size(); i++) {
			sum += values.get(i);
		}
		return sum / values.size();
	}

	public long median(List<Long> values) {
		int middle = values.size() / 2;
		if (values.size() % 2 == 1) {
			return values.get(middle);
		} else {
			return (values.get(middle - 1) + values.get(middle)) / 2;
		}
	}

	public static class LongComparator implements Comparator<Long> {

		@Override
		public int compare(Long o1, Long o2) {
			return Long.compare(o1, o2);
		}

	}
}
