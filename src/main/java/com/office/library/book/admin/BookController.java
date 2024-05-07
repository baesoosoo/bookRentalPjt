package com.office.library.book.admin;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.office.library.admin.member.AdminMemberVo;
import com.office.library.book.BookVo;
import com.office.library.book.admin.util.UploadFileService;

@Controller
@RequestMapping("/book/admin")
public class BookController {
	
	@Autowired
	BookService bookService;
	
	@Autowired
	UploadFileService uploadFileService;
	
	// 도서 등록 화면으로 이동하기 위한 메서드
	@GetMapping("registerBookForm")
	public String registerBookForm() {
		
		
		String nextPage = "admin/book/register_book_form";
		
		return nextPage;
	}
	
	//도서 등록 확인 메서드
	@PostMapping("/registerBookConfirm")
	public String registerBookConfirm(BookVo bookVo, 
									  @RequestParam("file") MultipartFile file) {
		System.out.println("[BookController] registerBookConfirm()");
		
		String nextPage = "admin/book/register_book_ok";
		
		// 파일 경로 설정 및 성공 여부
		//파일 이름이 넘어옴
		String savedFileName = uploadFileService.upload(file);
		
		if (savedFileName != null) {
			bookVo.setB_thumbnail(savedFileName);//이미지에 넣어주기

			int result = bookService.registerBookConfirm(bookVo); //도서등록 결과
			
			if (result <= 0)
				nextPage = "admin/book/register_book_ng";
			
		} else {
			nextPage = "admin/book/register_book_ng";
			
		}
		
		return nextPage;
		
	}
	
	//도서 검색
	@GetMapping("/searchBookConfirm")
	public String searchBookConfirm(BookVo bookVo, Model model) {
		
		System.out.println("[UserBookController] searchBookConfirm()");
		
		String nextPage = "admin/book/search_book";
		
		List<BookVo> bookVos = bookService.searchBookConfirm(bookVo);
		
		model.addAttribute("bookVos", bookVos);
		
		return nextPage;
		
	}
	
	@GetMapping("/bookDetail")
	public String bookDetail(@RequestParam("b_no") int b_no, Model model) {
		System.out.println("[BookController] bookDetail()");
		
		String nextPage = "admin/book/book_detail";
		
		BookVo bookVo = bookService.bookDetail(b_no);
		
		model.addAttribute("bookVo", bookVo);
		
		return nextPage;
		
	}
	
	//수정 내용을 가져옴
	@GetMapping("/modifyBookForm")
	public String modifyBookForm(@RequestParam("b_no") int b_no, 
								 Model model, 
								 HttpSession session) {
		System.out.println("[BookController] bookDetail()");
		
		String nextPage = "admin/book/modify_book_form";
		
		AdminMemberVo loginedAdminMemberVo = (AdminMemberVo) session.getAttribute("loginedAdminMemberVo");
		if (loginedAdminMemberVo == null)
			return "redirect:/admin/member//loginForm";
		
		BookVo bookVo = bookService.modifyBookForm(b_no);
		
		model.addAttribute("bookVo", bookVo);
		
		return nextPage;
		
	}
	
	
	 //도서 수정 확인
	@PostMapping("/modifyBookConfirm")
	public String modifyBookConfirm(BookVo bookVo, 
									@RequestParam("file") MultipartFile file, 
									HttpSession session) {
		System.out.println("[BookController] modifyBookConfirm()");
		
		String nextPage = "admin/book/modify_book_ok";
		
		// 현재 로그인한 관리자 정보를 세션에서 가져옴
		//AdminMemberVo loginedAdminMemberVo = (AdminMemberVo) session.getAttribute("loginedAdminMemberVo");
		
		// 관리자가 로그인하지 않은 경우 로그인 페이지로 이동
		//if (loginedAdminMemberVo == null)
		//	return "redirect:/admin/member//loginForm";
		
		// 업로드된 파일이 있는지 확인 /MultipartFile 객체에서 업로드된 파일의 원래 이름을 반환
		if (!file.getOriginalFilename().equals("")) {
			
			// // 파일 업로드를 수행하고 저장된 파일의 이름을 반환
			String savedFileName = uploadFileService.upload(file);
			
			 // 파일 업로드에 성공한 경우, 썸네일 속성에 파일 이름 설정
			if (savedFileName != null)
				
				bookVo.setB_thumbnail(savedFileName);
			
		}
		
		 // 책 정보를 수정하고 결과를 반환
		int result = bookService.modifyBookConfirm(bookVo);
		
		if (result <= 0)
			nextPage = "admin/book/modify_book_ng";
		
		return nextPage;
		
	}
	
	
	
}
