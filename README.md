# banki

A Mini Banking Application. This project provides oauth2 authentication using Keycloak.
It provides sign up and sign in features for users and allow them to do basic CRUD operations to create and manage bank
accounts. It allows them to transfer money between their own accounts and see their transaction history.

## How to start the application

### Prerequisites

- [Docker](https://www.docker.com/) required to initiate postgresql, keycloak and optional pgAdmin4
- [Java](https://www.azul.com/downloads/#zulu) 21
- [Node.js](https://nodejs.org/en) 20
- (optional) IntelliJ IDEA Community Edition.

### Running the backend

- Go to project folder **banki**
- run `docker compose up` command and wait until all services are initialized.
- Go to project folder **banki/backend**
- run `./gradlew bootRun` and wait until backend is initialized.
- Once backend starts it will prompt "Started BankingApplication in x seconds"
- The swagger doc is accessible at the following link http://localhost:8080/swagger-ui/index.html

### Running the frontend

- Go to project folder **banki/frontend**
- run `npm install` and wait until all the dependencies are installed.
- run `npm run start` and wait until the application is build.
- Once build finishes app should automatically open a browser window but incase it doesn't open the app is accessible
  at http://localhost:3000/login

Thats it 🥳! The application is now ready for use.