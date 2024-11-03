# StockControl

StockControl é uma aplicação desktop desenvolvida em Java, utilizando JavaFX, com o objetivo de gerenciar o estoque de produtos em um ambiente de vendas e compras. O sistema permite registrar vendas e compras, monitorar o saldo de produtos em estoque, exibir alertas de baixo estoque, além de manter registros de vendas mensais para análise. Este projeto é parte de uma extensão acadêmica do curso de Ciência da Computação.

## Funcionalidades

- **Registro de Vendas**: Permite registrar a venda de produtos.
- **Controle de Estoque**: Gerencia a quantidade de itens em estoque e alerta sobre produtos com estoque baixo.
- **Interface Responsiva**: Interface desenvolvida com JavaFX para exibir dados de forma organizada e intuitiva.

## Tecnologias Utilizadas

- **Java**: Linguagem principal para a implementação da lógica de negócio.
- **JavaFX**: Framework para construção da interface gráfica.
- **MySQL**: Banco de dados relacional utilizado para armazenamento dos dados de produtos e vendas.
- **JDBC**: Biblioteca para comunicação com o banco de dados MySQL.

## Estrutura do Banco de Dados

O sistema utiliza três tabelas principais no banco de dados:

- **products**: Armazena informações dos produtos.
- **sales**: Registra as vendas realizadas.
- **buy**: Tabela de compras que registra a quantidade adquirida e os preços de compra.

## Requisitos

- **JDK 11 ou superior**
- **JavaFX**
- **MySQL**

## Instalação

### Método 1: Desenvolvimento Local

1. Clone o repositório:
    
    ```bash
    git clone <https://github.com/GuilhermeIgnacio/StockControl.git>
    ```
    
2. Configure o banco de dados MySQL
    
    ```sql
    -- Criação do banco de dados
    CREATE DATABASE IF NOT EXISTS StockDb;
    USE StockDb;
    
    -- Criação da tabela products
    CREATE TABLE products (
        product_id INT(11) NOT NULL AUTO_INCREMENT,
        product_name VARCHAR(255) NOT NULL,
        product_description TEXT,
        purchase_price DECIMAL(10,2) NOT NULL,
        retail_price DECIMAL(10,2) NOT NULL,
        stock_quantity INT(11) NOT NULL,
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        PRIMARY KEY (product_id)
    );
    
    -- Criação da tabela sales
    CREATE TABLE sales (
        sale_id INT(11) NOT NULL AUTO_INCREMENT,
        product_id INT(11) NOT NULL,
        quantity INT(11) NOT NULL,
        sale_price DECIMAL(20,2) NOT NULL,
        price_unit DECIMAL(10,2) NOT NULL,
        sale_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (sale_id),
        FOREIGN KEY (product_id) REFERENCES products(product_id)
    );
    
    -- Criação da tabela buy
    CREATE TABLE buy (
        buy_id INT(11) NOT NULL AUTO_INCREMENT,
        product_id INT(11) NOT NULL,
        quantity INT(11) NOT NULL,
        buy_price DECIMAL(20,2) NOT NULL,
        buy_price_unit DECIMAL(10,2) NOT NULL,
        buy_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (buy_id),
        FOREIGN KEY (product_id) REFERENCES products(product_id)
    );
    
    ```
    
3. Configure as credenciais de acesso ao banco de dados no arquivo `ConnectionFactory`
    
    ```java
     		// Nome de usuário para a conexão com o banco de dados, lido de um arquivo de propriedades.
        private static final String USERNAME = "root";
    
        // Senha para a conexão com o banco de dados, lida de um arquivo de propriedades.
        private static final String PASSWORD = "";
    
        // URL do banco de dados, lida de um arquivo de propriedades.
        private static final String DATABASE_URL = "jdbc:mariadb://localhost:3306/StockDb";
    ```
    
4. Instale os módulos faltantes
    - InteliJ: pressione CTRL duas vezes e execute o comando `mvn install`
    - Outras IDEs: Este projeto não foi testado em outras IDEs e talvez necessite de configurações adicionais
5. Compile e excute o projeto

### Método 2: Aplicativo

1. Acesse a [página de releases no GitHub](https://www.github.com/GuilhermeIgnacio/StockControl/releases/latest) e faça o download do arquivo `app.zip`.
2. Após o download, descompacte o arquivo `app.zip` em uma pasta de sua escolha.
3. Navegue até a pasta descompactada e acesse a subpasta `bin`.
4. Dentro da pasta `bin`, procure pelo arquivo `app.bat`.
5. Dê um duplo clique no arquivo `app.bat` para iniciar o aplicativo.

---
