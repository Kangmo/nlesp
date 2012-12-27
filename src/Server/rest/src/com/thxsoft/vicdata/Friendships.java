package com.thxsoft.vicdata;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/1/friendships/*")
public class Friendships extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//		log(Util.getLog(request));

		response.setContentType("text/plain");
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log(Util.getLog(request));

		String pathInfo = request.getPathInfo();
		if (pathInfo.equals("/create.json")) {
			long friendId = Long.valueOf(request.getParameter("user_id"));
			System.out.println("Create friendship with: " + friendId);

			handleCreate(request, friendId, response);
		} else if (pathInfo.equals("/destroy.json")) {
			long friendId = Long.valueOf(request.getParameter("user_id"));
			System.out.println("Destory Friendship with: " + friendId);

			handleDestory(request, friendId, response);
		} else {
			response.setContentType("text/plain");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void handleCreate(HttpServletRequest request, long friendId,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();

		try {
			out.println(Repo.createFriendship(
					request.getParameter("access_token"), friendId,
					getServletContext()));
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}

	private void handleDestory(HttpServletRequest request, long friendId,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();

		try {
			out.println(Repo.destroyFriendship(
					request.getParameter("access_token"), friendId,
					getServletContext()));
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}
}
