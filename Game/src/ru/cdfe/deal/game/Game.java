package ru.cdfe.deal.game;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

import ru.cdfe.deal.game.exceptions.DoorDoesNotExistException;
import ru.cdfe.deal.game.exceptions.IllegalGameStateException;

public final class Game {
	private final Integer id;
	private final ConcurrentMap<Integer, Door> doors = new ConcurrentHashMap<>();
	private volatile GameStatus status = GameStatus.CREATED;
	
	public Game(Integer id) {
		this.id = id;
		generateDoors();
	}

	private void generateDoors() {
		int winningDoor = ThreadLocalRandom.current().nextInt(3);
		
		for (int i = 0; i < 3; i++) {
			doors.put(i, new Door(i, this.id, i == winningDoor ? DoorContent.PRIZE : DoorContent.EMPTY));
		}
	}
	
	public void selectDoor(int doorId) {
		validateDoor(doorId);
		
		if (status == GameStatus.CREATED) {
			doors.get(doorId).setStatus(DoorStatus.SELECTED);
			openHintDoor();
			status = GameStatus.HINTED;
		} else if (status == GameStatus.HINTED) {
			Door door = doors.get(doorId);
			
			if (door.getStatus() == DoorStatus.OPEN) {
				throw new IllegalGameStateException("This door is already open!");
			} else {
				door.setStatus(DoorStatus.OPEN);

				if (door.getContent() == DoorContent.PRIZE) {
					status = GameStatus.WON;
				} else {
					status = GameStatus.LOST;
				}
				
				openAllDoors();
			}
		} else {
			throw new IllegalGameStateException("This game is already " + (status == GameStatus.WON ? "won!" : "lost!"));
		}
	}

	private void openAllDoors() {
		for (Door door : getDoors()) {
			door.setStatus(DoorStatus.OPEN);
		}
	}

	private void validateDoor(int doorId) {
		if (doorId > 2 || doorId < 0) {
			throw new DoorDoesNotExistException("Door " + doorId + " does not exist!");
		}
	}
	
	private void openHintDoor() {
		for (Door door : doors.values()) {
			if (door.getStatus() == DoorStatus.CLOSED) {
				door.setStatus(DoorStatus.OPEN);
				
				if (door.getContent() == DoorContent.PRIZE) {
					door.setStatus(DoorStatus.CLOSED);
				} else {
					break;
				}
			}
		}
	}

	public Integer getId() {
		return this.id;
	}
	
	public GameStatus getStatus() {
		return this.status;
	}
	
	public Collection<Door> getDoors() {
		return Collections.unmodifiableCollection(doors.values());
	}
	
	public Door getDoor(Integer id) {
		validateDoor(id);
		return doors.get(id);
	}
}
