package controller;

import java.util.ArrayDeque;
import java.util.Queue;

public class ElevatorController extends Thread {
	private final Queue<Message> inbox;
	private final Queue<Message> outbox;
	private final Queue<Message> taskQueue;
	private double floor;
	private double targetFloor;
	private final int elevator;
	private boolean run;
	private int direction;

	public ElevatorController(int elevator) {
		setDirection(0);
		floor = setTargetFloor(0);
		this.elevator = elevator;
		taskQueue = new ArrayDeque<Message>();
		outbox = new ArrayDeque<Message>();
		inbox = new ArrayDeque<Message>();
		run = true;
	}

	@Override
	public void run() {
		try {
			while (true) {
				while (inbox.isEmpty() && taskQueue.isEmpty())
					Thread.yield();
				if (!getRun())
					return;
				Message msg = pollMessage();
				if (getDirection() == 0) {
					Message m = pollQueue();
					if (m != null) {
						System.err.println("Pulling task: " + m);
						setTargetFloor(m.getModifier());
						decideMove();
					}
				}
				if (msg != null) {
					switch (msg.getType()) {
					case 'p':
						if (msg.getModifier() == 32000) {
							addMessage(new Message('m', getElevator(), 0, 0));
							inbox.clear();
							setDirection(0);
							continue;
						}
						System.err.println("Enquing task: " + msg);
						addQueue(msg);
						break;
					case 'f':
						if (getDirection() == 0)
							continue;
						setFloor(msg.getFloor());
						if(Math.abs(getFloor()-getTargetFloor())<0.05){
							addMessage(new Message('m', getElevator(), 0, 0));
							setDirection(0);
							doorAction();
						}
						break;
					default:
						System.out.println("Unhandled message.");
					}
				}
				decideMove();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void addQueue(Message msg) {
		taskQueue.add(msg);
	}

	public synchronized Message pollQueue() {
		return taskQueue.poll();
	}

	private void decideMove() {
		if (getDirection() == 0 && Math.abs(getFloor() - getTargetFloor()) > 0.05) {
			System.err.println("DECIDE MOVE");
			int modifier;
			if (getFloor() < getTargetFloor() - 0.05) {
				System.err.println("Going up.");
				setDirection(1);
				modifier = 1;
			} else if (getFloor() > getTargetFloor() + 0.05) {
				System.err.println("Going down.");
				setDirection(-1);
				modifier = -1;
			} else {
				return;
			}
			Message msg1 = new Message('m', getElevator(), modifier, 0);
			addMessage(msg1);
		}
	}

	private void doorAction() throws InterruptedException {
		addMessage(new Message('d', getElevator(), 1, 0));
		Thread.sleep(1000);
		addMessage(new Message('d', getElevator(), -1, 0));
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
//		System.err.println("Setting direction to " + direction);
		this.direction = direction;
	}

	public synchronized double getTargetFloor() {
		return targetFloor;
	}

	public synchronized double setTargetFloor(double targetFloor) {
		this.targetFloor = targetFloor;
		return targetFloor;
	}

	public int getElevator() {
		return elevator;
	}

}
