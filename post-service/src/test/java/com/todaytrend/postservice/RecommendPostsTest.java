package com.todaytrend.postservice;

import com.todaytrend.postservice.entity.*;
import com.todaytrend.postservice.enumulator.CategoryNames;
import com.todaytrend.postservice.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
public class RecommendPostsTest {


    @Autowired
    private EntityManager em;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    public void init(){
        Post1();
        Post2();
        Post3();
        Post4();
    }
        public void Post1(){
            String user1 = "user1";
            String user2 = "user2";
            String user3 = "user3";
            String user4 = "user4";

            String hash1 = "tag1";
            String hash2 = "tag2";
            String hash3 = "tag3";

            Post post = new Post("user1 content",user1);
            em.persist(post);
            Category category1 = new Category(CategoryNames.CATEGORY1.name(), post.getPostId());
            Category category2 = new Category(CategoryNames.CATEGORY2.name(), post.getPostId());
            em.persist(category1);
            em.persist(category2);

            PostLike postLike1 = new PostLike(user1,post.getPostId());//본인이 본인꺼 좋아요 누름
            PostLike postLike2 = new PostLike(user2, post.getPostId());
            em.persist(postLike1);
            em.persist(postLike2);

            PostUserTag postUserTag1 = new PostUserTag(user2, post.getPostId());
            em.persist(postUserTag1);

            HashTag hashTag1 = new HashTag(hash1, post.getPostId());
            HashTag hashTag2 = new HashTag(hash3, post.getPostId());
            em.persist(hashTag1);
            em.persist(hashTag2);

        }

        public void Post2(){
            String user1 = "user1";
            String user2 = "user2";
            String user3 = "user3";
            String user4 = "user4";

            String hash1 = "tag1";
            String hash2 = "tag2";
            String hash3 = "tag3";

            Post post = new Post("user2 content",user2);
            em.persist(post);
            Category category1 = new Category(CategoryNames.CATEGORY1.name(), post.getPostId());
            Category category2 = new Category(CategoryNames.CATEGORY2.name(), post.getPostId());
            em.persist(category1);
            em.persist(category2);

            PostLike postLike1 = new PostLike(user1,post.getPostId());
            PostLike postLike2 = new PostLike(user3, post.getPostId());
            PostLike postLike3 = new PostLike(user4, post.getPostId());
            em.persist(postLike1);
            em.persist(postLike2);
            em.persist(postLike3);

            PostUserTag postUserTag1 = new PostUserTag(user4, post.getPostId());
            em.persist(postUserTag1);

            HashTag hashTag1 = new HashTag(hash1, post.getPostId());
//            HashTag hashTag2 = new HashTag(hash3, post.getPostId());
            em.persist(hashTag1);
//            em.persist(hashTag2);
        }

        public void Post3(){
            String user1 = "user1";
            String user2 = "user2";
            String user3 = "user3";
            String user4 = "user4";

            String hash1 = "tag1";
            String hash2 = "tag2";
            String hash3 = "tag3";

            Post post = new Post("user3 content",user3);
            em.persist(post);
            Category category1 = new Category(CategoryNames.CATEGORY1.name(), post.getPostId());
            Category category2 = new Category(CategoryNames.CATEGORY2.name(), post.getPostId());
            Category category3 = new Category(CategoryNames.CATEGORY3.name(), post.getPostId());
            em.persist(category1);
            em.persist(category2);
            em.persist(category3);

            PostLike postLike1 = new PostLike(user1, post.getPostId());
            PostLike postLike2 = new PostLike(user2, post.getPostId());
            em.persist(postLike1);
            em.persist(postLike2);

            PostUserTag postUserTag1 = new PostUserTag(user2, post.getPostId());
            em.persist(postUserTag1);

            HashTag hashTag1 = new HashTag(hash1, post.getPostId());
            HashTag hashTag2 = new HashTag(hash3, post.getPostId());
            HashTag hashTag3 = new HashTag(hash2, post.getPostId());
            em.persist(hashTag1);
            em.persist(hashTag2);
            em.persist(hashTag3);
        }


        public void Post4(){
            String user1 = "user1";
            String user2 = "user2";
            String user3 = "user3";
            String user4 = "user4";

            String hash1 = "tag1";
            String hash2 = "tag2";
            String hash3 = "tag3";

            Post post = new Post("user4 content",user4);
            em.persist(post);
            Category category1 = new Category(CategoryNames.CATEGORY1.name(), post.getPostId());
            Category category2 = new Category(CategoryNames.CATEGORY2.name(), post.getPostId());
            Category category3 = new Category(CategoryNames.CATEGORY3.name(), post.getPostId());
            em.persist(category1);
            em.persist(category2);
            em.persist(category3);

            PostLike postLike1 = new PostLike(user1, post.getPostId());
            PostLike postLike2 = new PostLike(user2, post.getPostId());
            em.persist(postLike1);
            em.persist(postLike2);

            PostUserTag postUserTag1 = new PostUserTag(user1, post.getPostId());
            em.persist(postUserTag1);

            HashTag hashTag1 = new HashTag(hash1, post.getPostId());
            HashTag hashTag2 = new HashTag(hash3, post.getPostId());
            HashTag hashTag3 = new HashTag(hash2, post.getPostId());
            em.persist(hashTag1);
            em.persist(hashTag2);
            em.persist(hashTag3);
        }


    @Test
    void findPostIdBy() {


        // 테스트용 데이터
        String userUuid = "user1";
        List<String> followings = Arrays.asList("user2");
        Integer tab = 2;
        List<String> categoryList = Arrays.asList(CategoryNames.CATEGORY1.name()/*,
                CategoryNames.CATEGORY2.name(),
                CategoryNames.CATEGORY3.name()*/);

        // 테스트 메서드 호출
        List<Long> result = postRepository.findPostIdBy(/*userUuid, followings, tab, */categoryList);

        System.out.println("Result: " + result);

        // 결과 검증
        //assertThat(result).containsExactly(post1.getPostId());
    }

}
