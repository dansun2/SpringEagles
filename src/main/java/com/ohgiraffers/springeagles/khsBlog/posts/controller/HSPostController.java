package com.ohgiraffers.springeagles.khsBlog.posts.controller;

import com.ohgiraffers.springeagles.khsBlog.posts.dto.HSPostsDTO;
import com.ohgiraffers.springeagles.khsBlog.posts.repository.HSPostsEntity;
import com.ohgiraffers.springeagles.khsBlog.posts.repository.HSPostsRepository;
import com.ohgiraffers.springeagles.khsBlog.posts.service.HSPostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/khs/blog")
public class HSPostController {

    private HSPostsService hsPostsService; // HSPostsService 클래스의 인스턴스를 주입받기 위한 필드
    private HSPostsRepository hsPostsRepository; // HSPostsRepository 클래스의 인스턴스를 주입받기 위한 필드

    @Autowired
    public HSPostController(HSPostsRepository hsPostsRepository, HSPostsService hsPostsService) {
        this.hsPostsRepository = hsPostsRepository; // 생성자를 통해 HSPostsRepository 인스턴스를 주입받음
        this.hsPostsService = hsPostsService; // 생성자를 통해 HSPostsService 인스턴스를 주입받음
    }

    /**
     * 수정 페이지를 보여주는 메서드
     * @param model 모델 객체
     * @return 수정 페이지 뷰 이름
     */
    @GetMapping("/editpagehs")
    public String showEditPage(Model model) {
        List<HSPostsEntity> postList = hsPostsService.postsEntityList(); // 모든 게시글 목록을 가져옴
        Collections.reverse(postList); // 게시글 목록을 역순으로 정렬
        model.addAttribute("currentPage", "editpagehs"); // 현재 페이지 정보를 모델에 추가
        model.addAttribute("postList", postList); // 게시글 목록을 모델에 추가
        return "khs_Blog/blogPost4"; // 뷰 이름 반환
    }

    /**
     * 게시글 목록 페이지를 보여주는 메서드
     * @param keyword 검색어 (옵션)
     * @param model 모델 객체
     * @param mv 모델앤뷰 객체
     * @return 모델앤뷰 객체 반환
     */
    @GetMapping
    public ModelAndView showlistpage(@RequestParam(value = "keyword", required = false) String keyword, Model model, ModelAndView mv) {
        List<HSPostsEntity> postList = hsPostsService.postsEntityList(); // 모든 게시글 목록을 가져옴
        List<HSPostsEntity> searchResults;


        // 검색어가 있는 경우 게시글 목록에서 검색어를 포함하는 게시글을 필터링
        if (keyword != null && !keyword.isEmpty()) { // 검색어가 null이 아니고 빈 문자열이 아닌 경우에만 실행
            searchResults = postList.stream() // 게시글 목록(postList)을 스트림으로 변환
                    .filter(post -> post.getTitle().contains(keyword) // 게시글 제목에 검색어가 포함되어 있는지 검사
                            || post.getDescription().contains(keyword) // 또는 게시글 설명에 검색어가 포함되어 있는지 검사
                            || post.getContent().contains(keyword)) // 또는 게시글 내용에 검색어가 포함되어 있는지 검사
                    .collect(Collectors.toList()); // 필터링된 게시글들을 리스트로 수집
            model.addAttribute("searchResults", searchResults); // 검색 결과를 모델에 추가
        } else {
            searchResults = Collections.emptyList(); // 빈 리스트로 설정
        }

        Collections.reverse(postList); // 게시글 목록을 역순으로 정렬
        model.addAttribute("currentPage", "main"); // 현재 페이지 정보를 모델에 추가
        mv.addObject("postList", postList); // 게시글 목록을 모델앤뷰에 추가
        mv.setViewName("khs_Blog/blogPost4"); // 뷰 이름 설정
        return mv; // 모델앤뷰 객체 반환
    }

    /**
     * 새로운 게시글을 추가하는 메서드
     * @param hsPostsDTO 게시글 DTO
     * @return 리다이렉트 경로
     */
    @PostMapping("/post")
    public String addPost(HSPostsDTO hsPostsDTO) {
        HSPostsEntity hsPostsEntity = hsPostsDTO.toEntity(); // DTO를 엔티티로 변환
        HSPostsEntity saved = hsPostsRepository.save(hsPostsEntity); // 엔티티를 저장
        System.out.println(saved.toString()); // 저장된 엔티티 정보를 콘솔에 출력
        return "redirect:/khs/blog"; // 블로그 메인 페이지로 리다이렉트
    }

    /**
     * 특정 게시글을 읽는 페이지를 보여주는 메서드
     * @param post_id 게시글 ID
     * @param mv 모델앤뷰 객체
     * @return 모델앤뷰 객체 반환
     */
    @GetMapping("/postreader/{post_id}")
    public ModelAndView showReadPage(@PathVariable("post_id") Integer post_id, ModelAndView mv) {
        HSPostsEntity post = hsPostsService.getPostById(post_id).orElse(null); // ID로 게시글을 찾음
        List<HSPostsEntity> postList = hsPostsService.postsEntityList(); // 모든 게시글 목록을 가져옴
        Collections.reverse(postList); // 게시글 목록을 역순으로 정렬
        mv.addObject("postList", postList); // 게시글 목록을 모델앤뷰에 추가
        mv.addObject("post", post); // 선택된 게시글을 모델앤뷰에 추가
        mv.addObject("selectedId", post_id); // 선택된 게시글 ID를 모델앤뷰에 추가
        mv.addObject("currentPage", "postreader"); // 현재 페이지 정보를 모델앤뷰에 추가
        mv.setViewName("khs_Blog/blogPost4"); // 뷰 이름 설정
        return mv; // 모델앤뷰 객체 반환
    }

    /**
     * 게시글을 삭제하는 메서드
     * @param id 게시글 ID
     * @return 리다이렉트 경로
     */
    @GetMapping("/postreader/delete")
    public String deletePost(@RequestParam("id") Integer id) {
        hsPostsService.deletePost(id); // ID로 게시글을 삭제
        return "redirect:/khs/blog"; // 블로그 메인 페이지로 리다이렉트
    }

    /**
     * 게시글 수정 페이지를 보여주는 메서드
     * @param id 게시글 ID
     * @param mv 모델앤뷰 객체
     * @return 모델앤뷰 객체 반환
     */
    @GetMapping("/postreader/modify{id}")
    public ModelAndView modifyPage(@PathVariable("id") Integer id, ModelAndView mv) {
        HSPostsEntity post = hsPostsService.getPostById(id).orElse(null); // ID로 게시글을 찾음
        mv.addObject("post", post); // 선택된 게시글을 모델앤뷰에 추가
        mv.addObject("currentPage", "modifypage"); // 현재 페이지 정보를 모델앤뷰에 추가
        mv.setViewName("khs_Blog/blogPost4"); // 뷰 이름 설정
        return mv; // 모델앤뷰 객체 반환
    }

    /**
     * 게시글을 수정하는 메서드
     * @param id 게시글 ID
     * @param hsPostsDTO 게시글 DTO
     * @return 리다이렉트 경로
     */
    @PostMapping("/postreader/modify{id}")
    public String modifyPost(@PathVariable("id") Integer id, HSPostsDTO hsPostsDTO) {
        HSPostsEntity updatedEntity = hsPostsDTO.toEntity(); // DTO를 엔티티로 변환
        hsPostsService.modifypost(id, updatedEntity); // ID로 게시글을 수정
        return "redirect:/khs/blog/postreader/" + id; // 수정된 게시글 페이지로 리다이렉트
    }
}