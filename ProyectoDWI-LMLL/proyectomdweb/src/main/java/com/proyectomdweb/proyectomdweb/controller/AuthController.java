package com.proyectomdweb.proyectomdweb.controller;

import com.proyectomdweb.proyectomdweb.dtos.UsuarioRegistroDTO;
import com.proyectomdweb.proyectomdweb.service.JwtService;
import com.proyectomdweb.proyectomdweb.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService    userDetailsService;
    private final JwtService            jwtService;
    private final UsuarioService        usuarioService;

    @GetMapping("/login")
    public String mostrarLogin(org.springframework.security.core.Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            boolean tieneAccesoPanel = auth.getAuthorities().stream()
                    .anyMatch(rol -> rol.getAuthority().equals("ROLE_ADMIN") 
                                  || rol.getAuthority().equals("ROLE_VENDEDOR")
                                  || rol.getAuthority().equals("ROLE_ALMACENERO")
                                  || rol.getAuthority().equals("ROLE_REPARTIDOR"));
            return tieneAccesoPanel ? "redirect:/admin/dashboard" : "redirect:/";
        }
        return "login"; // vista login.html
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("username") String username, 
                                @RequestParam("password") String password, 
                                HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            // 1. Autentica al usuario
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 2. Crea Token JWT
            String token = jwtService.generarToken(userDetails);
            
            // 3. Almacena en una Cookie Segura (dura lo mismo que el token)
            agregarCookieJwt(request, response, token, Duration.ofMillis(jwtService.getExpirationMs()));
            
            //  4. Verifica si es administrador, vendedor, almacenero o repartidor
            boolean tieneAccesoPanel = userDetails.getAuthorities().stream()
                    .anyMatch(rol -> rol.getAuthority().equals("ROLE_ADMIN") 
                                  || rol.getAuthority().equals("ROLE_VENDEDOR")
                                  || rol.getAuthority().equals("ROLE_ALMACENERO")
                                  || rol.getAuthority().equals("ROLE_REPARTIDOR"));
            
            if (tieneAccesoPanel) {
                return "redirect:/admin/dashboard"; // Panel de gestión para personal operativo y admin
            } 
            // 5. Manda clientes a la tienda
            return "redirect:/"; 
        } catch (Exception e) {
            return "redirect:/auth/login?error=true"; 
        }
    }
    // Metodo para cerrar sesión destruyendo la cookie
    @GetMapping("/logout")
    public String cerrarSesion(HttpServletRequest request, HttpServletResponse response) {
        agregarCookieJwt(request, response, "", Duration.ZERO); // maxAge 0 borra la cookie inmediatamente
        return "redirect:/auth/login?logout=true";
    }

    /**
     * Cookie del JWT para las vistas Thymeleaf:
     * - HttpOnly: JavaScript no puede leerla (protege contra XSS).
     * - Secure según request.isSecure(): con HTTPS solo viaja cifrada; en http://localhost sigue funcionando.
     *   Detrás de un proxy (nginx) depende de server.forward-headers-strategy (perfil prod).
     * - SameSite=Lax: no se envía en POST desde otros sitios, pero sí al volver de PayU (GET).
     */
    private void agregarCookieJwt(HttpServletRequest request, HttpServletResponse response, String valor, Duration duracion) {
        ResponseCookie cookie = ResponseCookie.from("jwt_token", valor)
                .httpOnly(true)
                .secure(request.isSecure())
                .sameSite("Lax")
                .path("/")
                .maxAge(duracion)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
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