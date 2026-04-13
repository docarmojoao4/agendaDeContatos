package br.com.contatos.service;

import br.com.contatos.exception.ViaCepException;
import br.com.contatos.model.Endereco;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ViaCepService {

    public Endereco buscarPorCep(String cep) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://viacep.com.br/ws/"+cep.replace("-","")+"/json/"))
                    .build();
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ViaCepException("Erro ao conectar com o serviço de CEP.");
            }

            if (response.body().contains("\"erro\": \"true\"")) {
                throw new ViaCepException("Erro ao consultar o endereço.");
            }

            String json = response.body();
            Gson gson = new Gson();
            Endereco endereco = gson.fromJson(json, Endereco.class);

            System.out.println(endereco);
            return endereco;

        } catch (IOException | InterruptedException e) {
            throw new ViaCepException("Falha de rede ao buscar o endereço.");
        }


    }
}
