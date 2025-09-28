package domain.in;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@SpringBootApplication
public class SSOAuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SSOAuthServerApplication.class, args);
	}

	@RequestMapping({
	    "/{path:[^\\.]*}", 
	    "/{path:[^\\.]*}/{path1:[^\\.]*}", 
	    "/{path:[^\\.]*}/{path1:[^\\.]*}/{path2:[^\\.]*}"
	})
	public String redirect() {
	    return "forward:/";
	}

}
