package com.esra.ideas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.esra.ideas.models.Idea;

@Repository
public interface IdeaRepository extends CrudRepository<Idea, Long> {
	List<Idea> findAll();

	@Query("SELECT i FROM Idea i Order By i.likers.size DESC")
	List<Idea> findAllByLikersOrderByLikersDesc();

	@Query("SELECT i FROM Idea i Order By i.likers.size ASC")
	List<Idea> findAllByLikersOrderByLikersAsc();

}
