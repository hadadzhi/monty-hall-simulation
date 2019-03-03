package ru.cdfe.deal.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.cdfe.deal.game.Game;
import ru.cdfe.deal.game.GameRepository;
import ru.cdfe.deal.game.exceptions.DoorDoesNotExistException;
import ru.cdfe.deal.game.exceptions.GameDoesNotExistException;
import ru.cdfe.deal.game.exceptions.IllegalGameStateException;
import ru.cdfe.deal.game.exceptions.IllegalGameStateTransitionException;
import ru.cdfe.deal.representations.DoorResource;
import ru.cdfe.deal.representations.GameResource;
import ru.cdfe.deal.representations.GameResources;
import ru.cdfe.deal.representations.HomeResource;

@RestController
@RequestMapping("deal")
public class DealController {
	private final GameRepository games;
	
	@Autowired
	public DealController(GameRepository gameRepository) {
		this.games = gameRepository;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public HomeResource home() {
		return new HomeResource();
	}

	@RequestMapping(path = "games", method = RequestMethod.POST)
	public ResponseEntity<Void> createGame() {
		Game game = games.create();
		HttpHeaders headers = new HttpHeaders();
		
		headers.setLocation(linkTo(methodOn(DealController.class).viewGame(game.getId())).toUri());
		
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	@RequestMapping(path = "games", method = RequestMethod.GET)
	public GameResources listGames() {
		return new GameResources(games.findAll());
	}
	
	@RequestMapping(path = "games/{id:[0-9]+}", method = RequestMethod.GET)
	public GameResource viewGame(@PathVariable("id") Integer id) {
		return new GameResource(games.findOne(id), true);
	}

	@RequestMapping(path = "games/{id:[0-9]+}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteGame(@PathVariable("id") Integer id) {
		games.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(path = "games/{gameId:[0-9]+}/doors/{id:[0-9]+}", method = RequestMethod.GET)
	public DoorResource viewDoor(@PathVariable("gameId") Integer gameId, @PathVariable("id") Integer id) {
		return new DoorResource(games.findOne(gameId).getDoor(id));
	}

	@RequestMapping(path = "games/{gameId:[0-9]+}/doors/{id:[0-9]+}", method = RequestMethod.POST)
	public ResponseEntity<Void> selectDoor(@PathVariable("gameId") Integer gameId, @PathVariable("id") Integer id) {
		games.findOne(gameId).selectDoor(id);		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}

@RestControllerAdvice(assignableTypes = DealController.class)
class DealExceptionHandler {
	@ExceptionHandler({ GameDoesNotExistException.class, DoorDoesNotExistException.class })
	public ResponseEntity<Void> handleNotFound(Throwable e) {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler({ IllegalGameStateException.class, IllegalGameStateTransitionException.class })
	public ResponseEntity<Void> handleConflict(Throwable e) {
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
}
