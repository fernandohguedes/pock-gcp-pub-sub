package com.fernandoguedes.gcp.PubSub;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PubSubPublisher {

    private final PubSubTemplate pubSubTemplate;

    public PubSubPublisher(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    @Autowired
    JacksonPubSubMessageConverter jacksonPubSubMessageConverter;

    public void publishMessage(String mensagem) {
        this.pubSubTemplate.publish("my-topic-teste", mensagem);
        System.out.println("Mensagem publicada");
    }

    public void publishMessageObject(Object mensagem) {
        this.pubSubTemplate.setMessageConverter(jacksonPubSubMessageConverter);
        this.pubSubTemplate.publish("my-topic-teste", mensagem);
        System.out.println("Mensagem publicada!!! ");
    }
}
