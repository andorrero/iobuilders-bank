# iobuilders-bank Project

#### Considerations
- This Rest is developed by Quarkus and Java 17
- Use H2 database
- Test with JUnit5
- Use hibernate and Panache for management of the repositories
- Use public and private key for the generation of the tokens
files:<br>
resources/publicKey.pem<br>
resources/privateKey.pem<br>
- Use a base64 encode/decode strategy to storage a password in database

#### Endpoints
The idea is that the user do the login to retrieve a valid token to let the user invoke the endpoints<br>
Exists an image file named <b>resources/iobuilders-bank.png</b> with a possible ui to define the aim of endpoints<br> 
The endpoints:
- GET /bank/login: let to do the login and returns valid token (all roles)
- POST /bank/user: let create the user (only role admin)
- GET /bank/user/{userId}: retrieve the user (only role admin)
- POST /bank/wallet: create the wallet (roles admin and user)
- GET /bank/wallet/{walletId}: retrieve the wallet and movements (roles admin and user)
- POST /wallet/{walletId}/deposit: create a deposit (roles admin and user)
- POST /wallet/transference: create a transference between wallets the wallet (roles admin and user)

#### Database
- Used H2 database.
- Exists a sql script file to create the schema (resources/import.sql)
- Exists an admin user inserted into the script.sql
- Database credentials:<br>
user: sa<br>
pass: sa<br>
JDBC url: jdbc:h2:mem:walletdb


#### Run Quarkus (dev profile) for IOBuilders-bank
```shell script
./mvnw quarkus:dev
```

#### Show swagger
When quarkus is started you can access to endpoints via swagger with:
```shell script
http://localhost:5001/swagger-ui/
```

#### Enter in H2 database
When quarkus is started you can access to the database:
```shell script
http://localhost:5001/h2
```
