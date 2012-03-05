package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import elevator.Elevators;

public class MasterController extends Thread {
	private Socket s;
	private BufferedReader in;
	private PrintWriter out;
	private final ArrayList<ElevatorController> controllers;

	public MasterController() {
		controllers = new ArrayList<ElevatorController>();

	}

	// public static void main(String[] args) {
	// new MasterController().doit();
	// }

	@Override
	public void run() {
		while (true) {
			try {
				s = new Socket("localhost", 4711);
				in = new BufferedReader(new InputStreamReader(
						s.getInputStream()));
				out = new PrintWriter(s.getOutputStream(), true);
				doit2();
			} catch (UnknownHostException e) {
				continue;
			} catch (IOException e) {
				continue;
			}
			break;
		}
	}

	@SuppressWarnings("unused")
	private void doit() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				while (true) {
					try {
						out.println(br.readLine());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		while (true)
			try {
				System.out.println(in.readLine());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private void doit2() throws SocketException {
		s.setSoTimeout(50);
		System.err.println("Controller starts.");
		for (int i = 0; i <= Elevators.numberOfElevators; i++) {
			controllers.add(new ElevatorController(i+1));
			controllers.get(i).start();
		}
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					for (ElevatorController c : controllers) {
						Message msg = null;
						if ((msg = c.retrieveMessage()) != null) {
							System.out.println("Retrieving message: " + msg);
							out.println(msg.toString());
						}
					}
					Thread.yield(); // XXX: This might be bad.
				}
			}
		}).start();
		while (true) {
			while (!s.isInputShutdown()) {

				String[] message = null;
				try {
					message = in.readLine().split(" ");
					char type = message[0].charAt(0);
					int elevator = Integer.valueOf(message[1]);
					double modifier = Double.valueOf(message[2]);
					System.err.println("MESSAGE FROM ELEVATOR: "+type + " " + elevator + " " + modifier);
					if(type == 'b'){
						new Assigner((int)elevator, (int)modifier).start();
						continue;
					}
					Message msg = new Message(type, elevator, (int) modifier,
							modifier);
					System.err.println("Posting message: " + msg);
					controllers.get(elevator-1).postMessage(msg);
				} catch (Exception e) {
				}

			}
		}
	}

	private class Assigner extends Thread {

		private final int floor;
		private final int direction;

		public Assigner(int floor, int direction) {
			this.floor = floor;
			this.direction = direction;
		}

		@Override
		public void run() {
			ElevatorController e = controllers.get(0);
			for (ElevatorController controller : controllers) {
				if (controller.getDirection() == 0) {
					e = controller;
					break;
				}
			}
			Message m = new Message('p', e.getElevator(), floor, floor);
			System.out.println(m);
			e.postMessage(m);
		}
	}
}
