package br.com.meetime.hubspot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class DashboardController {

	@GetMapping("/")
    public RedirectView mostrarPaginaHubSpot() {
        return new RedirectView("/Dashboard.html"); 
    }
	
}
