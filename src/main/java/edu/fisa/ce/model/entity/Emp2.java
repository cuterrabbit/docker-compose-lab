package edu.fisa.ce.model.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Emp2 { 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer empno;
	private String ename;
	private float sal;
	private int deptno;
}
