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
            
            // Generate the full URL for the secret
            // Check for X-Forwarded-Host header (used by Render and other proxies)
            String forwardedHost = request.getHeader("X-Forwarded-Host");
            String forwardedProto = request.getHeader("X-Forwarded-Proto");
            
            String baseUrl;
            if (forwardedHost != null && !forwardedHost.isEmpty()) {
                // Use forwarded headers from proxy
                String protocol = (forwardedProto != null && !forwardedProto.isEmpty()) ? forwardedProto : "https";
                baseUrl = protocol + "://" + forwardedHost;
            } else {
                // Fallback to request details (for local development)
                baseUrl = request.getScheme() + "://" + request.getServerName();
                if ((request.getScheme().equals("http") && request.getServerPort() != 80) ||
                    (request.getScheme().equals("https") && request.getServerPort() != 443)) {
                    baseUrl += ":" + request.getServerPort();
                }
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
