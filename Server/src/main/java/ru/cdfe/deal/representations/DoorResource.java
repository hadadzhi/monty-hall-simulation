package ru.cdfe.deal.representations;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.cdfe.deal.controllers.DealController;
import ru.cdfe.deal.game.Door;
import ru.cdfe.deal.game.DoorContent;
import ru.cdfe.deal.game.DoorStatus;

public class DoorResource extends Resource<DoorSummary> {
	public DoorResource(Door door) {
		super(new DoorSummary(door));
		add(linkTo(methodOn(DealController.class).viewDoor(door.getGameId(), door.getId())).withSelfRel());
		add(linkTo(methodOn(DealController.class).selectDoor(door.getGameId(), door.getId())).withRel("select"));
	}
}

@Relation(value = "door", collectionRelation = "doors")
@JsonInclude(Include.NON_NULL)
final class DoorSummary {
	private final DoorStatus status;
	private final DoorContent content;
	
	public DoorSummary(Door door) {
		this.status = door.getStatus();
		
		if (door.getStatus() == DoorStatus.OPEN) {
			this.content = door.getContent();
		} else {
			this.content = null;
		}
	}

	public DoorStatus getStatus() {
		return status;
	}

	public DoorContent getContent() {
		return content;
	}
}
