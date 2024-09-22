package com.shashi.srv;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SearchPasswdSrv")
public class SearchPasswdSrv extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 데이터베이스 연결 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/shopping-cart?useUnicode=true&characterEncoding=utf8";
    private static final String DB_USER = "root";  // DB 사용자 이름
    private static final String DB_PASSWORD = "1234";  // DB 비밀번호

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 사용자 입력값 가져오기
        String username = request.getParameter("username").trim();
        String email = request.getParameter("email").trim();

        // 입력값 확인 로그
        System.out.println("Received username: " + username);
        System.out.println("Received email: " + email);

        // 비밀번호 변수 초기화
        String password = null;

        // 데이터베이스 연결 및 쿼리 실행
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 연결 성공 로그
            System.out.println("Database connection established successfully.");

            // SQL 쿼리 작성
            String query = "SELECT password FROM `user` WHERE `name` = ? AND `email` = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // SQL 쿼리에 파라미터 설정
                stmt.setString(1, username);
                stmt.setString(2, email);

                // 쿼리 실행 전 로그
                System.out.println("Executing query: " + stmt);

                // 쿼리 실행 및 결과 받기
                ResultSet rs = stmt.executeQuery();

                // 결과 처리
                if (rs.next()) {
                    password = rs.getString("password");
                    // 비밀번호 확인 로그
                    System.out.println("Password found: " + password);

                    // 비밀번호를 알림창에 표시하고 로그인 페이지로 리디렉션
                    response.setContentType("text/html; charset=UTF-8");
                    response.getWriter().write(
                        "<script>alert('Your password is: " + password + "'); location.href='login.jsp';</script>");
                } else {
                    // 사용자 정보가 맞지 않을 경우
                    System.out.println("User not found with provided credentials.");
                    response.sendRedirect("searchpasswd.jsp?message=User not found.");
                }
            }
        } catch (Exception e) {
            // 예외 발생 시 로그 출력 및 에러 페이지로 리디렉션
            e.printStackTrace();
            response.sendRedirect("searchpasswd.jsp?message=Error occurred during password retrieval.");
        }
    }
}