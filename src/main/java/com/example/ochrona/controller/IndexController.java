package com.example.ochrona.controller;

import com.example.ochrona.model.Post;
import com.example.ochrona.service.AES;
import com.example.ochrona.service.PostService;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping({"/", "/index"})
public class IndexController {
    @Autowired
    private PostService postService;

    @GetMapping
    public String main(Model model) {
        model.addAttribute("post", new Post());
        return "index";
    }

    @PostMapping
    public String save(Post post, Model model, String secret, Authentication authentication) {
        post.setHtml(markdownToHTML(post.getContent()));
        post.setAuthor(authentication.getName());
        if (!post.isPublic()) {
            post.setHtml(AES.encrypt(post.getHtml(), secret));
            post.setContent(AES.encrypt(post.getContent(), secret));
        }
        postService.save(post);
        model.addAttribute("post", post);
        return "saved";
    }

    @GetMapping("/notes/my")
    public String myPosts(Model model, Authentication authentication) {
        System.out.println(authentication.getName());
        List<Post> all = postService.getAll(authentication.getName());
        System.out.println(all);
        model.addAttribute("posts", all);
        return "my_notes";
    }

    @GetMapping("/notes/all")
    public String getAllPublicNotes(Model model){
        List<Post> posts = postService.getAllPublic();
        model.addAttribute("posts", posts);
        return "all_notes";
    }

    @GetMapping("/note/{id}")
    public String showNote(@PathVariable int id, String secret, Model model, Authentication authentication) {
        String name = authentication.getName();
        Post post = postService.get(id);
        if (post == null) {
            throw new RuntimeException("Invalid post id");
        }
        if(!post.isPublic() && secret==null){
            throw new RuntimeException("Secret cannot be null");
        }
        if (!post.isPublic()){
            if(post.getAuthor().equals(name) && secret != null){
                model.addAttribute("title", post.getTitle());
                model.addAttribute("markdown", AES.decrypt(post.getHtml(), secret));
            } else {
                throw new RuntimeException("You aren't privileged to see this post");
            }
        } else {
            model.addAttribute("title", post.getTitle());
            model.addAttribute("markdown", post.getHtml());
        }

        return "note";
    }

    private String markdownToHTML(String markdown) {
        Parser parser = Parser.builder()
                .build();

        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .build();

        return renderer.render(document);
    }
}
