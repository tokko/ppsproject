package controller;

public class Message {

	private final char type;
	private final int elevator;
	private final int targetFloor;
	private final double curPos;

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
