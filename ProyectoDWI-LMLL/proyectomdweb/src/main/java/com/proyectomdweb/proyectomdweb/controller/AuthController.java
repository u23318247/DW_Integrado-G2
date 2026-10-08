package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.dtos.UsuarioRegistroDTO;
import com.proyectomdweb.proyectomdweb.service.JwtService;
import com.proyectomdweb.proyectomdweb.service.UsuarioService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService    userDetailsService;
    private final JwtService            jwtService;
    private final UsuarioService        usuarioService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // vista login.html
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("username") String username, 
                                @RequestParam("password") String password, 
                                HttpServletResponse response) {
        try {
            // 1. Autentica al usuario
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 2. Crea Token JWT
            String token = jwtService.generarToken(userDetails);
            
            // 3. Almacena en una Cookie Segura
            Cookie cookie = new Cookie("jwt_token", token);
            cookie.setHttpOnly(true);  // Protege contra ataques XSS (JS no puede leerla)
            cookie.setSecure(false);    // Cambiar a true para producciones reales
            cookie.setPath("/");          // Disponible en toda la aplicación
            cookie.setMaxAge(86400);  // Duración de 1 día
            response.addCookie(cookie);
            
            //  4. Verifica si es administrador o vendedor
            boolean tieneAccesoPanel = userDetails.getAuthorities().stream()
                    .anyMatch(rol -> rol.getAuthority().equals("ROLE_ADMIN") || rol.getAuthority().equals("ROLE_VENDEDOR"));
            
            if (tieneAccesoPanel) {
                return "redirect:/admin/dashboard"; // Panel de gestión para vendedor y admin
            } 
            // 5. Manda clientes a la tienda
            return "redirect:/"; 
        } catch (Exception e) {
            return "redirect:/auth/login?error=true"; 
        }
    }
    // Metodo para cerrar sesión destruyendo la cookie
    @GetMapping("/logout")
    public String cerrarSesion(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt_token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Esto borra la cookie inmediatamente
        response.addCookie(cookie);
        return "redirect:/";
    }

    //-- METODOS DE REGISTRO DE USUARIO --//

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro"; // Apunta a registro.html
    }

    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute UsuarioRegistroDTO registroDTO) {

        // 1. Verifica si el correo ya está registrado
        if (usuarioService.existePorEmail(registroDTO.getEmail())) {
            return "redirect:/auth/registro?error=email";
        }
        // 2. El UsuarioService se encarga de mapear, encriptar y guardar internamente
        usuarioService.registrarNuevoUsuario(registroDTO);

        // 3. Redirige al login con un mensaje de éxito
        return "redirect:/auth/login?exito=registro";
    }


        
}