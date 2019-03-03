package ru.cdfe.deal.representations;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.hateoas.Resources;

import ru.cdfe.deal.controllers.DealController;
import ru.cdfe.deal.game.Game;

public class GameResources extends Resources<GameResource> {
	public GameResources(Collection<Game> games) {
		super(games.stream().map(GameResource::new).collect(Collectors.toList()));
		add(linkTo(methodOn(DealController.class).listGames()).withSelfRel());
		add(linkTo(methodOn(DealController.class).createGame()).withRel("create"));
	}
}
