package server;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.Transport;

public class Server implements AutoCloseable {

	private ServerSocket serverSocket;
	private ExecutorService executorService;
	private List<Socket> clients = Collections.synchronizedList(new ArrayList<Socket>());
	private Map<Socket, Map<String, List<String>>> scriptsNoi = Collections.synchronizedMap(new HashMap<>());
	private Set<String> uniqueNames = Collections.synchronizedSet(new HashSet<>());

	@Override
	public void close() throws Exception {
		stop();
	}

	public void start(int port) throws IOException {
		stop();
		serverSocket = new ServerSocket(port);
		executorService = Executors.newFixedThreadPool(10 * Runtime.getRuntime().availableProcessors());
		executorService.execute(() -> {
			while (serverSocket != null && !serverSocket.isClosed()) {
				try {
					Socket socket = serverSocket.accept();
					executorService.execute(() -> {
						try {
							clients.add(socket);
							getMenu(socket);
							scriptsNoi.put(socket, new HashMap<>());
							Transport.send("~Publish your list of scrips~ How many do you wish to enter?", socket);
							while (socket != null && !socket.isClosed()) {
								String message = Transport.receive(socket);
								clients.forEach(client -> {
									try {
										if (client.getPort() == socket.getPort()) {
											String response = processCommand(message, client);
											Transport.send(response, client);
										}
									} catch (IOException e) {
										e.printStackTrace();
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								});
							}
						} catch (Exception e) {
							System.out.println(e.getMessage());
						} finally {
							clients.remove(socket);
						}
					});
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		});
	}

	private void getMenu(Socket socket) throws Exception {
		Transport.send("      [[[ Available Commands ]]]", socket);
		Transport.send("  •   add [noItems]", socket);
		Transport.send("  •   overwrite [commandName]", socket);
		Transport.send("  •   delete [commandName]", socket);
		Transport.send("  •   list", socket);
		Transport.send("  •   execute [commandName]", socket);
		Transport.send("  •   menu", socket);
		Transport.send("      ", socket);
	}

	private String addScriptsToCommand(String request, Socket client) throws Exception {
		String[] items = request.strip().split("\\s");
		if (items.length == 2) { // add nr
			if (!isNumeric(items[1]))
				return "Insert a number!";
			int noItems = Integer.parseInt(items[1]);
			if (noItems > 0) {
				Map<String, List<String>> scripturiClient = scriptsNoi.get(client);
				List<String> clientScripts = new ArrayList<String>();
				Transport.send("Command name (must be unique):", client);
				String name = Transport.receive(client);
				while (!uniqueNames.add(name.trim())) {
					Transport.send("This is not an unique name!", client);
					Transport.send("Command name (must be unique):", client);
					name = Transport.receive(client);
				}
				Transport.send("     You entered: " + name, client);
				for (int i = 0; i < noItems; i++) {
					Transport.send("Item number " + (i + 1) + ":", client);
					String item = Transport.receive(client);
					Transport.send("     You entered: " + item, client);
					clientScripts.add(item);
				}
				scripturiClient.put(name, clientScripts);
				scriptsNoi.put(client, scripturiClient);
				Transport.send("Done. You can't modify your list anymore.", client);
				return "";

			} else {
				return "Invalid number.";
			}
		} else {
			return "Wrong number of parameters.";
		}
	}

	private String modifyCommand(String request, Socket client, List<String> toModify) throws Exception {
		String[] items = request.strip().split("\\s");
		if (items.length == 2) {
			if (!isNumeric(items[1]))
				return "Insert a number!";
			int noItems = Integer.parseInt(items[1]);
			if (noItems > 0) {
				for (int i = 0; i < noItems; i++) {
					Transport.send("Item number " + (i + 1) + ":", client);
					String item = Transport.receive(client);
					Transport.send("     You entered: " + item, client);
					toModify.add(item);
				}
				Transport.send("Done. You can't modify your list anymore.", client);
				return "";

			} else {
				return "Invalid number.";
			}
		} else {
			return "Wrong number of parameters.";
		}
	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			int d = Integer.parseInt(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	private String processCommand(String request, Socket client) throws Exception {
		Transport.send("     You entered: " + request, client);
		String[] items = request.strip().split("\\s");
		if (items[0].equals("add")) {
			return addScriptsToCommand(request, client);
		} else if (items[0].equals("menu")) {
			getMenu(client);
			return "";
		} else if (items[0].equals("list")) {
			if (items.length == 1) {
				if (scriptsNoi.get(client) != null && scriptsNoi.get(client).size() > 0) {
					String result = "";
					for (Map.Entry<String, List<String>> entry : scriptsNoi.get(client).entrySet()) {
						result += ("Unique command name: " + entry.getKey() + ". Scripts: ");
						for (String script : entry.getValue()) {
							result += (script + "; ");
						}
					}
					return result;
				} else {
					return "You don't have any scripts.";
				}
			} else {
				return "Wrong number of parameters.";
			}
		} else if (items[0].equals("overwrite")) {
			if (items.length == 2) {
				Map<String, List<String>> scripturiClient = scriptsNoi.get(client);
				if (uniqueNames.contains(items[1].trim()) && scripturiClient.containsKey(items[1])) {
					List<String> toModify = scripturiClient.get(items[1]);
					toModify.clear(); // golesc tot
					Transport.send("How many do you wish to enter?", client);
					String req2 = Transport.receive(client);
					return modifyCommand(req2, client, toModify);

				} else {
					return "No such command.";
				}
			} else {
				return "Wrong number of parameters.";
			}
		} else if (items[0].equals("delete")) {
			if (items.length == 2) {
				Map<String, List<String>> scripturiClient = scriptsNoi.get(client);
				if (uniqueNames.contains(items[1].trim()) && scripturiClient.containsKey(items[1])) {
					scripturiClient.remove(items[1]);
					uniqueNames.remove(items[1]);
					return "Deleted "+items[1]+".";

				} else {
					return "No such command.";
				}
			} else {
				return "Wrong number of parameters.";
			}
		} else if (items[0].equals("execute")) {
			if (items.length == 2) {
				Map<String, List<String>> scripturiClient = scriptsNoi.get(client);
				if (uniqueNames.contains(items[1].trim()) && scripturiClient.containsKey(items[1])) {
					List<String> toModify = scripturiClient.get(items[1]);
					for (int i = 0; i < toModify.size(); i++) {
						try {
							Runtime.getRuntime().exec("cmd /c start \"\" " + toModify.get(i));
						} catch (IOException e1) {
							e1.printStackTrace();
							Transport.send("Error running: " + e1.getMessage(), client);
						}
					}
					return "Executed.";
				} else {
					return "No such command.";
				}

			} else {
				return "Wrong number of parameters.";
			}
		} else {
			return "Unknown command.";
		}
	}

	public void stop() throws IOException {
		if (executorService != null) {
			executorService.shutdown();
		}
		if (serverSocket != null) {
			serverSocket.close();
		}
	}
}
