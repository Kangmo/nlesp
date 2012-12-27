package com.thxsoft.vicdata;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/1/followers/*")
public class Followers extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//		log(Util.getLog(request));

		String pathInfo = request.getPathInfo();
		if (pathInfo.equals("/ids.json")) {
			handleIds(request, response);
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

	private void handleIds(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String ids;
		try {
			ids = Repo.getFollowerIds(request.getParameter("access_token"),
					getServletContext());
			System.out.println("Follower IDs : " + ids);
			out.println("[" + ids + "]");
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}

}
