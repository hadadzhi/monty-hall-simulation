package ru.cdfe.deal.representations;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceSupport;

import ru.cdfe.deal.controllers.DealController;

public class HomeResource extends ResourceSupport {
	public HomeResource() {
		add(linkTo(DealController.class).withSelfRel());
		add(linkTo(methodOn(DealController.class).listGames()).withRel("games"));
	}
}
