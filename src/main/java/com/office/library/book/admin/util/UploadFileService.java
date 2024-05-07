package com.office.library.book.admin.util;

import java.io.File;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileService {

	public String upload(MultipartFile file) {
		
		//작업의 성공 또는 실패
		boolean result = false;
		
		// File 저장
		String fileOriName = file.getOriginalFilename();
		
		//파일 이름에서 파일 확장자를 추출, lastIndexOf은 마지막 위치 인덱스
		String fileExtension = 
				fileOriName.substring(fileOriName.lastIndexOf("."), fileOriName.length());
		
		//업로드된 파일이 저장될 디렉토리를 지정
		String uploadDir = "C:\\library\\upload\\";
		
		//업로드된 파일을 위한 고유 식별자(UUID)를 생성
		UUID uuid = UUID.randomUUID();
		
		//UUID를 문자열로 변환하고 하이픈을 제거하여 파일의 고유한 이름을 생성
		//UUID는 고유성 보장 중복된 값을 방지
		String uniqueName = uuid.toString().replaceAll("-", "");
		
		//업로드된 파일을 저장할 대상 경로를 나타내는 File 객체를 만듬
		File saveFile = new File(uploadDir + "\\" + uniqueName + fileExtension);
		
		//저장할 디렉토리가 존재하는지 확인하고 존재하지 않으면 디렉토리를 만듬
		if (!saveFile.exists())
			saveFile.mkdirs();
		
		//업로드된 파일을 지정된 위치로 전송하려 시도
		//성공하면 result를 true로 설정하고 그렇지 않으면 스택 트레이스를 출력
		try {
			file.transferTo(saveFile); //저장할 경로에 파일 전송
			result = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		//파일 업로드가 성공한 경우(result가 true인 경우
		if (result) {
			System.out.println("[UploadFileService] FILE UPLOAD SUCCESS!!");
			return uniqueName + fileExtension; //파일 고유한 이름 + 파일 확장자
			
		} else {
			System.out.println("[UploadFileService] FILE UPLOAD FAIL!!");
			return null;
			
		}

	}
	
}
