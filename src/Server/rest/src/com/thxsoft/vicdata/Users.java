package com.thxsoft.vicdata;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/1/users/*")
public class Users extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		log(Util.getLog(request));

		String pathInfo = request.getPathInfo();
		if (pathInfo.equals("/show.json")) {
			handleShow(request, response);
		} else if (pathInfo.equals("/lookup.json")) {
			handleLookup(request, response);
		} else if (pathInfo.equals("/search.json")) {
			handleSearch(request, response);
		} else {
			response.setContentType("text/plain");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log(Util.getLog(request));

		response.setContentType("text/plain");
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	private void handleLookup(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		try {
			out.println("[");

			StringTokenizer userIds = new StringTokenizer(
					request.getParameter("user_id"), ",");
			while (userIds.hasMoreTokens()) {
				long userId = Long.valueOf(userIds.nextToken());

				out.println(Repo.getUserProfile(
						request.getParameter("access_token"), userId,
						getServletContext()));
				if (userIds.hasMoreTokens()) {
					out.println(",");
				}
			}
			out.println("]");

		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}

	private void handleSearch(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		String users;
		try {
			users = Repo.searchUserByEmail(
					request.getParameter("access_token"),
					request.getParameter("q"), getServletContext());
			out.println(users);
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}

	private void handleShow(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		long userId = Long.valueOf(request.getParameter("user_id"));

		PrintWriter out = response.getWriter();
		try {
			out.println(Repo.getUserProfile(
					request.getParameter("access_token"), userId,
					getServletContext()));
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}

}
