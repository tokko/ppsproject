package controller;

/**
 * Message used to communicate between threads.
 * The class consists of getters and setters and overrides toString and equals for our convenience. 
 * @author Mattias Knutsson and Andreas Gustafsson
 */
public class Message {

	private final char type;				//The type of the message (F,P,M etc.)
	private final int elevator;				//The ID of the elevator
	private final int targetFloor;			//The target floor
	private final double curPos;			//The current position

	public Message(char type, int elevator, int targetFloor, double curPos) {
		this.type = type;
		this.elevator = elevator;
		this.targetFloor = targetFloor;
		this.curPos = curPos;
	}

	public char getType() {
		return type;
	}

	public int getElevator() {
		return elevator;
	}

	public double getTargetFloor() {
		return targetFloor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Message) {
			Message m = (Message) obj;
			return type == m.type && elevator == m.elevator
					&& targetFloor == m.targetFloor;
		}
		return false;
	}

	@Override
	public String toString() {
		if(type == 'f')
			return type + " " + elevator + " " + curPos;
		else 
			return type + " " + elevator + " " + targetFloor;
	}

	public double getcurPos() {
		return curPos;
	}
}
