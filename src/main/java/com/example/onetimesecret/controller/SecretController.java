package com.example.onetimesecret.controller;

import com.example.onetimesecret.service.SecretService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SecretController {

    private final SecretService secretService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/create")
    public String createSecret(@RequestParam("secretText") String secretText, 
                              jakarta.servlet.http.HttpServletRequest request,
                              Model model) {
        try {
            UUID secretId = secretService.createSecret(secretText);
            
            // Generate the full URL for the secret using the request's base URL
            String baseUrl = request.getScheme() + "://" + request.getServerName();
            if ((request.getScheme().equals("http") && request.getServerPort() != 80) ||
                (request.getScheme().equals("https") && request.getServerPort() != 443)) {
                baseUrl += ":" + request.getServerPort();
            }
            String secretUrl = baseUrl + "/secret/" + secretId;
            
            model.addAttribute("secretUrl", secretUrl);
            model.addAttribute("secretId", secretId);
            
            return "result";
        } catch (Exception e) {
            log.error("Error creating secret", e);
            model.addAttribute("error", "Failed to create secret. Please try again.");
            return "index";
        }
    }

    @GetMapping("/secret/{id}")
    public String viewSecret(@PathVariable("id") UUID id, Model model) {
        try {
            Optional<String> secretContent = secretService.getSecret(id);
            
            if (secretContent.isPresent()) {
                model.addAttribute("secretContent", secretContent.get());
                return "view";
            } else {
                return "404";
            }
        } catch (Exception e) {
            log.error("Error retrieving secret", e);
            model.addAttribute("error", "Failed to retrieve secret.");
            return "404";
        }
    }
}
