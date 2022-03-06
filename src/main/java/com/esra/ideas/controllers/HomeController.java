package com.esra.ideas.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.esra.ideas.models.Idea;
import com.esra.ideas.models.User;
import com.esra.ideas.services.IdeaService;
import com.esra.ideas.services.UserService;
import com.esra.ideas.validators.IdeaValidator;

/**
 * Idea controller is for idea related CRUD operations.
 * @author esrayazar
 *
 */
@Controller
@RequestMapping("/ideas")
public class HomeController {
	@Autowired
	private UserService userService;
	@Autowired
	private IdeaService ideaService;
	@Autowired
	private IdeaValidator ideaValidator;

	/**
	 * Displays Idea dashboard
	 * @param session
	 * @param viewModel
	 * @param idea
	 * @return
	 */
	@GetMapping("/dashboard")
	public String dashboard(HttpSession session, Model viewModel, @ModelAttribute("idea") Idea idea) {
		// Check if there is any active user session.
		if(session.getAttribute("user__id") == null) return "redirect:/";
		User user = this.userService.findOneUser((Long) session.getAttribute("user__id"));
		viewModel.addAttribute("user", user);
		viewModel.addAttribute("ideas", this.ideaService.allIdeas());
		return "dashboard.jsp";
	}

	/**
	 * Sort dashboard based on high like counts
	 * @param session
	 * @param viewModel
	 * @param idea
	 * @return
	 */
	@GetMapping("/sortbyDesc")
	public String dashboardSortedbyDesc(HttpSession session, Model viewModel, @ModelAttribute("idea") Idea idea) {
		// Check if there is any active user session.
		if(session.getAttribute("user__id") == null) return "redirect:/";
		User user = this.userService.findOneUser((Long) session.getAttribute("user__id"));
		viewModel.addAttribute("user", user);
		viewModel.addAttribute("ideas", this.ideaService.getLikesSortedDesc());
		return "dashboard.jsp";
	}

	/**
	 * Sort dashboard based on low like counts
	 * @param session
	 * @param viewModel
	 * @param idea
	 * @return
	 */
	@GetMapping("/sortbyAsc")
	public String dashboardSortedbyAsc(HttpSession session, Model viewModel, @ModelAttribute("idea") Idea idea) {
		// Check if there is any active user session.
		if(session.getAttribute("user__id") == null) return "redirect:/";
		User user = this.userService.findOneUser((Long) session.getAttribute("user__id"));
		viewModel.addAttribute("user", user);
		viewModel.addAttribute("ideas", this.ideaService.getLikesSortedAsc());
		return "dashboard.jsp";
	}

	/**
	 * Only logged in users can create new ideas
	 * @param session
	 * @param viewModel
	 * @param idea
	 * @return
	 */
	@GetMapping("/new")
	public String createIdea(HttpSession session, Model viewModel, @ModelAttribute("idea") Idea idea) {
		// Check if there is any active user session.
		if(session.getAttribute("user__id") == null) return "redirect:/";
		User user = this.userService.findOneUser((Long) session.getAttribute("user__id"));
		viewModel.addAttribute("user", user);
		viewModel.addAttribute("allideas", this.ideaService.allIdeas());
		return "new.jsp";
	}

	@PostMapping("/dashboard/create")
	public String addIdea(@Valid @ModelAttribute("idea") Idea idea, BindingResult result, HttpSession session,
			Model viewModel) {
		User user = this.userService.findOneUser((Long) session.getAttribute("user__id"));
		ideaValidator.validate(idea, result);
		if (result.hasErrors()) {
			viewModel.addAttribute("user", user);
			viewModel.addAttribute("allIdeas", this.ideaService.allIdeas());
			return "new.jsp";
		}
		idea.setUser(user);
		this.ideaService.create(idea);
		return "redirect:/ideas/dashboard";
	}

	/**
	 * Get one idea details
	 * Check if the id - idea found in database.
	 * Controller should not generate white label error!
	 * 
	 * @param ideaId
	 * @param model
	 * @param session
	 * @return
	 */
	@GetMapping("/idea/{id}")
	public String idea(@PathVariable("id") Long ideaId, Model model, HttpSession session) {
		// Check if there is any active user session.
		if(session.getAttribute("user__id") == null) return "redirect:/";
		Idea idea = ideaService.getOneIdea(ideaId);
		User user = this.userService.findOneUser((Long) session.getAttribute("user__id"));
		if (idea == null) {
			System.out.println("Idea id=" + ideaId + " is not found in DB.");
			model.addAttribute("warning", "Given Idea can not be found in database!");
		}

		model.addAttribute("user", user);
		model.addAttribute("idea", idea);
		return "details.jsp";

	}

	/**
	 * Edit an Idea
	 * Check if the id - idea found in database.
	 * Controller should not generate white label error!
	 * @param id
	 * @param idea
	 * @param model
	 * @param session
	 * @return
	 */
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Long id, @ModelAttribute("editIdea") Idea idea, Model model,
			HttpSession session) {
		// Check if there is any active user session.
		if(session.getAttribute("user__id") == null) return "redirect:/";
		Idea editIdea = ideaService.getOneIdea(id);
		// Check if the given id returns anything from DB
		// This will prevent white label error on UI
		if (editIdea == null) {
			System.out.println("Idea id=" + id + " is not found in DB.");
			return "redirect:/ideas/dashboard";
		}
		// Verify User has access to Edit the Idea
		if (editIdea.getUser().getId().compareTo((Long) session.getAttribute("user__id")) != 0)
			return "redirect:/ideas/dashboard";
		model.addAttribute("editIdea", editIdea);
		return "edit.jsp";
	}

	/**
	 * Update an Idea
	 * @param id
	 * @param idea
	 * @param result
	 * @param session
	 * @param model
	 * @return
	 */
	@PostMapping("/update/{id}")
	public String update(@PathVariable("id") Long id, @Valid @ModelAttribute("editIdea") Idea idea,
			BindingResult result, HttpSession session, Model model) {
		ideaValidator.validate(idea, result);
		if (result.hasErrors()) {
			return "edit.jsp";
		}
		ideaService.updateIdea(id, idea.getDescription());
		return "redirect:/ideas/dashboard";
	}

	/**
	 * Handle like operation in DB
	 * @param session
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}/like")
	public String like(HttpSession session, @PathVariable("id") Long id) {
		// Check if there is any active user session.
		if(session.getAttribute("user__id") == null) return "redirect:/";
		User user = this.userService.findOneUser((Long) session.getAttribute("user__id"));
		Idea idea = this.ideaService.getOneIdea(id);
		this.ideaService.likeIdea(user, idea);
		return "redirect:/ideas/dashboard";
	}

	/**
	 * Handle unlike operation in DB
	 * @param session
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}/unlike")
	public String unlike(HttpSession session, @PathVariable("id") Long id) {
		// Check if there is any active user session.
		if(session.getAttribute("user__id") == null) return "redirect:/";
		User user = this.userService.findOneUser((Long) session.getAttribute("user__id"));
		Idea idea = this.ideaService.getOneIdea(id);
		this.ideaService.unlikeIdea(user, idea);
		return "redirect:/ideas/dashboard";
	}

	/**
	 * Delete an idea if user has access!
	 * @param id
	 * @param session
	 * @return
	 */
	@GetMapping("/{id}/delete")
	public String delete(@PathVariable("id") Long id, HttpSession session) {
		// Check if there is any active user session.
		if(session.getAttribute("user__id") == null) return "redirect:/";
		Idea idea = ideaService.getOneIdea(id);
		if (idea.getUser().getId().compareTo((Long) session.getAttribute("user__id")) != 0) {
			System.out.println("Warning: Access denied! User does not own this idea.");
			return "redirect:/ideas/dashboard";
		}

		this.ideaService.deleteIdea(id);
		return "redirect:/ideas/dashboard";
	}

}
