package edu.fisa.ce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.fisa.ce.exception.NotExistEmp2Exception;
import edu.fisa.ce.model.domain.dto.DeptDTO;
import edu.fisa.ce.model.domain.dto.Emp2DTO;

@Service   
public interface CompanyService {
	
	//모든 부서 검색
	public List<DeptDTO> getDeptAll();	
	
	
	//사원 등록
	public Emp2DTO addEmp(Emp2DTO emp);
	
	//사원명으로 해당 사원 모든 정보 검색
	public Emp2DTO getEmp(String ename) throws NotExistEmp2Exception;
		
	//부서번호로 해당 부서에 소속된 모든 직원의 이름, 급여만 검색
	public List<Object[]> getEmpsbyDeptno(int deptno);
	
	//사번을 기준으로 부서번호 수정
	public boolean updateDeptnoByEmpno(int empno, int newDeptno) throws NotExistEmp2Exception;
	
	//로그인 로직 
	//empno, ename으로 empno가 검색되면 true 반환
	//무효할 경우 false 반환 (예외 발생 추가도 가능)
	public boolean loginCheck(int empno, String ename);
	 
	//empno, ename으로 sql 검색
	public float getSalByEmpnoAndEname(int empno, String ename);
	
}
