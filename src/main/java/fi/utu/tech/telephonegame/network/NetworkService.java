package fi.utu.tech.telephonegame.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import static java.util.Collections.synchronizedList;

/**
 * A concrete implementation of Network interface.
 * Should be able to listen for peers, connect to
 * peer network (by connecting to a peer in existing
 * peer network) as well as send and receive
 * messages from neighbouring peers
 * 
 * You probably need to create more methods and attributes
 * to this class as well as create additional classes
 * to be able to implement all the required functionality
 */
public class NetworkService extends Thread implements Network {
	private final List<ObjectOutputStream> osList = synchronizedList(new ArrayList<>());
	private final LinkedBlockingQueue<Serializable> outputList = new LinkedBlockingQueue<>(5);
	private final LinkedBlockingQueue<Serializable> inputList = new LinkedBlockingQueue<>(5);
	/*
	 * No need to change the construtor
	 */
	public NetworkService() {
		this.start();
	}

	/**
	 * Creates a server instance and starts listening for new peers on specified port
	 * 
	 * The port used for listening for incoming connections is provided automatically
	 * by the Resolver upon calling.
	 * 
	 * @param serverPort Which port should we start to listen to?
	 * 
	 */
	public void startListening(int serverPort) {
        System.out.printf("I should start listening for new peers at TCP port %d%n", serverPort);
        // TODO
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
		// Accepting incoming connections in a new thread
        new Thread(new Listener(serverSocket)).start();
	}

	/**
	 * This method will be called when connecting to a peer (other whispers
	 * instance)
	 * The IP address and port will be provided by the template (by the resolver)
	 * upon calling.
	 * 
	 * @param peerIP   The IP address to connect to
	 * @param peerPort The TCP port to connect to
	 */
	public void connect(String peerIP, int peerPort) throws IOException, UnknownHostException {
		System.out.printf("I should connect myself to %s, TCP port %d%n", peerIP, peerPort);
		// TODO
		Socket socket = null;
		try {
			socket = new Socket(peerIP, peerPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Handling connections in a new thread
		new Thread(new Connector(socket)).start();
    }

	/**
	 * This method is used to send the message to all connected neighbours (directly connected nodes)
	 * 
	 * @param out The serializable object to be sent to all the connected nodes
	 * 
	 */
	private void sendToNeighbours(Serializable out) throws IOException {
		// Send the object to all neighbouring nodes
		// TODO
		if (!osList.isEmpty()) {
			for (ObjectOutputStream value : osList) {
				value.writeObject(out);
				value.flush();
				System.out.println("Message sent");
			}
		} else {
			System.out.println("No recipient, clearing outgoing messages");
			outputList.clear();
		}
	}
	
	/**
	 * Add an object to the queue for sending it to the peer network (all neighbours)
	 * 
	 * Note: This method is made for others to use. Ie. The implementation
	 * here is called by others (eg. MessageBroker) to post messages INTO
	 * network. To work out the structure of the internal implementation
	 * see run method and sendToNeighbours
	 * 
	 * @param out The Serializable object to be sent
	 */
	public void postMessage(Serializable out) {
		//TODO
		try {
			outputList.put(out);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the next message from the queue of received messages.
	 * 
	 * Note: This method is made for others to use. Ie. The implementation
	 * here is called by others (eg. MessageBroker) to get access to messages
	 * received from the peer network.
	 * 
	 * @return The next message
	 */
	public Object retrieveMessage() throws InterruptedException {
		//TODO
		if (inputList != null) {
            return inputList.take();
		} else {
			return null;
		}
	}

	/**
	 * Waits for messages from the core application and forwards them to the network
	 * 
	 * Ie. When MessageBroker calls postMessage, the message-to-be-sent should be spooled
	 * into some kind of a producer-consumer-friendly data structure and picked up here for
	 * the actual delivery over sockets.
	 * 
	 * Thread running this method is started in the constructor of NetworkService.
	 * 
	 */
	public void run() {
		// TODO

		while (true) {
			try {
				// We do not have structure (yet) where messages being sent are spooled
				if (outputList != null && osList != null) {
					Serializable toBeSent = outputList.take();
					sendToNeighbours(toBeSent);
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}

	}
	/**
	 * Accepting incoming connections in a new thread
	 */
	public class Listener extends Thread {
		private ServerSocket socket;
		public Listener(ServerSocket socket) {
			this.socket = socket;
		}
		public void run () {
			while (true) {
                Socket clientSocket = null;
                try {
					clientSocket = socket.accept();
					ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
					osList.add(os);
				} catch (SocketException e) {
					System.out.println("waaaa");
					if (socket.isClosed()) {
						System.out.println("piupau");
					}
                } catch (IOException e ) {
                    throw new RuntimeException(e);
                }
                System.out.println("New client connected: " + clientSocket.getInetAddress());
			}
		}
	}
	/**
	 * Handling connections in a new thread
	 */
	public class Connector extends Thread {
		private Socket socket;
		public Connector(Socket socket) {
			this.socket = socket;
		}
		public void run () {
			ObjectInputStream is = null;
			try {
				System.out.println("New server connected: " + socket.getInetAddress());
				is = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				System.out.println("virhe");
			}
			while (true) {
				assert is != null;
                try {
                    Serializable receivedObject = (Serializable) is.readObject();
					System.out.println("Received an object from the server\n");
					inputList.put(receivedObject);
				} catch (IOException | InterruptedException | ClassNotFoundException e) {
					System.out.println("waaaa");
					System.out.println(e);
				}
			}
		}
	}

}
