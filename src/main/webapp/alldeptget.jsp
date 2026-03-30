<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% System.out.println("alldeptget.jsp 호출 확인"); %>    
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	로그인 성공	
	<br>
	사번 : -${sessionScope.empno}-<br>
	이름 : -${sessionScope.ename}-<br>
	
	<br><hr><br>
	
	<%-- alldept의 url 요청시 비동기 형식으로 json 배열을 출력 --%>
	<button id="btn">모든 부서 정보 보기</button>
	
	<div id="deptsView"></div>
	<script>
  	document.getElementById("btn").addEventListener("click", async () => {
	
  		
    try {
      // 1. 서버 요청
      const response = await fetch("/emp/alldept");
	  
      // 2. JSON 변환
      const deptList = await response.json();
      
      // 3️. table 생성
      const table = document.createElement("table");
      table.border = "1";
      table.style.borderCollapse = "collapse";

      // 4️. thead
      const thead = document.createElement("thead");
      thead.innerHTML = `
        <tr>
          <th>부서번호</th>
          <th>부서명</th>
          <th>지역</th>
        </tr>
      `;
      table.appendChild(thead);

      // 3️. tbody
      const tbody = document.createElement("tbody");

      deptList.forEach(dept => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
          <td>\${dept.deptno}</td>
          <td>\${dept.dname}</td>
          <td>\${dept.loc}</td>
        `;

        tbody.appendChild(tr);
      });

      table.appendChild(tbody);

      // 4️. 화면에 출력
      document.getElementById("deptsView").appendChild(table);

    } catch (error) {
      console.error(error);
      alert("데이터 조회 실패");
    }
  });
</script>
	
	
</body>
</html>


