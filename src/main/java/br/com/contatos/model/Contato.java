package br.com.contatos.model;

public record Contato(Integer id, Cliente cliente, String tipo, String valor, String observacao) {
}
