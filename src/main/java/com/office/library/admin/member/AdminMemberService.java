package com.office.library.admin.member;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

/*
 * AdminMemberService는 @Service에 의해 이미 스프링 컨테이너에 빈 객체로 생성되어 있으므로 AdminMemberController는 의존 객체 자동 주입 방법으로 AdminMemberSerive를 별도의 생성 과정 없이 사용할 수 있음 
AdminMemberController에 @Autowired를 이용해서 AdminMemberSerive를 멤버 필드로 선언하는 코드
서비스 객체로 데이터베이스와 통신하기 위해 DAO를 이용함
 * */

@Service
public class AdminMemberService {
	
	/*
	 * createAccountConfirm()이 0 이하의 값(-1 또는 0)을 컨트롤러에게 반환하게 되면 관리자 회원가입은 실패이고, 1을 반환하게 되면 회원가입은 성공임
AdminMemberDao에 중복 아이디를 체크하고 회원정보를 추가하는 기능을 구현하기

	 * */
	@Autowired //메일 발송 JavaMailSenderImpl 사용하기 위해 자동 주입
	JavaMailSenderImpl javaMailSenderImpl;
	
	final static public int ADMIN_ACCOUNT_ALREADY_EXIST =0;
	final static public int ADMIN_ACCOUNT_CREATE_SUCCESS =1;
	final static public int ADMIN_ACCOUNT_CREATE_FAIL=-1;
	
	
	@Autowired
	AdminMemberDao adminMemberDao;
	
		public int createAccountConfirm(AdminMemberVo adminMemberVo) {
			
			boolean isMember = adminMemberDao.isAdminMember(adminMemberVo.getA_m_id());
			//아이디가 있으면 true, 없으면 false
			if(!isMember) {
				// 같은 아이디가 없으면 내용 저장 
				int result = adminMemberDao.insertAdminAccount(adminMemberVo);
				
				if(result > 0) {
				return	ADMIN_ACCOUNT_CREATE_SUCCESS;//1
				}else {
				return	ADMIN_ACCOUNT_CREATE_FAIL;//-1
				}
				
			}else {
				return ADMIN_ACCOUNT_ALREADY_EXIST;//0
			}
			
		}
		
		// 관리자 로그인 인증을 위한 메서드
		public AdminMemberVo loginConfirm(AdminMemberVo adminMemberVo) {
				
			
			AdminMemberVo loginedAdminMemberVo = adminMemberDao.selectAdmin(adminMemberVo);
			
			if (loginedAdminMemberVo != null)
				
				System.out.println("[AdminMemberService] ADMIN MEMBER LOGIN SUCCESS!");
			else
				System.out.println("[AdminMemberService] ADMIN MEMBER LOGIN FAIL!");
			
			return loginedAdminMemberVo;
		}
		
		// 일반 관리자 목록 조회하기
		public List<AdminMemberVo> listupAdmin(){
			
			return adminMemberDao.selectAdmins();
		}
		
		// 일반 관리자 승인하기
		public void setAdminApproval(int a_m_no) {
			
			int result = adminMemberDao.updateAdminAccount(a_m_no);
		}
		
		//관리자 계정 정보 수정
		public int modifyAccountConfirm(AdminMemberVo adminMemberVo) {
			
			return adminMemberDao.updateAdminAccount(adminMemberVo);
			
		}

		//관리자 정보를 불러오는 메서드
		public AdminMemberVo getLoginedAdminMemberVo(int a_m_no) {
		
			
			return adminMemberDao.selectAdmin(a_m_no);
			
		}
		
		//새 비밀번호 메일로 발송하기
		public int findPasswordConfirm(AdminMemberVo adminMemberVo) {

			
			AdminMemberVo selectedAdminMemberVo = adminMemberDao.selectAdmin(adminMemberVo.getA_m_id(), 
																			 adminMemberVo.getA_m_name(), 
																			 adminMemberVo.getA_m_mail());
			
			int result = 0;
			
			if (selectedAdminMemberVo != null) {
				//입력한 관리자의 정보가 없으면 새 비밀번호를 생성하라는 메서드 호출
				String newPassword = createNewPassword();
				
				result = adminMemberDao.updatePassword(adminMemberVo.getA_m_id(), newPassword);
				
				// 업데이트가 완료되면 메일발송하라는 메서드 호출(입력 받은 이메일,새로운 비번)
				if (result > 0)
					sendNewPasswordByMail(adminMemberVo.getA_m_mail(), newPassword);
			}
			
	        	return result;
			
		}
		
		// 새 비밀번호 생성하는 메서드
		private String createNewPassword() {
			
			char[] chars = new char[] {
					'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
					'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 
					'u', 'v', 'w', 'x', 'y', 'z'
					};

			
			StringBuffer stringBuffer = new StringBuffer();
			// 랜덤으로 숫자를 하나씩 가져오는 것
			SecureRandom secureRandom = new SecureRandom();
			// 현재 시간을 기준을 secureRandom에 넣음
			secureRandom.setSeed(new Date().getTime());
			
			int index = 0;
			int length = chars.length;
			
			for (int i = 0; i < 8; i++) {
				index = secureRandom.nextInt(length);
			
				if (index % 2 == 0) 
					stringBuffer.append(String.valueOf(chars[index]).toUpperCase());
				else
					stringBuffer.append(String.valueOf(chars[index]).toLowerCase());
			
			}
			
			return stringBuffer.toString();
			
		}
		
		// 메일 발송하는 메서드 선언
		private void sendNewPasswordByMail(String toMailAddr, String newPassword) {
			
			//메일을 발송하는 인터페이스
			final MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
				
				@Override
				public void prepare(MimeMessage mimeMessage) throws Exception {
					
					//메일 보낼 준비하는 class
					final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
					
					mimeMessageHelper.setTo("a91904634@gmail.com");
					//입력받은 이메일
					//mimeMessageHelper.setTo(toMailAddr);
					// 내용
					mimeMessageHelper.setSubject("[한국 도서관] 새 비밀번호 안내입니다.");
					mimeMessageHelper.setText("새 비밀번호 : " + newPassword, true);
					
				}
				
			};
			//메일 발송 JavaMailSenderImpl을 위에 선언해놨음.
			javaMailSenderImpl.send(mimeMessagePreparator);
			
		}
		
		
}
