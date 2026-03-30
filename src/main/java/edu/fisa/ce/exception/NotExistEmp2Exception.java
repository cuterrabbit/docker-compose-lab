package edu.fisa.ce.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotExistEmp2Exception extends Exception{
	
	public NotExistEmp2Exception(String msg) {
		super(msg); //getMessage() 데이터 활용
	}
	
}
