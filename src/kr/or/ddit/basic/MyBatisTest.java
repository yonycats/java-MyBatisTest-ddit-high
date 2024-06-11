package kr.or.ddit.basic;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import kr.or.ddit.vo.MemberVO;



public class MyBatisTest {
	public static void main(String[] args) {
		
		// MyBatis를 이용하여 DB작업을 처리하는 작업 순서
		// 1. MyBatis의 환경설정 파일을 읽어와 필요한 객체를 생성한다.
		
		SqlSessionFactory SqlSessionFactory = null; 
		
		try {
			// 1-1. XML 설정 파일 읽어오기
			Charset charset = Charset.forName("UTF-8"); // 설정 파일의 인코딩 정보 설정 (한글처리 위함) 
			Resources.setCharset(charset);
			
			Reader rd = Resources.getResourceAsReader("config/mybatis-config.xml");
			
			// 1-2. 위에서 읽어온 Reader 객체를 이용하여 SqlSessionFactory 객체를 생성한다.
			SqlSessionFactory = new SqlSessionFactoryBuilder().build(rd);
			
			rd.close(); // 스트림 닫기
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		// 2. 실행할 sql문에 맞는 쿼리문을 호출하여 원하는 작업을 수행한다.
		

		
		
		
		// 2-1. insert 작업 연습
		System.out.println("insert 작업 시작");
		
		// 1) 저장할 데이터를 VO에 담는다
		MemberVO mv = new MemberVO();
		mv.setMemId("d001");
		mv.setMemName("강감찬");
		mv.setMemTel("2222-2222");
		mv.setMemAddr("경상남도");
		
		// 2) sqlSession 객체를 이용하여 해당 쿼리문을 실행한다.
		
		// autoCommit 여부 설정, false = 오토커밋 안함
		SqlSession session = SqlSessionFactory.openSession(false); 
		
//		try {
//			// 형식) insert("namespace값.쿼리ID값", 파라미터객체);
//			// 정상적으로 실행된 레코드 수가 반환됨
//			int cnt = session.insert("memberTest.insertMember", mv);
//			
//			if (cnt > 0) {
//				System.out.println("insert 작업 성공!");
//				session.commit(); // 커밋하기
//			} else {
//				System.out.println("insert 작업 실패!");
//			}
//			
//		} catch (PersistenceException e) {
//			e.printStackTrace();
//		} finally {
//			// 커넥션풀 반납하기 전에 커밋을 하지 않으면, 롤백을 함. 반드시 커밋을 해서 반영을 해줘야함
//			session.close(); // 커넥션풀에 사용한 커넥션 반납하기
//		}
		
		
		System.out.println("-------------------------------------");
		
		
		
		
		
		// 2-2 update 연습
		System.out.println("update 작업 시작");
		
		mv = new MemberVO();
		mv.setMemId("d001");
		mv.setMemName("김고추");
		mv.setMemTel("9999-9999");
		mv.setMemAddr("충청남도");
		
		// 파라미터를 넣지 않으면 false와 같음. false = 오토커밋 안함 
		session = SqlSessionFactory.openSession();
		
		try {
			// 정상적으로 실행된 레코드 수가 반환됨
			int cnt = session.update("memberTest.updateMember", mv);
			
			if (cnt > 0) {
				System.out.println("update 작업 성공!");
				session.commit(); // 커밋하기
			} else {
				System.out.println("update 작업 실패!");
			}
			
		} catch (PersistenceException e) {
			e.printStackTrace();
			// 예외 발생하면 롤백, 여기서 하지 않아도 커밋을 하지 않았다면 close 시점에서 자동 롤백이 됨
			session.rollback();
		} finally {
			// 커넥션풀 반납하기 전에 커밋을 하지 않으면, 롤백을 함. 반드시 커밋을 해서 반영을 해줘야함
			session.close(); // 커넥션풀에 사용한 커넥션 반납하기
		}
		
		
		System.out.println("-------------------------------------");
		
		
		
		
		
		/*
		// 2-3 delete 연습
		System.out.println("delete 작업 시작");
		
		session = SqlSessionFactory.openSession(); // autoCommit false
		
		try {
			// 정상적으로 실행된 레코드 수가 반환됨
			int cnt = session.delete("memberTest.deleteMember", "d001");
			
			if (cnt > 0) {
				System.out.println("delete 작업 성공!");
				session.commit(); // 커밋하기
			} else {
				System.out.println("delete 작업 실패!");
			}
			
		} catch (PersistenceException e) {
			e.printStackTrace();
			// 예외 발생하면 롤백, 여기서 하지 않아도 커밋을 하지 않았다면 close 시점에서 자동 롤백이 됨
			session.rollback();
		} finally {
			// 커넥션풀 반납하기 전에 커밋을 하지 않으면, 롤백을 함. 반드시 커밋을 해서 반영을 해줘야함
			session.close(); // 커넥션풀에 사용한 커넥션 반납하기
		}
		*/
		
		System.out.println("=======================================");
		
		
		
		
		
		// 2-4 select 연습
		
		// 1) 응답의 결과가 여러개일 경우
		System.out.println("select 연습 (결과가 여러개일 경우)");
		
		session = SqlSessionFactory.openSession(true); // autoCommit 켜기 (select는 커밋 필요없음)
		
		try {
			List<MemberVO> memList = session.selectList("memberTest.selectAllMember");

			for (MemberVO mv2 : memList) {
				System.out.println("ID : " + mv2.getMemId());
				System.out.println("이름 : " + mv2.getMemName());
				System.out.println("전화 : " + mv2.getMemTel());
				System.out.println("주소 : " + mv2.getMemAddr());
				
				System.out.println("-----------------------------------");
			}
			System.out.println("전체 회원 정보 출력 끝");
			
		} catch (PersistenceException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		System.out.println("-----------------------------------");
		
		
		
		// 2) 응답의 결과가 1개일 경우
		/* primary키를 조회하거나, 집계함수를 조회하는 등, 무조건 리턴이 1개임이 확실할 때 */
		
		System.out.println("select 연습 (결과가 1개일 경우)");
		
		session = SqlSessionFactory.openSession(true); // autoCommit 켜기 (select는 커밋 필요없음)
		// autoCommit 기능을 켜서 트랜잭션 관리를 별도로 따로 하지 않을 것임
		
		try {
			MemberVO mv3 = session.selectOne("memberTest.getMember", "d001");
			
			System.out.println("ID : " + mv3.getMemId());
			System.out.println("이름 : " + mv3.getMemName());
			System.out.println("전화 : " + mv3.getMemTel());
			System.out.println("주소 : " + mv3.getMemAddr());

		} catch (PersistenceException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		

		System.out.println("/////////////////////////////////////////");
		
		
		
		
		// VO가 아닌 Map을 이용한 방식
		
		System.out.println("select 연습 (VO가 아닌 Map을 이용한 방식)");
		
		session = SqlSessionFactory.openSession(true);
		
		
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("memId", "d001");
			
			// 파라미터 타입을 지정 안하고 Map을 넣어도 되긴 함
			MemberVO mv3 = session.selectOne("memberTest.getMember", paramMap);
			System.out.println("ID : " + mv3.getMemId());
			System.out.println("이름 : " + mv3.getMemName());
			System.out.println("전화 : " + mv3.getMemTel());
			System.out.println("주소 : " + mv3.getMemAddr());
			
			
			Map<String, Object> resultMap = session.selectOne("memberTest.getMember2", paramMap);
			System.out.println("ID : " + resultMap.get("MEM_ID"));
			System.out.println("이름 : " + resultMap.get("MEM_NAME"));
			System.out.println("전화 : " + resultMap.get("MEM_TEL"));
			System.out.println("주소 : " + resultMap.get("MEM_ADDR"));
			

		} catch (PersistenceException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		
		
		
		System.out.println("/////////////////////////////////////////");
		
		
		
		
		// VO가 아닌 List를 이용한 방식 // foreach를 써서 함 아직 안 배움, 추후에 한번 해주신다고 함
		
		System.out.println("select 연습 (VO가 아닌 List를 이용한 방식)");
		
		session = SqlSessionFactory.openSession(true);
		
		
		try {
			List<String> list = new ArrayList<String>(); 
			list.add("d001");
			list.add("김고추");
			
			// 파라미터 타입을 지정 안하고 Map을 넣어도 되긴 함
//			MemberVO mv3 = session.selectOne("memberTest.getMember3", list);
//			System.out.println("ID : " + mv3.getMemId());
//			System.out.println("이름 : " + mv3.getMemName());
//			System.out.println("전화 : " + mv3.getMemTel());
//			System.out.println("주소 : " + mv3.getMemAddr());
			
			
//			List<String> list = session.selectOne("memberTest.getMember3", list);
//			System.out.println("ID : " + resultMap.get("MEM_ID"));
//			System.out.println("이름 : " + resultMap.get("MEM_NAME"));
//			System.out.println("전화 : " + resultMap.get("MEM_TEL"));
//			System.out.println("주소 : " + resultMap.get("MEM_ADDR"));
			

		} catch (PersistenceException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		
		
	}
}
