package controller;

import java.util.ArrayDeque;
import java.util.Queue;

public class ElevatorController extends Thread {
	private final Queue<Message> inbox;
	private final Queue<Message> outbox;
	private double floor;
	private double targetFloor;
	private final int elevator;
	private boolean run;
	private int direction;

	public ElevatorController(int elevator) {
		direction = 0;
		floor = targetFloor = 0;
		this.elevator = elevator;
		outbox = new ArrayDeque<Message>();
		inbox = new ArrayDeque<Message>();
		run = true;
	}

	@Override
	public void run() {
		try {
			while (true) {
				while (inbox.isEmpty())
					Thread.yield();
				if (!getRun())
					return;
				Message msg = pollMessage();
				System.out.println("Msg is: " + msg);
				switch (msg.getType()) {
				case 'p':
					if (msg.getModifier() == 32000) {
						addMessage(new Message('m', elevator, 0, 0));
						direction = 0;
						continue;
					}
					if (direction != 0)
						postMessage(msg);
					else
						targetFloor = msg.getModifier();
					break;
				case 'f':
					if (direction == 0) continue;
					setFloor((direction==1?Math.floor(msg.getFloor()):Math.ceil(msg.getFloor())));
					System.err.println(getFloor() + " " + targetFloor);
					if (getFloor() == targetFloor) {
						
							addMessage(new Message('m', elevator, 0, 0));
							direction = 0;
							addMessage(new Message('d', elevator, 1, 0));
							Thread.sleep(2000);
							addMessage(new Message('d', elevator, -1, 0));
							Thread.sleep(1000);
					}
					break;
				default:
					System.out.println("Unhandled message.");
				}
				if (direction == 0) {
					int modifier;
					if(getFloor() < targetFloor) {
						System.err.println("Going up.");
						direction = 1;
						modifier = 1;
					}
					else if(getFloor() > targetFloor){
						System.err.println("Going down.");
						direction = -1;
						modifier = -1;
					}
					else{
						continue;
					}
					Message msg1 = new Message('m', elevator, modifier, 0);
					addMessage(msg1);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean getRun() {
		return run;
	}

	public synchronized void disable() {
		run = false;
	}

	public synchronized Message retrieveMessage() {
		return outbox.poll();
	}

	public synchronized void addMessage(Message msg) {
		System.err.println("Adding message: " + msg);
		outbox.add(msg);
	}

	public synchronized void postMessage(Message msg) {
		inbox.add(msg);
	}

	public synchronized Message pollMessage() {
		return inbox.poll();
	}

	public synchronized double getFloor() {
		return floor;
	}

	public synchronized void setFloor(double floor) {
		this.floor = floor;
	}

}
