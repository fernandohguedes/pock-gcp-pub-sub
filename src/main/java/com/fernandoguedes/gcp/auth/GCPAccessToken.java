package com.fernandoguedes.gcp.auth;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
@ComponentScan
public class GCPAccessToken {

    // injetando a classe de configuração
    @Autowired
    private GoogleCloudProperties cloudProperties;

    //Pega o token apontando para o arquivo JSON direto
    private GoogleCredentials credential;

    public  GoogleCredentials getCredentialDirectFile() throws IOException {
        // recuperando o arquivo json
        String securityKeyFile = cloudProperties.getSecurityKeyFile();

        credential = GoogleCredentials.fromStream(new ClassPathResource(securityKeyFile).getInputStream())
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/devstorage.read_only"));

        credential.refreshIfExpired();

        // retorna o objeto GoogleCredentials com as credenciais nele
        return credential;
    }
}
