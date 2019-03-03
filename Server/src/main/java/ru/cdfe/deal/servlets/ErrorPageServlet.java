package ru.cdfe.deal.servlets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/error")
public class ErrorPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger log = Logger.getLogger(this.getClass().getName());

	private void processError(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setStatus(getStatus(request));

		Map<String, Object> errorEntity = new HashMap<>();

		logError(request, errorEntity);

		if (!errorEntity.isEmpty()) {
			ObjectMapper json = new ObjectMapper();

			response.setContentType("application/json");
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());

			json.writeValue(response.getWriter(), errorEntity);
		}
	}

	private Integer getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
	
		if (statusCode == null) {
			if (exception == null) {
				// The error resource was requested directly by the client, return NOT FOUND
				return HttpServletResponse.SC_NOT_FOUND;
			} else {
				// An unexpected exception was thrown, return INTERNAL SERVER ERROR
				return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			}
		}
	
		return statusCode;
	}

	private void logError(HttpServletRequest request, Map<String, Object> errorEntity) {
		Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");

		if (exception != null) {
			String errorId = UUID.randomUUID().toString();
			String uri = (String) request.getAttribute("javax.servlet.error.request_uri");
			String message = String.format(
				"Error id: [%s], Request: [%s %s], Exception: [%s], Stack trace:", errorId, request.getMethod(), uri, exception.toString());

			log.log(Level.SEVERE, message, exception);

			errorEntity.put("timestamp", new Date().getTime() / 1000);
			errorEntity.put("errorId", errorId);
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			processError(req, resp);
		} catch (RuntimeException e) {
			throw new ServletException(e);
		}
	}
}
