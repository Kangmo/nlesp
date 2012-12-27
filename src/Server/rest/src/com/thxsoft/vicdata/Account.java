package com.thxsoft.vicdata;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/1/account/*")
@MultipartConfig
public class Account extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// log(Util.getLog(request));

		response.setContentType("text/plain");
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		log(Util.getLog(request));

		String pathInfo = request.getPathInfo();
		if (pathInfo.equals("/login.json")) {
			handleLogin(request, response);
		} else if (pathInfo.equals("/create_user_account.json")) {
			handleCreateAccount(request, response);
		} else if (pathInfo.equals("/update_profile.json")) {
			handleUpdateProfile(request, response);
		} else {
			response.setContentType("text/plain");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void handleCreateAccount(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		String loginId = request.getParameter("login_id").substring(2);
		String password = request.getParameter("password").substring(2);
		String name = request.getParameter("name").substring(2);
		String status = request.getParameter("status").substring(2);

		byte[] imageBytes = new byte[0];
		try {
			Part image = request.getPart("image");
			if (image != null) {
				imageBytes = new byte[(int) image.getSize()];
				log("Image Size: " + image.getSize());
				image.getInputStream().read(imageBytes);
			}
		} catch (ServletException e) {
			// VKIT_FIX return appropriate error to client.
		}

		PrintWriter out = response.getWriter();
		try {
			out.println(Repo.createAccount(loginId, password, name, status,
					ByteBuffer.wrap(imageBytes), getServletContext()));
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}

	private void handleLogin(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String loginId = request.getParameter("login_id");
		String password = request.getParameter("password");
		Long clientVersion = Long.valueOf(request
				.getParameter("client_version"));

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		try {
			String loginResult = Repo.login(loginId, password, clientVersion,
					getServletContext());
//			log("Login result: " + loginResult);
			out.println(loginResult);
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}

	private void handleUpdateProfile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		String email = request.getParameter("login_id").substring(2);
		String name = request.getParameter("name").substring(2);
		String status = request.getParameter("status").substring(2);
		String accessToken = request.getParameter("access_token").substring(2);

		// System.out.println(email + ":" + name + ":" + status + ":"
		// + accessToken);

		byte[] imageBytes = null;
		try {
			Part image = request.getPart("image");
			if (image != null) {
				imageBytes = new byte[(int) image.getSize()];
				image.getInputStream().read(imageBytes);
			}
		} catch (ServletException e) {
			// VKIT_FIX return appropriate error to client.
		}

		ByteBuffer photo = null;
		if (imageBytes != null) {
			photo = ByteBuffer.wrap(imageBytes);
		}

		PrintWriter out = response.getWriter();
		try {
			out.println(Repo.updateProfile(accessToken, email, name, status,
					photo, getServletContext()));
		} catch (ServerException e) {
			out.println(e.getMessage());
		}
	}

}
