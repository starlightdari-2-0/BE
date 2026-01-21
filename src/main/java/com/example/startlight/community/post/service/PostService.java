package com.example.startlight.community.post.service;

import com.example.startlight.community.funeral.dao.FuneralDao;
import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.entity.Member;
import com.example.startlight.community.post.dao.PostDao;
import com.example.startlight.community.post.dto.PostRequestDto;
import com.example.startlight.community.post.dto.PostDetailedRepDto;
import com.example.startlight.community.post.dto.PostResponseDto;
import com.example.startlight.community.post.dto.PostUpdateReqDto;
import com.example.startlight.community.post.entity.Category;
import com.example.startlight.community.post.entity.Post;
import com.example.startlight.infra.s3.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostDao postDao;
    private final MemberDao memberDao;
    private final FuneralDao funeralDao;
    private final S3Service s3Service;

    public PostDetailedRepDto createPost(PostRequestDto postRequestDto) throws IOException {
        Long userId = UserUtil.getCurrentUserId();
        Member member = memberDao.selectMember(userId);
        Post post = Post.toEntity(postRequestDto, member);
        Post createdPost = postDao.createPost(post);
        if (postRequestDto.getImage() != null) {
            String postImgUrl = s3Service.uploadPostImg(postRequestDto.getImage(), createdPost.getPost_id());
            createdPost.setImg_url(postImgUrl);
        }
        return PostDetailedRepDto.toDto(createdPost, funeralDao);
    }

    public PostDetailedRepDto getPost(Long postId) {
        Post postById = postDao.findPostById(postId);
        return PostDetailedRepDto.toDto(postById, funeralDao);
    }

    public List<PostResponseDto> getAllPosts(String category) {
        List<Post> allPost = postDao.findAllPost();
        return allPost.stream()
                .filter(post -> {
                    if (category == null || category.equalsIgnoreCase("all")) return true;
                    try {
                        return post.getCategory().equals(Category.valueOf(category.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        return false;  // 잘못된 category 값이면 필터링
                    }
                })
                .map(PostResponseDto::toResponseDto)  // Post → PostRequestDto 변환
                .collect(Collectors.toList());
    }

    public PostDetailedRepDto updatePost(PostUpdateReqDto postRequestDto) throws IOException {
        Long userId = UserUtil.getCurrentUserId();
        Post post = postDao.updatePost(userId, postRequestDto);
        if(postRequestDto.getImage() != null) {
            s3Service.deletePostImg(postRequestDto.getPostId());
            s3Service.uploadPostImg(postRequestDto.getImage(), post.getPost_id());
        }
        return PostDetailedRepDto.toDto(post, funeralDao);
    }

    public void deletePost(Long postId) {
        Long userId = UserUtil.getCurrentUserId();
        postDao.deletePost(userId, postId);
        s3Service.deletePostImg(postId);
    }
}
