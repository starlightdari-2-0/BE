package com.example.startlight.community.post.controller;

import com.example.startlight.community.post.dto.*;
import com.example.startlight.community.post.entity.Category;
import com.example.startlight.community.post.service.PostService;
import com.example.startlight.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping()
    public ResponseEntity<PostResponseDto> create(@ModelAttribute PostRequestDto postRequestDto) throws IOException {
        PostResponseDto post = postService.createPost(postRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailedRepDto> get(@PathVariable Long id) throws IOException {
        PostDetailedRepDto post = postService.getPost(id);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @GetMapping()
    public ResponseEntity<List<PostResponseDto>> getAll(@RequestParam(required = false) String category){
        List<PostResponseDto> posts = postService.getAllPosts(category);
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

//    @PatchMapping()
//    public ResponseEntity<PostDetailedRepDto> update(@ModelAttribute PostUpdateReqDto postRequestDto) throws IOException {
//        PostDetailedRepDto postDetailedRepDto = postService.updatePost(postRequestDto);
//        return ResponseEntity.status(HttpStatus.OK).body(postDetailedRepDto);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted post id : " + id);
    }

    @GetMapping("/all")
    public ResponseEntity<PageResponse<PostRepDto>> getPublicStars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Category category
    ) {
        PageResponse<PostRepDto> posts = postService.getPosts(page, size, category);
        return ResponseEntity.ok(posts);
    }

}
