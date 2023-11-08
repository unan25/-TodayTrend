package com.todaytrend.postservice.service;

import com.todaytrend.postservice.dto.RequestDeleteReadPostDto;
import com.todaytrend.postservice.dto.RequestPostDto;
import com.todaytrend.postservice.dto.ResponsePostDto;
import com.todaytrend.postservice.entity.Category;
import com.todaytrend.postservice.entity.HashTag;
import com.todaytrend.postservice.entity.Post;
import com.todaytrend.postservice.entity.PostUserTag;
import com.todaytrend.postservice.enumulator.CategoryNames;
import com.todaytrend.postservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
//@Transactional(readOnly = true)
// repo통해서만 db변경가능하게 읽기 전용으로 둠,
// 변경 감지를 위한 스냅샷 인스턴스 보관이 필요 없으므로 메모리 사용량 최적화 가능
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepo;
    private final CategoryRepository categoryRepo;
    private final PostLikeRepository postLikeRepo;
    private final PostUserTagRepository postUserTagRepo;
    private final HashTagRepository hashTagRepo;


//--------------------------- 포스트 생성 --------------------------------
    @Override
    public String makePost(RequestPostDto requestPostDto) {

        String u_uuid = requestPostDto.getUserUuid();
        String content = requestPostDto.getContent();
        List<String> categoryList = requestPostDto.getCategoryList();

        Post post = Post.builder()
                        .userUuid(u_uuid)
                        .content(content)
                        .build();

        Post resultPost = postRepo.save(post);//save는 저장한 객체를 그대로 반환
        Integer postId = resultPost.getPostId();

        checkUserTagAndHashTag(content,postId);
        checkCategory(categoryList,postId);

        return "postServiceImpl : postInsert is finish -----------";
    }

    //category중 어느 카테고리인지 확인
    private void checkCategory(List<String> categoryList, Integer postId){
        for (String categoryName : categoryList){
            switch (categoryName){
                case "CATEGORY1" -> {
                    makeCategory(CategoryNames.CATEGORY1.toString(),postId);
                }
                case "CATEGORY2" ->{
                    makeCategory(CategoryNames.CATEGORY2.toString(),postId);
                }
                case "CATEGORY3" ->{
                    makeCategory(CategoryNames.CATEGORY3.toString(),postId);
                }
            }
        }
    }

    //category insert
    private void makeCategory(String category, Integer postId){
        categoryRepo.save(Category.builder()
                .categoryName(category)//todo : catecory 이름은 임의로 enum으로 일단 선언해둠 나중에 변경 필요
                .postId(postId)
                .build()
        );
    }

    //Content에서 #과 @분리 및 save(PostUserTag, HashTag)
    private void checkUserTagAndHashTag(String content,Integer postId){
        String postContent = content;
        // hashtag - 내용 - usertag 순으로 작성된다고 가정, todo: 나중에 변경 할 것
        String[] checkHashTag = Stream.of(postContent.split("#")).map(String::trim).toArray(String[]::new);
        String[] checkUserTag = Stream.of(postContent.split("@")).map(String::trim).toArray(String[]::new);

        if (checkHashTag.length > 1){
            for (int i = 1 ; i< checkHashTag.length; i++){
                String[] hashTag = checkHashTag[i].split(" ");
                if(hashTag.length == 1){
                    makeHashTag(checkHashTag[i],postId);
                }else{
                    makeHashTag(hashTag[0],postId);
                    break;
                }
            }
        }

        if (checkUserTag.length>1){
            for (int i =1; i< checkUserTag.length; i++){
                makePostUserTag(checkUserTag[i],postId);
            }
        }
    }

    //PostUserTag insert
    private void makePostUserTag(String checkUserTag, Integer postId){
        postUserTagRepo.save( PostUserTag.builder()
                .userUuid(findUserUuidByNickname(checkUserTag))
                .postId(postId)
                .build()
        );
    }

    //hashTag insert
    private void makeHashTag(String hashTag, Integer postId){
        hashTagRepo.save( HashTag.builder()
                .hashtag(hashTag)
                .postId(postId)
                .build()
        );
    }


    //@nickname으로 멘션시 nickname을 이용해서 userUuid값 가져오기
    private String findUserUuidByNickname(String nickname){
        //todo: User서버로 넘어가서 uuid가져오는 로직
        return null;
    }

//---------------------------------------------------------------------------

//--------------------------포스트 삭제----------------------------------------
    @Override
    public String removePost(RequestDeleteReadPostDto requestDeletePostDto) {
        Integer postId = requestDeletePostDto.getPostId();
        String userUuid = requestDeletePostDto.getUserUuid();

        if(userUuid.equals(postRepo.findById(postId).get().getUserUuid())){//postid로 해당 post작성자의 uuid를 받아와서 본인의 게시물이 맞는지 판별
            hashTagRepo.deleteAllByPostId(postId);
            postUserTagRepo.deleteAllByPostId(postId);
            postLikeRepo.deleteAllByPostId(postId);
            categoryRepo.deleteAllByPostId(postId);
            postRepo.deleteAllByPostId(postId);
            return "postServiceImpl : post delete is finish -----------";
        }
        return "postServiceImpl : post delete has error -----------";
    }

//----------------------------------------------------------------------------

//----------------------------포스트 불러오기------------------------------------

    @Override
    public ResponsePostDto findPost(RequestDeleteReadPostDto requestReadPostDto) {
        //todo : post 상세보기에서 해당 포스트가 본인의 것인지 판별하는 로직은 어디서 할지 정해야함(화면에서 수정하기 버튼 있어야하니까!)
        Integer postId = requestReadPostDto.getPostId();
        String userUuid = requestReadPostDto.getUserUuid();

        boolean myPost = userUuid.equals(postRepo.findById(postId).get().getUserUuid()) ? true : false ;// T- 본인 포스트

        Post post = postRepo.findById(postId).get();
        List<String> categoryList = new ArrayList<>();
        categoryRepo.findAllByPostId(postId)
                .forEach(category -> categoryList.add(category.getCategoryName()));
        boolean checkClickLike = postLikeRepo.findByUserUuid(userUuid) != null ? true : false; //T-좋아요 누른 사람


        return ResponsePostDto.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .updatedAt(post.getUpdatedAt())
                .categoryList(categoryList)
                .likeCnt(postLikeRepo.findAllByPostId(postId).size())
                .like(checkClickLike)
                .build();
    }
}