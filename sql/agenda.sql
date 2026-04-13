CREATE TABLE Cliente(
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	endereco_id INTEGER NOT NULL,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    FOREIGN KEY (endereco_id) REFERENCES Endereco(id)
);

CREATE TABLE Endereco(
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
    cep VARCHAR(9) NOT NULL,
    logradouro VARCHAR(100) NOT NULL,
    complemento VARCHAR(100) NOT NULL,
    numero INTEGER NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    localidade VARCHAR(100) NOT NULL,
    estado VARCHAR(100) NOT NULL
);

CREATE TABLE Contato(
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	cliente_id INTEGER,
    tipo VARCHAR(50),
    valor VARCHAR(100),
    observacao VARCHAR(255),
    FOREIGN KEY (cliente_id) REFERENCES Cliente(id)
);

