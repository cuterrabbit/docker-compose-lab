package edu.fisa.ce.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.fisa.ce.exception.NotExistEmp2Exception;
import edu.fisa.ce.model.domain.dto.DeptDTO;
import edu.fisa.ce.model.domain.dto.Emp2DTO;
import edu.fisa.ce.service.CompanyService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController 
@SessionAttributes({"empno", "ename"})
public class CompanyController {

    @Autowired 
    private CompanyService service;
    
    /* 비동기 처리
     * - json 배열로 응답 
     */
    //모든 부서 정보 검색 - dept  
    /* DeptDTO(c, m, v에서 다 사용) 개발 후
     * 1. Dept entity(model에서만 db와만 소통)
     * 2. Repository 생성? 아니면 기존 Emp2Repository 에 작업?
     * 
     * http://127.0.0.1:8081/emp/alldept
     */
    @GetMapping("/deptall")
    public List<DeptDTO> getAllDept(){
    	return service.getDeptAll();
    }

    //사번과 이름으로 급여 검색
    @GetMapping("/salget") // http://ip:port/emp/salget
    public String getSal(@RequestParam("empno") int empno, @RequestParam("ename") String ename){
    	//getSal edu.fisa.ce.service.CompanyServiceImpl@4aa604ae
    	System.out.println("getSal " + service);
    	service.getSalByEmpnoAndEname(empno, ename);
    	return "검색결과 서비스 impl에서 확인";    	
    }
        
    // 사원 등록
    /* @RequestBody : json 포멧으로 client가 서버에 전송
     * 서버는 json 포멧 형식으로 받고 key를 멤버 변수와 동기화  
     */
    @PostMapping("/add")
    public String addEmp(@RequestBody Emp2DTO emp) {
    	Emp2DTO savedEmp = service.addEmp(emp);
        log.info("사원 등록 완료: {}", savedEmp);
        return "사원 등록 완료";
    }

    // 사원 조회 
    @GetMapping("/get")
    public Emp2DTO getEmpByEname(@RequestParam("ename") String ename) throws NotExistEmp2Exception {
        return service.getEmp(ename);
    }
 
    // 부서별 직원 조회
    @GetMapping("/dept")
    public List<Object[]> getEmpsByDept(@RequestParam int deptno) {
        return service.getEmpsbyDeptno(deptno);
    }

    // 사원 부서 이동
    @PostMapping("/dept")
    public String updateEmpDept(@RequestParam int empno, @RequestParam int newDeptno) throws NotExistEmp2Exception {
        service.updateDeptnoByEmpno(empno, newDeptno);
        return "사원 부서 이동 성공";
    }

    // 예외 전담 처리 메소드
    // 이 메소드로 try~catch 문장의 간소화 : spring의 매력 
    @ExceptionHandler(NotExistEmp2Exception.class)
    public String handleNotExistEmp(NotExistEmp2Exception e) {
        return e.getMessage();
    }
}
