package edu.fisa.ce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.fisa.ce.service.CompanyService;

@Controller
@SessionAttributes({"empno", "ename"})
public class LoginController {
	/* CompanyController와 LoginController에는 같은 타입의 멤버 변수가 선언
	 * 질문?
	 * CompanyServiceImpl객체가 동일한 객체가 활용
	 */
    @Autowired 
    private CompanyService service; //inteface 타입이나 실제 자식타입의 객체
    
	  /* 로그인 성공 - 세션에 데이터 저장
     * 	: 사번과 이름 다 저장 
     * 로그인 실패 - 실패 처리 화면으로 이동 
     */
    //로그인 (empno, ename) 
    //http://127.0.0.1:8081/emp/login?empno=10&ename=기영
	@RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginCheck(@RequestParam("empno") int empno, 
    						 @RequestParam("ename") String ename, Model session) {
		System.out.println("alldeptget.jsp로 이동 전 " + ename + " " + empno);
		boolean result = service.loginCheck(empno, ename);
		
		if(result == true) {
	    	//세션에 데이터 저장  
	    	session.addAttribute("empno", empno);
	    	session.addAttribute("ename", ename);
	    	return "redirect:/alldeptget.jsp";
		}else {
			return "redirect:/failview.jsp";
		}
    }
}
