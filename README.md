# Sergipe Tec Challenge

Esta é uma aplicação Spring Boot conectada a um banco de dados PostgreSQL.

## Instruções de Uso

Siga os passos abaixo para configurar e executar a aplicação localmente.

### 1. Criar o Banco de Dados

Primeiro, você precisa criar o banco de dados no postgresql. Use um cliente SQL como pgAdmin ou a linha de comando do postgresql.

**Comando para criar o banco de dados:**
```sql
CREATE DATABASE bank_challenge_db;
```
### 2.  Modificar as Credenciais no application.properties
No arquivo src/main/resources/application.properties, você encontrará as seguintes configurações:
![image](https://github.com/user-attachments/assets/4fb298d6-84fd-4ced-b450-bca7bc729206)

Se o nome de usuário e a senha do seu PostgreSQL forem diferentes das padrões (postgres), atualize as linhas:

**spring.datasource.username: Substitua pelo nome de usuário do seu PostgreSQL.**

**spring.datasource.password: Substitua pela senha do seu PostgreSQL.**

### 3.  Iniciar a execução da aplicação

Para rodar a aplicação, navegue até o diretório raiz do projeto e execute o seguinte comando:

```./mvnw spring-boot:run```
### 4. Acessar a Aplicação
Depois que a aplicação for iniciada, você poderá acessá-la em:
http://localhost:8080

### 5. Autenticação
![image](https://github.com/user-attachments/assets/f90c008b-f8c5-4ef1-89a8-5c7e38027209)

Ao acessar o link citado acima automaticamente será redirecionado para uma página de autenticação. 
#### O username e senha padrão para acessar estão abaixo: ####
**Username: admin**


**Password: admin**

### 6. A aplicação já estará pronta para uso!

## Tecnologias Utilizadas

Este projeto utiliza as seguintes tecnologias:

- **[Spring Boot](https://spring.io/projects/spring-boot)** - Framework para construção de aplicações Java.
- **[PostgreSQL](https://www.postgresql.org/)** - Sistema de gerenciamento de banco de dados relacional.
- **Hibernate/JPA** - Mapeamento objeto-relacional (ORM) para persistência de dados.
- **Maven**-  Gerenciamento de dependências.
- **[Lombok](https://projectlombok.org/)** - Para os getters and setters.
- **[Thymeleaf](https://www.thymeleaf.org/)** - Template engine para renderização de páginas HTML no lado do servidor.
- **Java 17**


## UML - Diagramas 
![uml_diagram_class](https://github.com/user-attachments/assets/c04eabe6-1dd3-4adb-a0fb-bf7c0f7f03e2)



