package com.todaytrend.postservice.comment.repository;

import com.todaytrend.postservice.comment.entity.CommentTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentTagRepository extends JpaRepository<CommentTag, Long> {
    void deleteAllByCommentId(Long commentId);
}
