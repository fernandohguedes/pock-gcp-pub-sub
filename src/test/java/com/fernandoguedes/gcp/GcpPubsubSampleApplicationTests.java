package com.fernandoguedes.gcp;

import com.fernandoguedes.gcp.auth.GCPAccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
@AutoConfigureMockMvc
class GcpPubsubSampleApplicationTests {

    @Autowired
    GCPAccessToken gcpAccessToken;

    @Test
    public void shoulReturnTokenFromDirectFile() throws IOException {
        // instanciando o GoogleCredentials e chamando nosso metodo que usa o JSON para se autenticar
        GoogleCredentials credentialDirectFile = gcpAccessToken.getCredentialDirectFile();

        // Salvando o conteudo do AccessToken em uma String
        String accessToken = credentialDirectFile.getAccessToken().toString();

        // Imprime na tela o valor do Token
        System.out.printf("AccessToken: %s", accessToken);
    }
}
