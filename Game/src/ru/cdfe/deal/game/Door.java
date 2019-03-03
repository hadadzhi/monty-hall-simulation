package ru.cdfe.deal.game;

public final class Door {
	private volatile DoorStatus status = DoorStatus.CLOSED;
	private final DoorContent content;
	private final Integer id;
	private final Integer gameId;
	
	public Door(Integer id, Integer gameId, DoorContent content) {
		this.id = id;
		this.gameId = gameId;
		this.content = content;
	}

	public DoorStatus getStatus() {
		return status;
	}

	void setStatus(DoorStatus status) {
		this.status = status;
	}

	public DoorContent getContent() {
		if (status != DoorStatus.OPEN) {
			throw new IllegalStateException("Can't see what's behind a closed door!");
		}
		
		return content;
	}
	
	public Integer getId() {
		return id;
	}

	public Integer getGameId() {
		return gameId;
	}
}
