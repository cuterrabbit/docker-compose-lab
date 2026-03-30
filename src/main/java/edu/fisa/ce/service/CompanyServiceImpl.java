/* 다양한 DB의 table 간에 고유한 스펙을 규정해야 하는 경우가 있을 선호하는 방식
 * interface로 스펙 제시 - 메소드 구현 로직 제외한 프로토타입만 제시
 * 구현체 개발
 */
package edu.fisa.ce.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.fisa.ce.dao.DeptRepository;
import edu.fisa.ce.dao.Emp2Repository;
import edu.fisa.ce.exception.NotExistEmp2Exception;
import edu.fisa.ce.model.domain.dto.DeptDTO;
import edu.fisa.ce.model.domain.dto.Emp2DTO;
import edu.fisa.ce.model.entity.Dept;
import edu.fisa.ce.model.entity.Emp2;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompanyServiceImpl implements CompanyService {

	/* CompanyServiceImpl empDao 변수에 생성된 객체 자동 주입
	 * 
	 * Emp2Repository 기본생성자 자동 호출
	 * CompanyServiceImpl 기본 생성자 자동 호출
	 * 
	 * CompanyServiceImpl setXxx() 호출하면서 Emp2Repository 객체 주입:@Autowired 
	 * */
	@Autowired 
	private Emp2Repository empDao;
	
	@Autowired 
	private DeptRepository deptDao;
	
	//dto와 entity간의 데이터 복제
	private ModelMapper mapper = new ModelMapper();
	
	//모든 부서 정보 검색
	/* DeptRepository 에서 받아온 Entity 자체를 controller에게 제공하지 않음
	 * DB의 select문장은 Entity와 연계
	 * findAll()는 제공받는 메소드, 반환 타입은 관련 entity를 List에 담아서 반환
	 * 
	 * service에서 주로 Entity 를 DTO로 전환
	 * 
	 * 
	 */
	public List<DeptDTO> getDeptAll(){
		List<DeptDTO> allDeptDto = deptDao.findAll().stream()
				.map(entity -> mapper.map(entity, DeptDTO.class)).toList();
		
		return allDeptDto;
	}	
	
	//검색 데이터가 없을 경우의 처리도 추후 고려해야 함
	@Override
	public float getSalByEmpnoAndEname(int empno, String ename) {
		return empDao.findSalByEmpnoAndEname(empno, ename);
	}

	
	@Override
	public boolean loginCheck(int empno, String ename) {
		//org.... .SimpleJpaRepository@6dcedb3
		System.out.println("Dao --- " + empDao);  // 객체명@위치값
		
		//select e.empno from Emp2 e where e.empno=:empno and e.ename=:ename
		int result = empDao.loginCheck(empno, ename);
		if(result == empno) {
			return true;
		}		
		return false;
	}
	
	
	
	@Override
	@Transactional
	public Emp2DTO addEmp(Emp2DTO emp) {
		Emp2 entity = mapper.map(emp, Emp2.class);
		Emp2 savedEntity = empDao.save(entity);
		
		log.info("*** 사원 등록 완료: empno={} ***", savedEntity.getEmpno());
		return mapper.map(savedEntity, Emp2DTO.class);
	}

	@Override
	public Emp2DTO getEmp(String ename) throws NotExistEmp2Exception {
		Emp2DTO emp = mapper.map(empDao.findByEname(ename), Emp2DTO.class);
		return emp;
	}

	@Override
	public List<Object[]> getEmpsbyDeptno(int deptno) {
		List<Object[]> emps = empDao.findByDeptno(deptno);
		return emps;
	}

	// 사번으로 부서 번호 수정하는 메소드
	@Override
	@Transactional
	public boolean updateDeptnoByEmpno(int empno, int newDeptno) throws NotExistEmp2Exception {
		log.info("*** 사번으로 부서 이동했습니다 ***");
		int result = empDao.updateByEmpnoDeptno(empno, newDeptno);
		if (result != 1) {
			throw new NotExistEmp2Exception("사번에 일치되는 사원이 없습니다");
		}
		return true;
	}





}
