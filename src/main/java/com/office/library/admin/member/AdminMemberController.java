package com.office.library.admin.member;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/member")
public class AdminMemberController {

	@Autowired
	AdminMemberService adminMemberService;
	
	//회원가입 form으로 넘어가는 메서드
	@RequestMapping(value ="/createAccountForm",method = RequestMethod.GET)
	public String createAccountForm() {
		
		
		String nextPage = "admin/member/create_account_form";
		
		return nextPage;
	}
	
	//회원가입 한 내용을 가져오는 메서드
	@PostMapping("/createAccountConfirm")
	public String createAccountConfirm(AdminMemberVo adminMemberVo) {
		
		
		String nextPage = "admin/member/create_account_ok";
		
		//관리자 아이디가 있는 아이디인지 체크 / 성공해서 1이 넘어오면 nextpage로 이동
		int result = adminMemberService.createAccountConfirm(adminMemberVo);
		
		// 회원가입 실패할 경우
		if(result <= 0) {
			nextPage = "admin/member/create_account_ng";
		}
		return nextPage;
	}
	
	// 바로 뷰를 만들고 이동하는 단순한 구조 (로그인 폼페이지로)
	@GetMapping("loginForm")
	public String loginForm() {
		
		
		String nextPage = "admin/member/login_form";
		
		return nextPage;
	}
	
	@PostMapping("loginConfirm")
	public String loginConfirm(AdminMemberVo adminMemberVo,HttpSession session) {
		
		
		String nextPage = "admin/member/login_ok";
		
		// null 아니면 AdminMemberVo가 반환이 됨 , null이 아닌 경우 세션에 저장을 할 수 있음
		AdminMemberVo logAdminMemberVo = adminMemberService.loginConfirm(adminMemberVo);
		
		if(logAdminMemberVo == null) {
			
			nextPage = "admin/member/login_ng";
		}else {
			session.setAttribute("loginedAdminMemberVo", logAdminMemberVo);
			session.setMaxInactiveInterval(60*30); //세션 유효기간을 설정하는 메서드(단위:초)
		}
		return nextPage;
	}
	
	@RequestMapping(value = "/logoutConfirm",method = RequestMethod.GET)
	public String logoutConfirm(HttpSession session) {
		
		String nextPage = "redirect:/admin"; //admin/home.jsp가 클라이언트에 응답이 됨
		
		session.invalidate(); // 세션을 무효화시키는 것으로 세션에 저장된 데이터 사용x,로그인 상태 해제
		
		return nextPage;
	}
	
	// 관리자 리스트 조회하기 
	@RequestMapping("listupAdmin")
	public String listupAdmin(Model model) {
		
		String nextPage = "admin/member/listup_admins";
		
		List<AdminMemberVo> adminMemberVos = adminMemberService.listupAdmin();
		
		model.addAttribute("adminMemberVos", adminMemberVos);
		
		return nextPage;
		
	}
	
	// 일반 관리자 승인하는 메서드
	@GetMapping("/setAdminApproval")
	public String setAdminApproval(@RequestParam("a_m_no") int a_m_no) {
		
		String nextPage = "redirect:/admin/member/listupAdmin";
		
		adminMemberService.setAdminApproval(a_m_no);
		
		return nextPage;
	}
	
	//회원 정보 수정 메서드
	@GetMapping("/modifyAccountForm")
	public String modifyAccountForm(HttpSession session) {
		
		String nextPage = "admin/member/modify_account_form";
		
		AdminMemberVo loginedAdminMemberVo = (AdminMemberVo) session.getAttribute("loginedAdminMemberVo");
		// 로그인이 안되어 있으면(세션 유지가 안되어 있으면)
		if (loginedAdminMemberVo == null)
			nextPage = "redirect:/admin/member/login_form";
		
		return nextPage;
		
	}
	
	//회원 정보 수정 확인
	@PostMapping("/modifyAccountConfirm")
	public String modifyAccountConfirm(AdminMemberVo adminMemberVo, HttpSession session) {
		
		
		String nextPage = "admin/member/modify_account_ok";
		
		// 관리자 정보를 수정하는 메서드
		int result = adminMemberService.modifyAccountConfirm(adminMemberVo);
		
		if (result > 0) {
			//관리자 정보를 조회하는 메서드
			AdminMemberVo loginedAdminMemberVo = adminMemberService.getLoginedAdminMemberVo(adminMemberVo.getA_m_no());
			
			session.setAttribute("loginedAdminMemberVo", loginedAdminMemberVo);
			session.setMaxInactiveInterval(60 * 30);
			
		} else {
			nextPage = "admin/member/modify_account_ng";
			
		}
		
		return nextPage;
		
	}
	// 비밀번호 찾기 눌렀을때 폼으로 이동
	@GetMapping("/findPasswordForm")
	public String findePasswordForm() {
		
		
		String nextPage = "admin/member/find_password_form";
		
		return nextPage;
		
	}
	
	// 비밀번호 찾기 확인
	@PostMapping("/findPasswordConfirm")
	public String findPasswordConfirm(AdminMemberVo adminMemberVo) {
		
		
		String nextPage = "admin/member/find_password_ok";
		
		int result = adminMemberService.findPasswordConfirm(adminMemberVo);
		
		if (result <= 0)
			nextPage = "admin/member/find_password_ng";
		
		return nextPage;
		
	}
	
	
}
