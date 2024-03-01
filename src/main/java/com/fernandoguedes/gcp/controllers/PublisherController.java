package com.fernandoguedes.gcp.controllers;

import com.fernandoguedes.gcp.DTO.UsuarioDTO;
import com.fernandoguedes.gcp.PubSub.PubSubPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/filas")
public class PublisherController {

    @Autowired
    private PubSubPublisher pubSubPublisher;

    @GetMapping("/publish/{mensagem}")
    public void publicar(@PathVariable String mensagem) {
        pubSubPublisher.publishMessage(mensagem);
        System.out.println("Recebida no controller !!!");
    }

    @PostMapping
    public ResponseEntity<?> publicarObjeto(@RequestBody UsuarioDTO usuarioDTO) {
        System.out.println("Controller recebendo requisição de objeto");
        System.out.println("Mensagem: " + usuarioDTO.toString());
        pubSubPublisher.publishMessageObject(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
    }
}
