package com.fernandoguedes.gcp.controllers;

import com.fernandoguedes.gcp.PubSub.PubsubConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/consumidor")
public class SubscriberController {

    @Autowired
    private PubsubConsumer pubsubConsumer;

    @GetMapping("/mensagem")
    public void publicar() throws Exception {
        pubsubConsumer.consumidor();

        System.out.println("Recebida no controller !!!");
    }
}
