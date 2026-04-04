package com.example.startlight.community.post.postComment.repository;

import com.example.startlight.community.post.postComment.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    @Query("select count(p) from Post p where p.post_id = :postId")
    Integer countByPostId(@Param("postId") Long postId);

    /**
     * 부모 댓글 페이징 조회 (parent가 null인 댓글만)
     */
    @Query("""
        select pc
        from PostComment pc
        where pc.post.post_id = :postId
          and pc.parent is null
        order by pc.createdAt desc
    """)
    Page<PostComment> findParentCommentByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("""
    select pc
    from PostComment pc
    where pc.post.post_id = :postId
    order by pc.comment_id desc
""")
    List<PostComment> findAllByPostIdDesc(@Param("postId") Long postId);

    /**
     * 특정 부모 댓글의 자식 댓글 조회
     */
    @Query("""
        select pc
        from PostComment pc
        where pc.parent.comment_id = :parentId
        order by pc.createdAt asc
    """)
    List<PostComment> findChildrenCommentByCommentId(@Param("parentId") Long parentId);

    /**
     * 특정 부모 댓글의 자식 댓글 개수
     */
    @Query("""
        select count(pc)
        from PostComment pc
        where pc.parent.comment_id = :parentId
    """)
    Long countChildrenComment(@Param("parentId") Long parentId);
}
