# agendaDeContatos

---

```markdown
# Agenda de Contatos - Cadastro de Clientes

Este projeto é uma aplicação Java Web desenvolvida como parte de um desafio técnico. A solução consiste em um sistema para gerenciamento de clientes e endereços, com persistência em banco de dados relacional e uma interface frontend integrada.

## 🏗️ Estrutura do Projeto

A aplicação segue o padrão de arquitetura em camadas para facilitar a manutenção e escalabilidade:

* **br.com.contatos.servlet**: Atua como a camada de controle, gerenciando as requisições HTTP.
* **br.com.contatos.service**: Camada intermediária que contém as regras de negócio.
* **br.com.contatos.dao**: Responsável pela persistência e comunicação direta com o banco de dados (Data Access Object).
* **br.com.contatos.model**: Contém as classes de modelo/entidades (Cliente e Endereco).
* **br.com.contatos.exception**: Classes para tratamento de erros e exceções customizadas.
* **webapp**: Interface do usuário desenvolvida com tecnologias web padrão.

## 📁 File Tree

```text
agendaContatos/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/contatos/
│   │   │       ├── dao/         # Persistência de dados (SQL)
│   │   │       ├── exception/   # Tratamento de erros
│   │   │       ├── model/       # Entidades de negócio
│   │   │       ├── service/     # Lógica da aplicação
│   │   │       └── servlet/     # Controladores (Endpoints)
│   │   ├── resources/           # Configurações de ambiente
│   │   └── webapp/              # Frontend (UI)
│   │       ├── index.html
│   │       ├── script.js
│   │       └── style.css
│   └── test/                    # Testes do sistema
├── entregaveis/
│   └── fluxograma.png           # Diagrama da operação de cadastro
├── sql/
│   └── script_db.sql            # Script de criação e população do DB
├── .gitignore
└── pom.xml                      # Dependências Maven
```

## 🛠️ Tecnologias Utilizadas

* **Java 17**
* **Maven** (Gerenciamento de dependências)
* **Servlets** (Java EE / Jakarta EE)
* **SQL** (Banco de dados relacional)
* **HTML5, CSS3 e JavaScript** (Frontend)

## 📦 Dependências

* `Jakarta Servlet API`: Para manipulação de requisições web.
* `Driver JDBC`: Para conexão com o banco de dados.
* `Jackson/Gson`: Para conversão de objetos Java em JSON (caso utilizado).

## 🚀 Instruções para Execução

1.  **Banco de Dados**: 
    * Localize o arquivo em `/sql/script_db.sql`.
    * Execute os comandos no seu gerenciador de banco de dados para criar as tabelas e inserir os dados iniciais.
2.  **Configuração**:
    * Certifique-se de que as credenciais do banco em `src/main/resources` estejam corretas.
3.  **Rodar a Aplicação**:
    * Utilize um servidor como Tomcat ou o comando Maven:
    ```bash
    mvn spring-boot:run 
    ```

## ✅ Checklist de Implementação

- [x] Estrutura de pacotes organizada.
- [x] CRUD completo de clientes e endereços.
- [x] Scripts SQL de criação e população inclusos.
- [x] Fluxograma de processo criado e anexado.
- [x] Frontend funcional integrado ao backend.

## 🔗 Referências

* [Documentação Oficial do Java](https://docs.oracle.com/en/java/)
* [Jakarta Servlet](https://jakarta.ee/specifications/servlet/)

## 🤖 Uso de IA

O desenvolvimento deste desafio contou com o auxílio de IA (Gemini) para:
* **Estruturação da documentação técnica**: Organização e formatação deste arquivo README.
* **Refinamento e revisão de código**: Otimização de lógica e revisão das melhores práticas de desenvolvimento.
* **Impacto**: Aumento da produtividade na escrita de documentação e garantia de um código mais limpo e estruturado.
```
