import java.io.Console;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class WebPing implements Runnable {
	private List<Long> executionValues = new ArrayList<Long>();
	private List<Long> connectionValues = new ArrayList<Long>();
	private List<Long> sqlExecutionValues = new ArrayList<Long>();

	public static void main(String[] args) throws Exception {
		WebPing webPing = new WebPing();
		Runtime.getRuntime().addShutdownHook(new Thread(webPing));
		String serverName = args.length < 1 ? "www.google.com" : args[0];
		int port = args.length < 2 ? 80 : Integer.parseInt(args[1]);
		String database = args.length < 3 ? null : args[2];
		String userName = args.length < 4 ? null : args[3];
		char[] password = null;
		if (userName != null) {
			DriverManager.registerDriver((Driver) Class.forName("oracle.jdbc.driver.OracleDriver").newInstance());
			password = System.console().readPassword("Database Password :");
		}
		while (true) {
			webPing.executeTest(serverName, port);
			if (userName != null && password != null) {
				webPing.executeSQLTest("jdbc:oracle:thin:@" + serverName + ":" + port + "/" + database, userName,
						password);
			}
			Thread.sleep(100);
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

	public void executeSQLTest(String jdbcURL, String userName, char[] password) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			long sqlExecutionStart = System.currentTimeMillis();
			conn = DriverManager.getConnection(jdbcURL, userName, new String(password));
			ps = conn.prepareStatement("select sysdate from dual");
			resultSet = ps.executeQuery();
			while (resultSet.next()) {
				resultSet.getString(1);
			}
			long sqlExecutionEnd = System.currentTimeMillis();
			sqlExecutionValues.add(sqlExecutionEnd - sqlExecutionStart);
		} catch (Exception exception) {
			System.out.println("Can't connect to Database " + jdbcURL + " (" + userName + ")");
			System.out.println(exception);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (Exception exception) {
					System.out.println("Failed to close resultset");
					System.out.println(exception);
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception exception) {
					System.out.println("Failed to close prepared statement");
					System.out.println(exception);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception exception) {
					System.out.println("Failed to close connection");
					System.out.println(exception);
				}
			}
		}
	}

	public void run() {
		long numberOfExecution = executionValues.size();
		System.out.println("Total Execution              : " + numberOfExecution);
		Collections.sort(executionValues, new LongComparator());
		Collections.sort(connectionValues, new LongComparator());
		Collections.sort(sqlExecutionValues, new LongComparator());
		System.out.println("Execution time (max/avg/median/min) : " + executionValues.get(executionValues.size() - 1)
				+ " / " + mean(executionValues) + " / " + median(executionValues) + " / " + executionValues.get(0));
		System.out.println("Connection time (max/avg/median/min) : " + connectionValues.get(connectionValues.size() - 1)
				+ " / " + mean(connectionValues) + " / " + median(connectionValues) + " / " + connectionValues.get(0));
		if (sqlExecutionValues.size() > 0)
			System.out.println("SQL time (count/max/avg/median/min)    : "
					+ sqlExecutionValues.size() +" / " + sqlExecutionValues.get(sqlExecutionValues.size() - 1) + " / " + mean(sqlExecutionValues) + " / "
					+ median(sqlExecutionValues) + " / " + sqlExecutionValues.get(0));
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
