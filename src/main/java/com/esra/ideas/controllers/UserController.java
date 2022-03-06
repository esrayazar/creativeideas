package com.esra.ideas.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.esra.ideas.models.User;
import com.esra.ideas.services.UserService;
import com.esra.ideas.validators.UserValidator;

/**
 * This controller handles CRUD operations for User Login and Registration
 * @author esrayazar
 *
 */
@Controller
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserValidator validator;

	@GetMapping("/")
	public String login(@ModelAttribute("user") User user) {
		return "index.jsp";
	}

	/**
	 * User registration
	 * @param user
	 * @param result
	 * @param session
	 * @return
	 */
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
		System.out.println("User:" + user.toString());
		validator.validate(user, result);
		if (result.hasErrors()) {
			return "index.jsp";
		}
		User newUser = this.userService.registerUser(user);
		session.setAttribute("user__id", newUser.getId());
		return "redirect:/ideas/dashboard";
	}

	/**
	 * Handle User login
	 * @param session
	 * @param email
	 * @param password
	 * @param redirectAttr
	 * @return
	 */
	@PostMapping("/login")
	public String login(HttpSession session, @RequestParam("loginEmail") String email,
			@RequestParam("loginPassword") String password, RedirectAttributes redirectAttr) {
		if (!this.userService.authenticateUser(email, password)) {
			redirectAttr.addFlashAttribute("loginError", "Invalid Credentials");
			return "redirect:/";
		}
		User userToBeLoggedIn = this.userService.getUserByEmail(email);
		session.setAttribute("user__id", userToBeLoggedIn.getId());
		return "redirect:/ideas/dashboard";
	}

	/**
	 * Handle User logout.
	 * @param session
	 * @return
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

}



















