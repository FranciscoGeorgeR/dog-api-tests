# Dog API — Automated Test Suite

Projeto de automação de testes de API para a [Dog CEO API](https://dog.ceo/dog-api/documentation), desenvolvido como solução para o desafio técnico de QA.

---

## Índice

- [Objetivo](#objetivo)
- [Stack Tecnológica](#stack-tecnológica)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do Ambiente](#configuração-do-ambiente)
- [Executando os Testes](#executando-os-testes)
- [Relatório de Resultados](#relatório-de-resultados)
- [Endpoints Testados](#endpoints-testados)
- [Casos de Teste](#casos-de-teste)
- [Pipeline CI/CD](#pipeline-cicd)

---

## Objetivo

Desenvolver um conjunto de testes automatizados para validar os endpoints da Dog CEO API, garantindo que:

- ✅ A API responde corretamente com os status HTTP esperados
- ✅ Os dados retornados estão no formato e tipo corretos
- ✅ A aplicação se comporta conforme esperado em cenários positivos e negativos
- ✅ As URLs de imagens seguem o padrão CDN oficial
- ✅ O tempo de resposta está dentro de limites aceitáveis

---

## Stack Tecnológica

| Ferramenta | Versão | Finalidade |
|---|---|---|
| Java | 11 | Linguagem de programação |
| Maven | 3.8+ | Gerenciamento de build e dependências |
| TestNG | 7.9.0 | Framework de testes com suporte a DataProvider e suites |
| REST Assured | 5.4.0 | Client HTTP para testes de API no estilo BDD (given/when/then) |
| Allure | 2.25.0 | Relatórios HTML interativos com agrupamento por feature |
| Jackson | 2.16.1 | Deserialização de JSON para objetos Java tipados (POJOs) |
| Hamcrest | 2.2 | Matchers expressivos para asserções |
| Logback / SLF4J | 1.4.14 | Logging estruturado durante a execução |
| GitHub Actions | — | Pipeline CI/CD com execução automática |

---

## Estrutura do Projeto

```
dog-api-tests/
├── .github/
│   └── workflows/
│       └── ci.yml                          # Pipeline GitHub Actions
├── src/
│   └── test/
│       ├── java/
│       │   └── dogapi/
│       │       ├── models/
│       │       │   └── DogApiResponse.java # POJOs de resposta da API
│       │       ├── tests/
│       │       │   ├── BreedsListAllTest.java  # 12 testes → GET /breeds/list/all
│       │       │   ├── BreedImagesTest.java    # 19 testes → GET /breed/{breed}/images
│       │       │   └── RandomImageTest.java    # 12 testes → GET /breeds/image/random
│       │       └── utils/
│       │           ├── ApiConfig.java      # Constantes centralizadas
│       │           ├── BaseTest.java       # Setup global com @BeforeClass
│       │           └── RequestSpecFactory.java # Specs REST Assured reutilizáveis
│       └── resources/
│           └── testng.xml                  # Definição da suite de testes
└── pom.xml                                 # Dependências e plugins Maven
```

---

## Pré-requisitos

| Requisito | Versão mínima | Como verificar |
|---|---|---|
| Java JDK | 11 | `java -version` |
| Maven | 3.8 | `mvn -version` |
| Git | qualquer | `git --version` |
| Conexão à internet | — | Acesso a `dog.ceo` necessário |

> Não é necessário instalar servidores locais, bancos de dados ou qualquer outro serviço. Os testes fazem chamadas HTTP diretamente à API pública.

---

## Configuração do Ambiente

### 1. Instalar o Java JDK 11

**Windows / macOS / Linux:** Baixe o JDK 11 em https://adoptium.net e siga o instalador.

Após instalar, confirme:
```bash
java -version
# Saída esperada: openjdk version "11.x.x"
```

### 2. Instalar o Maven

**Windows:**
1. Baixe o `apache-maven-3.x.x-bin.zip` em https://maven.apache.org/download.cgi
2. Extraia para `C:\maven`
3. Adicione `C:\maven\apache-maven-3.x.x\bin` à variável de ambiente `Path`

**macOS (Homebrew):**
```bash
brew install maven
```

**Linux (apt):**
```bash
sudo apt install maven
```

Confirme a instalação:
```bash
mvn -version
# Saída esperada: Apache Maven 3.x.x
```

### 3. Clonar o repositório

```bash
git clone git@github.com:FranciscoGeorgeR/dog-api-tests.git
cd dog-api-tests
```

### 4. Baixar dependências (opcional)

O Maven faz isso automaticamente ao rodar os testes, mas você pode adiantar:
```bash
mvn dependency:resolve
```

---

## Executando os Testes

### Executar toda a suite

```bash
mvn clean test
```

### Executar apenas uma classe específica

```bash
# Apenas testes do endpoint /breeds/list/all
mvn clean test -Dtest=BreedsListAllTest

# Apenas testes do endpoint /breed/{breed}/images
mvn clean test -Dtest=BreedImagesTest

# Apenas testes do endpoint /breeds/image/random
mvn clean test -Dtest=RandomImageTest
```

### Executar um único método de teste

```bash
mvn clean test -Dtest=BreedsListAllTest#shouldContainLabradorBreed
```

### Resultado esperado ao executar com sucesso

```
[INFO] Tests run: 43, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: ~20 s
[INFO] BUILD SUCCESS
```

---

## Relatório de Testes (Allure) 
 
O relatório completo com todos os resultados está disponível publicamente:
 
🔗 **[Ver Relatório Allure](https://franciscogeorger.github.io/dog-api-tests/)**
 
> Atualizado automaticamente a cada push na branch `main`.
 

### Gerar e abrir o relatório Allure

Após executar os testes (`mvn clean test`), rode:

```bash
# Gerar o HTML do relatório
mvn allure:report

# Abrir no navegador automaticamente
mvn allure:serve
```

### O que o relatório contém

| Seção | Conteúdo |
|---|---|
| **Overview** | Total de testes, taxa de sucesso, duração |
| **Suites** | Resultados agrupados por classe de teste |
| **Behaviors** | Agrupamento por `@Epic / @Feature / @Story` |
| **Timeline** | Execução dos testes ao longo do tempo |
| **Cada teste** | Request e response HTTP completos capturados |
| **Falhas** | Stack trace detalhado e contexto da falha |

### Relatório Surefire (alternativo, sem instalação extra)

Gerado automaticamente em `target/surefire-reports/`. Abra no navegador:

```
target/surefire-reports/index.html
```

---

## Endpoints Testados

### `GET /breeds/list/all`

Retorna todas as raças e sub-raças disponíveis.

```json
{
  "message": {
    "hound": ["afghan", "blood", "english", "ibizan", "plott", "walker"],
    "labrador": [],
    "poodle": ["miniature", "standard", "toy"]
  },
  "status": "success"
}
```

### `GET /breed/{breed}/images`

Retorna todas as imagens de uma raça específica.

```json
{
  "message": [
    "https://images.dog.ceo/breeds/labrador/n02099712_1.jpg",
    "https://images.dog.ceo/breeds/labrador/n02099712_2.jpg"
  ],
  "status": "success"
}
```

Resposta para raça inválida:
```json
{
  "status": "error",
  "message": "Breed not found (master breed does not exist)",
  "code": 404
}
```

### `GET /breeds/image/random`

Retorna uma imagem aleatória de qualquer raça.

```json
{
  "message": "https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg",
  "status": "success"
}
```

---

## Casos de Teste

**Total: 43 testes | ✅ 43 passando | ❌ 0 falhando**

### BreedsListAllTest — 12 casos

| Caso de Teste | Tipo | Severidade |
|---|---|---|
| GET /breeds/list/all retorna HTTP 200 com JSON | Funcional | 🔴 Blocker |
| Response contém campos `message` e `status` | Estrutural | 🔴 Blocker |
| Campo `status` é igual a `"success"` | Funcional | 🟠 Critical |
| Campo `message` é um mapa não vazio | Estrutural | 🟠 Critical |
| Raça `labrador` está presente na lista | Funcional | 🟠 Critical |
| Raça `hound` está presente na lista | Funcional | 🟡 Normal |
| Raça `bulldog` está presente na lista | Funcional | 🟡 Normal |
| Raça `poodle` está presente na lista | Funcional | 🟡 Normal |
| Sub-raças de todos os breeds são arrays | Estrutural | 🟠 Critical |
| `hound` possui sub-raças `afghan` e `blood` | Funcional | 🟡 Normal |
| Nomes de raças são apenas letras minúsculas | Contrato | 🟡 Normal |
| Tempo de resposta inferior a 5 segundos | Performance | ⚪ Minor |

### BreedImagesTest — 19 casos

| Caso de Teste | Tipo | Severidade |
|---|---|---|
| HTTP 200 para `labrador` | Funcional | 🔴 Blocker |
| HTTP 200 para `hound` | Funcional | 🔴 Blocker |
| HTTP 200 para `bulldog` | Funcional | 🔴 Blocker |
| HTTP 200 para `retriever` | Funcional | 🔴 Blocker |
| HTTP 200 para `poodle` | Funcional | 🔴 Blocker |
| Campo `status` é `"success"` para labrador | Funcional | 🟠 Critical |
| Lista de imagens de labrador não é vazia | Funcional | 🟠 Critical |
| Todas as URLs seguem o padrão CDN | Contrato | 🟠 Critical |
| URLs contêm o nome da raça no caminho | Contrato | 🟡 Normal |
| URLs utilizam esquema HTTPS | Segurança | 🟡 Normal |
| Sub-raça `hound/afghan` retorna HTTP 200 | Funcional | 🟡 Normal |
| Sub-raça `hound/blood` retorna HTTP 200 | Funcional | 🟡 Normal |
| Sub-raça `bulldog/english` retorna HTTP 200 | Funcional | 🟡 Normal |
| Sub-raça `retriever/golden` retorna HTTP 200 | Funcional | 🟡 Normal |
| Campo `message` é array de strings | Estrutural | 🟠 Critical |
| Tempo de resposta inferior a 8 segundos | Performance | ⚪ Minor |
| Raça inválida retorna HTTP 404 | Negativo | 🟠 Critical |
| Raça inválida: campo `status` é `"error"` | Negativo | 🟠 Critical |
| Raça inválida: mensagem de erro não vazia | Negativo | 🟡 Normal |

### RandomImageTest — 12 casos

| Caso de Teste | Tipo | Severidade |
|---|---|---|
| GET /breeds/image/random retorna HTTP 200 | Funcional | 🔴 Blocker |
| Response contém campos `message` e `status` | Estrutural | 🔴 Blocker |
| Campo `status` é igual a `"success"` | Funcional | 🟠 Critical |
| Campo `message` é uma string não vazia | Estrutural | 🟠 Critical |
| URL corresponde ao padrão CDN | Contrato | 🟠 Critical |
| URL utiliza esquema HTTPS | Segurança | 🟡 Normal |
| URL termina com extensão de imagem válida | Contrato | 🟡 Normal |
| URL contém `images.dog.ceo/breeds/` no caminho | Contrato | 🟡 Normal |
| 5 chamadas retornam ao menos 2 URLs distintas | Aleatoriedade | 🟡 Normal |
| 3 chamadas consecutivas retornam HTTP 200 | Disponibilidade | 🟡 Normal |
| Tempo de resposta inferior a 5 segundos | Performance | ⚪ Minor |
| Content-Type da resposta é `application/json` | Contrato | 🟡 Normal |

---

## Pipeline CI/CD

O projeto inclui um workflow de **GitHub Actions** (`.github/workflows/ci.yml`) que executa automaticamente os testes em push.

O pipeline realiza:
1. Checkout do repositório
2. Configuração do Java 11 (Eclipse Temurin)
3. Execução de todos os testes com `mvn clean test`
4. Geração do relatório Allure com `mvn allure:report`
5. Publicação dos artefatos (resultados, relatório HTML e logs)
6. Falha do pipeline caso algum teste não passe

Para executar o pipeline manualmente, acesse a aba **Actions** no repositório GitHub e clique em **Run workflow**.

---

## Autor

Francisco George Rodrigues de Sousa<br>
Analista de Qualidade / QA Automation
