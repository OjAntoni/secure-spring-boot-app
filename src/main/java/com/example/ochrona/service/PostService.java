package com.example.ochrona.service;

import com.example.ochrona.model.Post;
import com.example.ochrona.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public void save(Post post) {
        postRepository.save(post);
    }

    public List<Post> getAll(String username){
        return postRepository.findAllByAuthor(username);
    }

    public List<Post> getAllPublic(){
        return postRepository.findAllPublic();
    }

    public Post get(int id){
        return postRepository.findById(id);
    }
}
