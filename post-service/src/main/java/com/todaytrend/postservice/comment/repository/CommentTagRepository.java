package com.todaytrend.postservice.comment.repository;

import com.todaytrend.postservice.comment.entity.CommentTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentTagRepository extends JpaRepository<CommentTag, Long> {

//    @Query("SELECT ct.nickname FROM CommentTag ct WHERE ct.commentId = :commentId")
//    List<String> findByCommentId(Long commentId);

}
