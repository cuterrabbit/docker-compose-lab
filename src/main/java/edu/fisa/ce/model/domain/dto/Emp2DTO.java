package edu.fisa.ce.model.domain.dto;

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
public class Emp2DTO {
	private Integer empno;
	private String ename;
	private float sal;
	private int deptno;
}
