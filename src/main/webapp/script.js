
function fecharModal(id) {
    document.getElementById(id).classList.add('hidden');
}

function mascaraCPF(input) {
    let valor = input.value.replace(/\D/g, "");
    if (valor.length <= 11) {
        valor = valor.replace(/(\d{3})(\d)/, "$1.$2");
        valor = valor.replace(/(\d{3})(\d)/, "$1.$2");
        valor = valor.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
    }
    input.value = valor;
}

function mascaraCEP(input) {
    input.value = input.value.replace(/\D/g, "").replace(/^(\d{5})(\d)/, "$1-$2");
}

function buscarCep() {
    const cep = document.getElementById('cep').value.replace(/\D/g, '');
    if (cep.length === 8) {
        fetch('consultarCep?cep=' + cep)
            .then(res => res.json())
            .then(data => {
                if (!data.erro) {
                    document.getElementById('logradouro').value = data.logradouro || "";
                    document.getElementById('bairro').value = data.bairro || "";
                    document.getElementById('localidade').value = data.localidade || "";
                    document.getElementById('estado').value = data.uf || data.estado || "";
                }
            })
            .catch(err => console.error(err));
    }
}



function preencherTabela(clientes) {
    const corpoTabela = document.getElementById('tabela-clientes-corpo');
    corpoTabela.innerHTML = "";
    
    
    document.querySelector('.table-section').classList.remove('hidden');

    clientes.forEach(cliente => {
        const idReal = cliente.id || cliente.cli_id;
        const cidade = cliente.endereco ? cliente.endereco.localidade : "N/A";
        const uf = cliente.endereco ? cliente.endereco.estado : "N/A";
        
        
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${cliente.nome}</td>
            <td>${cliente.cpf}</td>
            <td>${cidade}/${uf}</td>
            <td>
                <button class="btn-edit" onclick='editarCliente(${JSON.stringify(cliente)})'>Editar</button>
                <button class="btn-contacts" onclick="abrirContatos(${idReal}, '${cliente.nome}')">Contatos</button>
            </td>`;
        corpoTabela.appendChild(tr);
    });
}


function abrirContatos(id, nome) {
    document.getElementById('modal-contatos').classList.remove('hidden');
    document.getElementById('nome-cliente-contato').innerText = nome;
    document.getElementById('contato-cliente-id').value = id;
    carregarContatos(id);
}

function carregarContatos(clienteId) {
    fetch(`listarContatos?clienteId=${clienteId}`)
        .then(res => res.json())
        .then(contatos => {
            const corpo = document.getElementById('corpo-tabela-contatos');
            corpo.innerHTML = "";
            contatos.forEach(c => {
                corpo.innerHTML += `
                    <tr>
                        <td>${c.tipo}</td>
                        <td>${c.valor}</td>
                        <td>
                            <button class="btn-delete" onclick="excluirContato(${c.id}, ${clienteId})">Excluir</button>
                        </td>
                    </tr>`;
            });
        })
        .catch(err => console.error(err));
}

function salvarNovoContato() {
    const id = document.getElementById('contato-cliente-id').value;
    const tipo = document.getElementById('tipo-contato-modal').value;
    const valor = document.getElementById('valor-contato-modal').value;
    fetch(`salvarContato?clienteId=${id}&tipo=${tipo}&valor=${valor}`, { method: 'POST' })
        .then(res => {
            if (res.ok) {
                document.getElementById('valor-contato-modal').value = "";
                carregarContatos(id);
            }
        });
}

function excluirContato(id, clienteId) {
    if (confirm("Deseja excluir este contato?")) {
        fetch(`salvarContato?id=${id}`, { method: 'DELETE' })
            .then(res => {
                if (res.ok) carregarContatos(clienteId);
            });
    }
}

function aplicarMascaraDinamica(input) {
    const tipo = document.getElementById('tipo-contato-modal').value;
    let valor = input.value;
    if (tipo === "TELEFONE") {
        valor = valor.replace(/\D/g, "");
        if (valor.length <= 11) {
            valor = valor.replace(/^(\d{2})(\d)/g, "($1) $2");
            if (valor.length > 13) valor = valor.replace(/(\d{5})(\d)/, "$1-$2");
            else valor = valor.replace(/(\d{4})(\d)/, "$1-$2");
        }
        input.value = valor.substring(0, 15);
    } else {
        input.value = valor.trim().toLowerCase();
    }
}

function limparELocarFoco() {
    const input = document.getElementById('valor-contato-modal');
    input.value = "";
    input.focus();
    const tipo = document.getElementById('tipo-contato-modal').value;
    input.placeholder = tipo === "TELEFONE" ? "(00) 00000-0000" : "exemplo@email.com";
}

window.onload = function () {
    carregarClientes();
};

function salvarNovoContato() {
    const idCliente = document.getElementById('contato-cliente-id').value;
    const tipo = document.getElementById('tipo-contato-modal').value;
    const valor = document.getElementById('valor-contato-modal').value;

    fetch(`salvarContato?clienteId=${idCliente}&tipo=${tipo}&valor=${valor}`, {
        method: 'POST'
    })
        .then(res => {
            if (res.ok) {
                document.getElementById('valor-contato-modal').value = "";
                carregarContatos(idCliente);
            } else {
                alert("Erro ao salvar contato no servidor.");
            }
        })
        .catch(err => console.error("Erro na requisição:", err));
}

function exibirDetalhesCliente(cliente) {

    document.querySelector('.table-section').classList.add('hidden');
    document.getElementById('form-section').classList.add('hidden');

    const view = document.getElementById('detalhes-cliente');
    view.classList.remove('hidden');

    document.getElementById('view-nome').innerText = cliente.nome;
    document.getElementById('view-cpf').innerText = cliente.cpf;
    document.getElementById('view-nascimento').innerText = cliente.dataNascimento;

    if (cliente.endereco) {
        document.getElementById('view-endereco').innerText =
            `${cliente.endereco.logradouro}, nº ${cliente.endereco.numero} - ${cliente.endereco.bairro}`;
        document.getElementById('view-cidade-uf').innerText =
            `${cliente.endereco.localidade}/${cliente.endereco.estado}`;
        document.getElementById('view-cep').innerText = cliente.endereco.cep;
    }

    document.getElementById('btn-editar-view').onclick = () => {
        fecharDetalhes();
        editarCliente(cliente);
    };

    const idReal = cliente.id || cliente.cli_id;
    document.getElementById('btn-contatos-view').onclick = () => {
        abrirContatos(idReal, cliente.nome);
    };
}





function fecharDetalhes() {
    document.getElementById('detalhes-cliente').classList.add('hidden');
    document.querySelector('.table-section').classList.remove('hidden');
}


function formatarDataBR(dataISO) {
    if (!dataISO) return "";
    const [ano, mes, dia] = dataISO.split('-');
    return `${dia}/${mes}/${ano.slice(-2)}`;
}



function toggleForm() {
    const formSection = document.getElementById('form-section');
    const isOpening = formSection.classList.contains('hidden');

    if (isOpening) {
        const form = formSection.querySelector('form');
        form.reset();
        document.getElementById('cliente-id-hidden').value = "";
        document.getElementById('endereco-id-hidden').value = "";
        document.getElementById('form-title').innerText = "Cadastrar Novo Cliente";
        document.getElementById('btn-excluir-editar').style.display = "none";
    }

    formSection.classList.toggle('hidden');
}

function editarCliente(cliente) {
    document.getElementById('form-title').innerText = "Editar Cliente";
    
    document.getElementById('form-section').classList.remove('hidden');
    document.querySelector('.table-section').classList.add('hidden');
    document.getElementById('detalhes-cliente').classList.add('hidden');

    const btnExcluirEdicao = document.getElementById('btn-excluir-editar');
    btnExcluirEdicao.style.display = "inline-block";

    const form = document.querySelector('#form-section form');
    const idCli = cliente.id || cliente.cli_id;
    const idEnd = cliente.endereco ? (cliente.endereco.id || cliente.endereco.end_id) : "";

    form.querySelector('[name="id"]').value = idCli;
    form.querySelector('[name="idEndereco"]').value = idEnd;
    form.querySelector('[name="nome"]').value = cliente.nome;
    form.querySelector('[name="cpf"]').value = cliente.cpf;
    form.querySelector('[name="dataNascimento"]').value = cliente.dataNascimento;

    if (cliente.endereco) {
        form.querySelector('[name="cep"]').value = cliente.endereco.cep;
        form.querySelector('[name="logradouro"]').value = cliente.endereco.logradouro;
        form.querySelector('[name="numero"]').value = cliente.endereco.numero;
        form.querySelector('[name="bairro"]').value = cliente.endereco.bairro;
        form.querySelector('[name="localidade"]').value = cliente.endereco.localidade;
        form.querySelector('[name="estado"]').value = cliente.endereco.estado;
    }

    btnExcluirEdicao.onclick = function() {
        excluirCliente(idCli, idEnd);
    };

    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function exibirFichaCliente(cliente) {
    document.querySelector('.table-section').classList.add('hidden');
    document.getElementById('form-section').classList.add('hidden');

    const ficha = document.getElementById('detalhes-cliente');
    ficha.classList.remove('hidden');

    const p = cliente.dataNascimento.split('-');
    const dataFormatada = `${p[2]}/${p[1]}/${p[0].slice(-2)}`;

    document.getElementById('view-nome').innerText = cliente.nome;
    document.getElementById('view-cpf').innerText = cliente.cpf;
    document.getElementById('view-nascimento').innerText = dataFormatada;

    if (cliente.endereco) {
        document.getElementById('view-logradouro').innerText = cliente.endereco.logradouro;
        document.getElementById('view-numero').innerText = cliente.endereco.numero;
        document.getElementById('view-bairro').innerText = cliente.endereco.bairro;
        document.getElementById('view-localidade').innerText = cliente.endereco.localidade;
        document.getElementById('view-estado').innerText = cliente.endereco.estado;
        document.getElementById('view-cep').innerText = cliente.endereco.cep;
    }

    document.getElementById('btn-editar-ficha').onclick = function () {
        fecharDetalhes();
        editarCliente(cliente);
    };

    document.getElementById('btn-contatos-ficha').onclick = function () {
        const idCliente = cliente.id || cliente.cli_id;
        abrirContatos(idCliente, cliente.nome);
    };

    document.getElementById('btn-excluir-ficha').onclick = function () {
        const idCli = cliente.id || cliente.cli_id;
        const idEnd = cliente.endereco ? (cliente.endereco.id || cliente.endereco.end_id) : null;
        if (idEnd) excluirCliente(idCli, idEnd);
    };
}

function carregarClientes() {
    const campoBusca = document.querySelector('input[name="busca"]');
    const termo = campoBusca ? campoBusca.value.trim() : "";

    fetch(`clientes?busca=${encodeURIComponent(termo)}`)
        .then(res => res.json())
        .then(dados => {
            if (dados.length === 1 && termo !== "") {
                exibirFichaCliente(dados[0]);
            } else {
                fecharDetalhes();
                preencherTabela(dados);
            }
        })
        .catch(err => console.error(err));
}

function excluirCliente(idCliente, idEndereco) {
    if (confirm("Tem certeza que deseja excluir este cliente e seu endereço?")) {
        fetch(`clientes?id=${idCliente}&idEndereco=${idEndereco}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                alert("Cliente removido com sucesso!");
                fecharDetalhes();
                toggleForm();
                document.getElementById('form-section').classList.add('hidden');
                carregarClientes();
            } else {
                alert("Erro ao excluir cliente.");
            }
        })
        .catch(err => console.error(err));
    }
}