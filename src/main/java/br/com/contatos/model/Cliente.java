package br.com.contatos.model;

import java.time.LocalDate;

public record Cliente(Integer id,String nome, String cpf, LocalDate dataNascimento, Endereco endereco) {

}
