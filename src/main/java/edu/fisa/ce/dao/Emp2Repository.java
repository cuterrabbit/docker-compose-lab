package edu.fisa.ce.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.fisa.ce.model.entity.Emp2;

@Repository
public interface Emp2Repository extends JpaRepository<Emp2, Integer>{

	//메소드명으로 sql 생성 : select * from emp where ename=?
	public Emp2 findByEname(String ename);
		
	//사번과 이름으로 해당 사원의 급여 검색
	//select * from emp where empno=? and ename=?
//	public Emp2 findSalByEmpnoAndEname(int empno, String ename);
	
	
	// pk의 int -> Integer로 자동 변환 
	@Query("select e.sal from Emp2 e where e.empno=:empno and e.ename=:ename")
	public float findSalByEmpnoAndEname(@Param("empno") int empno, @Param("ename") String ename);
	
	@Query("select e.ename, e.sal from Emp2 e where e.deptno=:deptno")
	public List<Object[]> findByDeptno(@Param("deptno") int deptno);

	
	@Modifying
	@Query("update Emp2 e set e.deptno=:deptno where e.empno=:empno")
	public int updateByEmpnoDeptno(@Param("empno") int empno, 
								   @Param("deptno") int deptno);
	
	//login 처리 로직 
	/* entity 기반의 query 문장 생성 - JPQL
	 * @Query (sql) / 메소드명은 자율
	 * 
	 * emp2 table에는 empno, ename, sal, deptno
	 * select empno from emp2 where empno=? and ename=?
	 * 
	 * Emp2 e = new Emp2();
	 */
	@Query("select e.empno from Emp2 e where e.empno=:empno and e.ename=:ename")
	public Integer loginCheck(@Param("empno") Integer empno, 
							  @Param("ename") String ename);
	
	
}


