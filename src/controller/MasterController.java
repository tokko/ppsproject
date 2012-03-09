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
		System.err.println("Controller starts.");
		for (int i = 0; i < Elevators.numberOfElevators; i++) {
			controllers.add(new ElevatorController(i + 1));
			controllers.get(i).start();
		}
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					for (ElevatorController c : controllers) {
						Message msg = null;
						if ((msg = c.retrieveMessage()) != null) {
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
					// System.out.println("Message is: " + message[0] + " " +
					// message[1] + " " + message[2]);
					char type = message[0].charAt(0);
					int elevator = Integer.valueOf(message[1]);
					double modifier = Double.valueOf(message[2]);
<<<<<<< HEAD
					//System.err.println("MESSAGE FROM ELEVATOR: "+type + " " + elevator + " " + modifier);
					if(type == 'b'){
						new Assigner((int)elevator, (int)modifier).start();
=======
					if (type == 'b') {
						new Assigner((int) elevator, (int) modifier).start();
>>>>>>> 13833474f6d26dab3897b9275990350828b22a73
						continue;
					}
					Message msg = new Message(type, elevator, (int) modifier,
							modifier);
					controllers.get(elevator - 1).postMessage(msg);
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
			ElevatorController closestEmpty = null, closestJoin = null;
			double costEmpty, costJoin;
			costEmpty = costJoin = Double.POSITIVE_INFINITY;
			for (boolean first = true; closestEmpty == null
					&& closestJoin == null; first = false) {
				if (!first)
					Thread.yield();

				for (ElevatorController c : controllers) {
					// Join to a current tour
					if (c.getIntendedDirection() == direction
							&& c.getDirection() * c.getFloor() < c
									.getDirection() * floor) {
						double thisCost = Math.abs(floor - c.getFloor()) + 2
								* c.getTaskQueueSize();
						if (thisCost < costJoin) {
							costJoin = thisCost;
							closestJoin = c;
						}
					}else if (c.getIntendedDirection()!=c.getDirection() && c.getIntendedDirection()==direction){
						double thisCost = Math.abs(floor - c.getFloor()) + 2
								* c.getTaskQueueSize();
						if (thisCost < costJoin) {
							costJoin = thisCost;
							closestJoin = c;
						}
					}

					// Assign Empty ELevator
					if (c.getTaskQueueSize() == 0 && c.getInboxSize() == 0) {
						double thisCost = Math.abs(floor - c.getFloor()) + 2
								* c.getTaskQueueSize();
						if (thisCost < costEmpty) {
							costEmpty = thisCost;
							closestEmpty = c;
						}
					}
				}
			}
<<<<<<< HEAD
			//Message m = new Message('p', e.getElevator(), floor, floor);
			Message m = new Message('p', 2313, floor, floor);
			System.out.println(m);
			e.postMessage(m);
=======
			ElevatorController cf = closestEmpty;
			if (closestJoin != null)
				cf = closestJoin;
			else
				cf.setIntendedDirection(direction);

			Message m = new Message('p', cf.getElevator(), floor, floor);
			System.out.println("ASSIGNER MESSAGE: " + m);
			cf.postMessage(m);

>>>>>>> 13833474f6d26dab3897b9275990350828b22a73
		}
	}
}
