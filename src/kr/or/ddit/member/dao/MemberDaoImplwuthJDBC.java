package kr.or.ddit.member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import kr.or.ddit.util.JDBCUtil3;
import kr.or.ddit.vo.MemberVO;

public class MemberDaoImplwuthJDBC implements IMemberDao {

	// Singleton 패턴 만들기
	// 나 자신의 타입 객체 저장을 위한 객체변수선언 (static으로 선언한다.)
	private static IMemberDao memDao = new MemberDaoImplwuthJDBC();
	
	// 기본 생성자의 타입을 private으로 설정하면 어느 곳에서도 MemberDaoImplwuthJDBC 객체를 만들 수 없다 (New)
	private MemberDaoImplwuthJDBC() {
	}
	
	// 객체를 만들지 않고도, 객체를 가져갈 수 있도록 static 메서드를 만든 것.
	public static IMemberDao getInstance() {
		return memDao;
	}
	
	
	private Connection conn;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	
	@Override
	public int insertMember(MemberVO mv) {
		
		int cnt = 0;
		
		try {
			conn = JDBCUtil3.getConnection();
			
			String sql = " INSERT INTO MYMEMBER(MEM_ID, MEM_NAME, MEM_TEL, MEM_ADDR)" + 
						 "    VALUES (?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, mv.getMemId());
			pstmt.setString(2, mv.getMemName());
			pstmt.setString(3, mv.getMemTel());
			pstmt.setString(4, mv.getMemAddr());
			
			cnt = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil3.close(conn, stmt, pstmt, rs);
		}
		
		return cnt;
	}

	
	@Override
	public int updateMember(MemberVO mv) {
		
		int cnt = 0;
		
		try {
			conn = JDBCUtil3.getConnection();
			
			String sql = " UPDATE MYMEMBER\r\n" + 
						 " SET MEM_NAME = ?, MEM_TEL = ?, MEM_ADDR = ?\r\n" + 
						 " WHERE MEM_ID = ?";
			
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, mv.getMemName());
			pstmt.setString(2, mv.getMemTel());
			pstmt.setString(3, mv.getMemAddr());
			pstmt.setString(4, mv.getMemId());
			
			cnt = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil3.close(conn, stmt, pstmt, rs);
		}
		
		return cnt;
	}

	
	@Override
	public boolean checkMember(String memId) {

		boolean isExist = false;
		
		try {
			conn = JDBCUtil3.getConnection();
			
			String sql = " SELECT COUNT(*) CNT FROM MYMEMBER WHERE MEM_ID = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, memId);
			
			rs = pstmt.executeQuery();
			
			int cnt = 0;
			
			while(rs.next()) {
				cnt = rs.getInt("CNT");
			}
			
			if (cnt > 0) {
				isExist = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil3.close(conn, stmt, pstmt, rs);
		}
		
		return isExist;
		
	}

	
	@Override
	public int deleteMember(String memId) {
		
		int cnt = 0;
		
		try {
			conn = JDBCUtil3.getConnection();
			
			String sql = " DELETE FROM MYMEMBER WHERE MEM_ID = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memId);
			
			// 리턴값은 성공여부 반환함 (0, 1)
			cnt = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil3.close(conn, stmt, pstmt, rs);
		}
		return cnt;
	}

	
	@Override
	public List<MemberVO> getAllMember() {
		
		List<MemberVO> memList = new ArrayList<MemberVO>();
		
		try {
			conn = JDBCUtil3.getConnection();
			
			stmt = conn.createStatement();
			
			// stmt.executeQuery()는 실행하는 시점에 넣어주면 됨
			rs = stmt.executeQuery(" SELECT * FROM MYMEMBER ORDER BY MEM_ID");
			
			while (rs.next()) {
				String memId = rs.getNString("mem_id");
				String memName = rs.getNString("mem_name");
				String memTel = rs.getNString("mem_tel");
				String memAddr = rs.getNString("mem_addr");
				
				LocalDate regDt = rs.getTimestamp("reg_dt").toLocalDateTime().toLocalDate();
				
				MemberVO mv = new MemberVO();
				mv.setMemId(memId);
				mv.setMemName(memName);
				mv.setMemTel(memTel);
				mv.setMemAddr(memAddr);
				mv.setRegDt(regDt);
				
				memList.add(mv);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil3.close(conn, stmt, pstmt, rs);
		}
		
		return memList;
	}
	

	// 다이나믹 쿼리 작성, 다중 조건 검색
	@Override
	public List<MemberVO> searchMember(MemberVO mv) {
		
		List<MemberVO> memList = new ArrayList<MemberVO>();
		
		try {
			conn = JDBCUtil3.getConnection();
			
			String sql = " SELECT * FROM MYMEMBER WHERE 1=1 ";
			
			// 사용자가 뭔가 입력을 한 경우 true가 됨
			// 원하는 조건의 값만 넣어서 쿼리가 만들어질 수 있다.
			// preparestatment 를 위한 다이나믹 쿼리문 => 객체가 만들어질 것임
			if ( mv.getMemId() != null && !mv.getMemId().equals("") ) {
				sql += " AND MEM_ID = ? ";
			}
			if ( mv.getMemName() != null && !mv.getMemName().equals("") ) {
				sql += " AND MEM_NAME = ? ";
			}
			if ( mv.getMemTel() != null && !mv.getMemTel().equals("") ) {
				sql += " AND MEM_TEL = ? ";
			}
			if ( mv.getMemAddr() != null && !mv.getMemAddr().equals("") ) {
				sql += " AND MEM_ADDR LIKE '%' || ? || '%' ";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			// ++을 넣어서 인덱스 값을 1씩 증가시킴
			int paramIndex = 1;
			
			// 물음표 안에 있는 값들을 적절하게 세팅함
			if ( mv.getMemId() != null && !mv.getMemId().equals("") ) {
				pstmt.setString(paramIndex++, mv.getMemId());
			}
			if ( mv.getMemName() != null && !mv.getMemName().equals("") ) {
				pstmt.setString(paramIndex++, mv.getMemName());
			}
			if ( mv.getMemTel() != null && !mv.getMemTel().equals("") ) {
				pstmt.setString(paramIndex++, mv.getMemTel());
			}
			if ( mv.getMemAddr() != null && !mv.getMemAddr().equals("") ) {
				pstmt.setString(paramIndex++, mv.getMemAddr());
			}
			
			// 쿼리 실행하기 -> execute
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				String memId = rs.getNString("mem_id");
				String memName = rs.getNString("mem_name");
				String memTel = rs.getNString("mem_tel");
				String memAddr = rs.getNString("mem_addr");
				
				LocalDate regDt = rs.getTimestamp("reg_dt").toLocalDateTime().toLocalDate();
				
				MemberVO mv2 = new MemberVO();
				mv2.setMemId(memId);
				mv2.setMemName(memName);
				mv2.setMemTel(memTel);
				mv2.setMemAddr(memAddr);
				mv2.setRegDt(regDt);
				
				memList.add(mv2);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil3.close(conn, stmt, pstmt, rs);
		}
		
		return memList;
	}

}
