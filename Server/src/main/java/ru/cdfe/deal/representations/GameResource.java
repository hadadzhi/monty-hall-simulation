package ru.cdfe.deal.representations;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.core.Relation;

import ru.cdfe.deal.controllers.DealController;
import ru.cdfe.deal.game.Door;
import ru.cdfe.deal.game.Game;
import ru.cdfe.deal.game.GameStatus;

public class GameResource extends Resource<GameSummary> {
	public GameResource(Game game) {
		this(game, false);
	}
	
	public GameResource(Game game, boolean withDoors) {
		super(new GameSummary(game));
		
		add(linkTo(methodOn(DealController.class).viewGame(game.getId())).withSelfRel());
		add(linkTo(methodOn(DealController.class).deleteGame(game.getId())).withRel("delete"));
		
		if (withDoors) {
			for (Door door : game.getDoors()) {
				add(linkTo(methodOn(DealController.class).viewDoor(game.getId(), door.getId())).withRel("doors"));	
			}	
		}
	}
}

@Relation(value = "game", collectionRelation = "games")
final class GameSummary {
	private final GameStatus status;

	public GameSummary(Game game) {
		this.status = game.getStatus();
	}

	public GameStatus getStatus() {
		return status;
	}
}
