package br.com.contatos.model;

public record Endereco(Integer id,String cep, String logradouro, String complemento,
                       Integer numero, String bairro, String localidade, String estado) {
}
