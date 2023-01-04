package com.example.demo;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.result.view.RedirectView;
 
@Controller
public class AppController {
 
    @Autowired
    private UserRepository userRepo;
     
    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }
    @GetMapping("/profile")
    public String viewProfile(Model model,@ModelAttribute("user") @Validated User user,BindingResult result) {
    	
    	 Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
    	    String email = loggedInUser.getName();
    	    
    	    User user1 = userRepo.findByEmail(email);
    	    String pic = user1.getPhotos();
    	    String pathid = user1.getId().toString();
    	
    	model.addAttribute("userpic",  pic);
    	model.addAttribute("userpathid",  pathid);
    	
        return "photo";
    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
         
        return "signup_form";
    }
    @PostMapping("/process_register")
    public String processRegister(User user, @RequestParam("image") MultipartFile multipartFile)throws IOException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
         
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        user.setPhotos(fileName);
         
        User savedUser = userRepo.save(user);
 
        String uploadDir = "user-photos/" + savedUser.getId();
 
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        
         
        return "register_success";
    }
    @GetMapping("/login")
    public String viewLoginPage() {
    
    return "login";
    }
}
