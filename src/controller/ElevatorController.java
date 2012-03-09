package controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class ElevatorController extends Thread {
	private final Queue<Message> inbox;
	private final Queue<Message> outbox;
	private final ArrayList<Message> taskQueue;
	private double floor;
	private double targetFloor;
	private final int elevator;
	private int direction;
	private int intendedDirection;
	
	public ElevatorController(int elevator) {
		setDirection(0);
		setIntendedDirection(0);
		floor = setTargetFloor(0);
		this.elevator = elevator;
		taskQueue = new ArrayList<Message>();
		outbox = new ArrayDeque<Message>();
		inbox = new ArrayDeque<Message>();
	}

	@Override
	public void run() {
		try {
			while (true) {
				while (inbox.isEmpty() && taskQueue.isEmpty()){
					Thread.yield();
				}
				Message m = peekQueue();
				if (m != null) {
					setTargetFloor(m.getTargetFloor());
					if (getFloor() >= getTargetFloor() - 0.05 && getFloor() <= getTargetFloor() + 0.05) {
						doorAction();
						pollQueue();
						setDirection(0);
						continue;
					}
					decideMove();
				}
				Message msg = pollMessage();
				if (msg != null) {
					switch (msg.getType()) {
					case 'p':
						if (msg.getTargetFloor() == 32000) {
							addMessage(new Message('m', getElevator(), 0, 0));
							synchronized (this) {
								inbox.clear();
								taskQueue.clear();
							}
							setDirection(0);
							continue;
						}
						addQueue(msg);
						break;
					case 'f':
						if (getDirection() == 0)
							continue;
						setFloor(msg.getcurPos());
						if (Math.abs(getFloor() - getTargetFloor()) < 0.05) {
							addMessage(new Message('m', getElevator(), 0, 0));
							doorAction();
							pollQueue();
							setDirection(0);
						}
						break;
					default:
						System.err.println("Unhandled message.");
					}
				}
				decideMove();
				Thread.yield();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void addQueue(Message msg) {
		System.out.println("Adding task: " + msg);
		
		if (!(getDirection() * msg.getcurPos() < getDirection()
						* getFloor())) {
			for (int i = 0; i < taskQueue.size(); i++) {
				//			if (msg.getcurPos() == taskQueue.get(i).getcurPos())
				//				return;
				if (getDirection() * msg.getcurPos() < getDirection()
						* taskQueue.get(i).getcurPos()) {
					taskQueue.add(i, msg);
					return;
				}
			}
			taskQueue.add(msg);
		}else{
			System.err.println("The Elevator is not heading that way, douche.");
		}
	}

	public synchronized int getInboxSize(){
		return inbox.size();
	}
	public synchronized int getTaskQueueSize(){
		return taskQueue.size();
	}
	public synchronized Message peekQueue() {
//		System.err.println("Peeking taskQueue.");
		return taskQueue.isEmpty() ? null : taskQueue.get(0);
	}

	public synchronized Message pollQueue() {
//		System.err.println("Polling taskQueue.");
		if(taskQueue.size() == 1) setIntendedDirection(0);
		return taskQueue.isEmpty() ? null : taskQueue.remove(0);
	}

	private void decideMove() {
		if (getDirection() == 0
				&& Math.abs(getFloor() - getTargetFloor()) > 0.05) {
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
		// System.err.println("Setting direction to " + direction);
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

	public synchronized int getIntendedDirection() {
		return intendedDirection;
	}

	public synchronized void setIntendedDirection(int intendedDirection) {
		this.intendedDirection = intendedDirection;
	}

}
