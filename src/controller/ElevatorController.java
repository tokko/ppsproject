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
		setDirection(0);
		floor = setTargetFloor(0);
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
						inbox.clear();
						setDirection(0);
						continue;
					}
					if (getDirection() != 0)
						postMessage(msg);
					else
						setTargetFloor(msg.getModifier());
					break;
				case 'f':
					if (getDirection() == 0) continue;
					setFloor((getDirection()==1?Math.floor(msg.getFloor()):Math.ceil(msg.getFloor())));
					System.err.println("Floor targetFloor: " + getFloor() + " " + getTargetFloor());
					if (getFloor() == getTargetFloor()) {
						
							addMessage(new Message('m', elevator, 0, 0));
							setDirection(0);
							doorAction();
					}
					break;
				default:
					System.out.println("Unhandled message.");
				}
				if (getDirection() == 0) {
					int modifier;
					if(getFloor() < getTargetFloor()) {
						System.err.println("Going up.");
						setDirection(1);
						modifier = 1;
					}
					else if(getFloor() > getTargetFloor()){
						System.err.println("Going down.");
						setDirection(-1);
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

	private void doorAction() throws InterruptedException {
		addMessage(new Message('d', elevator, 1, 0));
		Thread.sleep(2000);
		addMessage(new Message('d', elevator, -1, 0));
		Thread.sleep(1000);
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

	public synchronized int getDirection() {
		return direction;
	}

	public synchronized void setDirection(int direction) {
		this.direction = direction;
	}

	public synchronized double getTargetFloor() {
		return targetFloor;
	}

	public synchronized double setTargetFloor(double targetFloor) {
		this.targetFloor = targetFloor;
		return targetFloor;
	}

}
