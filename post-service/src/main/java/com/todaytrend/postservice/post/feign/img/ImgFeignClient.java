package com.todaytrend.postservice.post.feign.img;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "image-service")
public interface ImgFeignClient {
    @PostMapping("api/images/postList")
    ImgFeignDto getImagesByPostIdList(@RequestBody RequestImageListDto dto);

    @GetMapping("api/images/{postId}")
    ImgFeignDto getImageByPostId(@PathVariable Long postId);
}