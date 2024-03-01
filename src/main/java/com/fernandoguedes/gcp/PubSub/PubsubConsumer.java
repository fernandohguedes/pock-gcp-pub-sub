package com.fernandoguedes.gcp.PubSub;

import com.fernandoguedes.gcp.DTO.UsuarioDTO;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;

import com.google.pubsub.v1.AcknowledgeRequest;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PubsubConsumer {

    private static final String PROJECT_ID = "pubsub-teste-375317";
    private static final String SUBSCRIPTION_ID = "my-subscription-teste";
    private static final Integer NUMBER_MESSAGES = 10;
    // 20MB (maximum message size).
    private static final Integer MAX_INBOUND_MESSAGE_SIZE = 20 * 1024 * 1024;

    private final PubSubTemplate pubSubTemplate;

    public PubsubConsumer(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }


    @Autowired
    JacksonPubSubMessageConverter jacksonPubSubMessageConverter;

//    implements ApplicationRunner
    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.pubSubTemplate.subscribe("my-subscription-teste", basicAcknowledgeablePubsubMessage -> {
            System.out.println("Mensagem Recebida " + basicAcknowledgeablePubsubMessage.toString());
            basicAcknowledgeablePubsubMessage.ack();
        });
    }

    //Comentar esse cara aqui
    public void teste() throws Exception {
        this.pubSubTemplate.setMessageConverter(jacksonPubSubMessageConverter);

        var usuario = pubSubTemplate.subscribeAndConvert("my-subscription-teste",
                convertedBasicAcknowledgeablePubsubMessage -> {
                    System.out.println("Mensagem Recebida " + convertedBasicAcknowledgeablePubsubMessage.getPayload().toString());
                    convertedBasicAcknowledgeablePubsubMessage.ack();
                }, UsuarioDTO.class);


    }

    // Não propagar essa exception para o controller aplicar o tratamento correto.
    public void consumidor() throws IOException {
        System.out.println("=======> Começando o pull de mensagens <=======");

        // Prepara o objeto que define o tamanho do retorno que pode podemos receber
        SubscriberStubSettings subscriberStubSettings = SubscriberStubSettings.newBuilder()
                .setTransportChannelProvider(SubscriberStubSettings.defaultGrpcTransportProviderBuilder()
                        .setMaxInboundMessageSize(MAX_INBOUND_MESSAGE_SIZE).build()
                ).build();

        try (SubscriberStub subscriber = GrpcSubscriberStub.create(subscriberStubSettings)) {
            String subscriptionName = ProjectSubscriptionName.format(PROJECT_ID, SUBSCRIPTION_ID);

            // prepara o objeto  setando o número de mensagens que queremos receber, e passamos as configurações  de projeto subscription
            PullRequest pullRequest = PullRequest.newBuilder().setMaxMessages(NUMBER_MESSAGES).setSubscription(subscriptionName).build();

            // chamada que retorna o objeto que contem ou não mensagens
            PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);

            // quando não houver mensagens finalizar o processo
            if (pullResponse.getReceivedMessagesList().isEmpty()) {
                System.out.println("Nenhuma mensagem encontrada");
                return;
            }

            //  System.out.println(pullResponse.getReceivedMessagesList().get(0));
            System.out.println(pullResponse.getReceivedMessagesList());

            // tentativa de conversão do objeto, se funcionar separar em um método para conversão de uma lista
            // que pode conter 1 ou mais mensagens vamos definir o número máximo de mensagens
            //  var usuarioTeste = jacksonPubSubMessageConverter.fromPubSubMessage(pullResponse.getReceivedMessagesList().get(0).getMessage(), UsuarioDTO.class);
            var usuarios = converteMensagemEmObjeto(pullResponse.getReceivedMessagesList());

            List<String> ackIds = new ArrayList<>();
            for (ReceivedMessage message : pullResponse.getReceivedMessagesList()) {
                ackIds.add(message.getAckId());
            }

            // prepara o objeto para fazer o ACK
            AcknowledgeRequest acknowledgeRequest = AcknowledgeRequest.newBuilder()
                    .setSubscription(subscriptionName)
                    .addAllAckIds(ackIds)
                    .build();

            //chamada para fazer o ACK das mensagens
            subscriber.acknowledgeCallable().call(acknowledgeRequest);

            System.out.println("Total de mensagens encontradas: " + pullResponse.getReceivedMessagesList().size());
            System.out.println("Resultado do conversão: ===> " + usuarios.toString() + " <===");
        }
    }

    private List<UsuarioDTO> converteMensagemEmObjeto(List<ReceivedMessage> mensagens) {
        return mensagens.stream()
                .map(menssage -> jacksonPubSubMessageConverter.fromPubSubMessage(menssage.getMessage(), UsuarioDTO.class))
                .collect(Collectors.toList());
    }
}
