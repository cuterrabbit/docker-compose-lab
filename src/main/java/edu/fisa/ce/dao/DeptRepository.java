package edu.fisa.ce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.fisa.ce.model.entity.Dept;

@Repository
public interface DeptRepository extends JpaRepository<Dept, Integer>{
	
}
