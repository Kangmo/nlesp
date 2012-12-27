package com.thxsoft.vicdata;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/1/statuses/*")
public class Statuses extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//		log(Util.getLog(request));

		String pathInfo = request.getPathInfo();
		if (pathInfo.equals("/home_timeline.json")) {
			handleHomeTimeline(request, response);
		} else if (pathInfo.equals("/comments.json")) {
			handleComments(request, response);
		} else {
			response.setContentType("text/plain");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		log(Util.getLog(request));
		String pathInfo = request.getPathInfo();

		if (pathInfo.equals("/update.json")) {
			handleUpdate(request, response);
		} else if (pathInfo.equals("/comment.json")) {
			handleComment(request, response);
		} else {
			response.setContentType("text/plain");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void handleComment(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String accessToken = request.getParameter("access_token");
		String replyToId = request.getParameter("tweet_id");
		String tweetOwnerId = request.getParameter("tweet_owner_id");
		String comment = request.getParameter("comment");

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();

		System.out.println("comment");
		String updatedTweet;
		try {
			updatedTweet = Repo.postComment(accessToken, comment,
					Long.valueOf(replyToId), Long.valueOf(tweetOwnerId),
					getServletContext());
			out.println(updatedTweet);
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}

	private void handleComments(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		long tweetId = Long.valueOf(request.getParameter("tweet_id"));
		long tweetOwnerId = Long
				.valueOf(request.getParameter("tweet_owner_id"));

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		List<String> tweets;
		try {
			tweets = Repo.getComments(request.getParameter("access_token"),
					tweetId, tweetOwnerId, getServletContext());
			out.println("[");
			for (String tweet : tweets) {
				out.println(tweet + ',');
			}
			out.println("]");
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}

	private void handleHomeTimeline(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		long startId = Long.valueOf(request.getParameter("since_id"));
		long stopId = Long.valueOf(request.getParameter("max_id"));

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		List<String> tweets;
		try {
			tweets = Repo.getHomeTimeline(request.getParameter("access_token"),
					startId, stopId, getServletContext());
			out.println("[");
			if (tweets != null) {
				for (String tweet : tweets) {
					out.println(tweet + ',');
				}
			}
			out.println("]");
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}

	private void handleUpdate(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();

		String status = request.getParameter("status");
		String accessToken = request.getParameter("access_token");

		String newTweet;
		try {
			newTweet = Repo.postTweet(accessToken, status, getServletContext());
			out.println(newTweet);
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}
}
