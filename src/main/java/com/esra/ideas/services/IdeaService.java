package com.esra.ideas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esra.ideas.models.Idea;
import com.esra.ideas.models.User;
import com.esra.ideas.repositories.IdeaRepository;

@Service
public class IdeaService {

	@Autowired
	private IdeaRepository ideaRepository;

	// Create
	public Idea create(Idea idea) {
		return this.ideaRepository.save(idea);
	}

	// find all

	public List<Idea> allIdeas() {
		return this.ideaRepository.findAll();
	}

	// Get one
	public Idea getOneIdea(Long id) {
		return this.ideaRepository.findById(id).orElse(null);
	}

	// Update
	public Idea updateIdea(Long id, String description) {
		Idea idea = getOneIdea(id);
		idea.setDescription(description);
		return ideaRepository.save(idea);
	}

	// Delete
	public void deleteIdea(Long id) {
		this.ideaRepository.deleteById(id);
	}

	// Like
	public void likeIdea(User user, Idea idea) {
		List<User> usersWhoLiked = idea.getLikers();
		usersWhoLiked.add(user);
		this.ideaRepository.save(idea);
	}

	// Unlike
	public void unlikeIdea(User user, Idea idea) {
		List<User> usersWhoLiked = idea.getLikers();
		usersWhoLiked.remove(user);
		this.ideaRepository.save(idea);
	}

	public List<Idea> getLikesSortedDesc() {
		return this.ideaRepository.findAllByLikersOrderByLikersDesc();
	}

	public List<Idea> getLikesSortedAsc() {
		return this.ideaRepository.findAllByLikersOrderByLikersAsc();
	}
}
