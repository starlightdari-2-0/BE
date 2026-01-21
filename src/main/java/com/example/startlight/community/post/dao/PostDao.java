package com.example.startlight.community.post.dao;

import com.example.startlight.global.exception.UnauthorizedAccessException;
import com.example.startlight.community.post.dto.PostUpdateReqDto;
import com.example.startlight.community.post.entity.Post;
import com.example.startlight.community.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostDao {
    private final PostRepository postRepository;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post findPostById(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isPresent()) {
            return postOptional.get();
        }
        throw new NoSuchElementException("Post not found with id: " + id);
    }
    public List<Post> findAllPost() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Post updatePost(Long userId, PostUpdateReqDto postRequestDto) {
        Post postById = findPostById(postRequestDto.getPostId());
        if (postById.getMember().getMember_id().equals(userId)) {
            postById.updatePost(postRequestDto);
            return postById;
        }
        else {
            throw new UnauthorizedAccessException("자신이 작성한 글만 수정할 수 있습니다.");
        }

    }

    public void deletePost(Long userId, Long postId) {
        Post postById = findPostById(postId);
        if (postById.getMember().getMember_id().equals(userId)) {
            postRepository.deleteById(postId);
        }
        else {
            throw new UnauthorizedAccessException("자신이 작성한 글만 삭제할 수 있습니다.");
        }
    }
}
