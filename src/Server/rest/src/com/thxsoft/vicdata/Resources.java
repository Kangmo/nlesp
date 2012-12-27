package com.thxsoft.vicdata;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/1/resources/*")
public class Resources extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String URL_USER_IMAGE = "http://nhnsoft.com:8080/rest/1/resources/user/";

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log(Util.getLog(request));

		String pathInfo = request.getPathInfo();
		if (pathInfo.startsWith("/user/")) {
			handleUserImageRequest(request, response);
		} else {
			response.setContentType("text/plain");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void handleUserImageRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String path = request.getPathInfo();
		Long userId = Long.valueOf(path.substring("/user/".length()));
//		System.out.println("get image resource.");

		byte[] rawData;
		ServletOutputStream out = response.getOutputStream();
		try {
			rawData = Repo.getUserImage(request.getParameter("access_token"),
					Long.toString(userId), getServletContext());
			if (rawData == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			System.out.println("image size for uid(" + userId + ") = "
					+ rawData.length);

			out.write(rawData);

			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ userId + "\"");
			response.setContentType("image/png");
			response.setContentLength(rawData.length);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}
}
