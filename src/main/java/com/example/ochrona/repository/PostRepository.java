package com.example.ochrona.repository;

import com.example.ochrona.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByAuthor(String author);
    Post findById(int id);

    @Query("select c from Post c where c.isPublic = true ")
    List<Post> findAllPublic();
}
