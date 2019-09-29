# bank

This project was build using maven, so you can use the regular mvn commands.

Use 'mvn test' to run the JUnit tests.
Use 'mvn install' to run the set of tests and create the jar file.

How to run the application
To run the application you should use the generated jar file from the target folder.

Example:
java -jar target\bank-1.0.0.jar


Available Endpoints

Create transaction (POST) (localhost:8080/transactions):

This endpoint will receive the transaction information and store it into the system.
It is IMPORTANT to note that a transaction that leaves the total account balance bellow 0 is not allowed.

Payload:
{
"reference":"12345A",
"account_iban":"ES9820385778983000760236",
"date":"2019-07-16T16:55:42.000Z",
"amount":193.38,
"fee":3.18,
"description":"Restaurant payment"
}

reference (optional): The transaction unique reference number in our system. If not present, the system will generate one.
account_iban (mandatory): The IBAN number of the account where the transaction has happened.
date (optional): Date when the transaction took place
amount (mandatory): If positive the transaction is a credit (add money) to the account. If negative it is a debit (deduct money from the account)
fee (optional): Fee that will be deducted from the amount, regardless on the amount being positive or negative.
description (optional): The description of the transaction



List transactions (GET) (localhost:8080/transactions)

This endpoint list all the transactions that are stored in the system.



List transactions (GET) (localhost:8080/transactions/{iban})

This endpoint searches for transactions filtering it by the account iban, and sorting it by amount (ascending/descending) if it is required.

How to use the sorting property: add a new header parameter with key: "sort-type" and value "ASC" or "DESC", if the parameter "sort-type" is not provided, the transactions will be retrieved without sorting them.




Transaction status (GET) (localhost:8080/transactionStatus)

This endpoint, based on the payload and some business rules, will return the status and additional information for a specific transaction.

Payload:
{
"reference":"12345A",
"channel":"CLIENT"
}

reference (mandatory): The transaction reference number
channel (optional): The type of the channel that is asking for the status. It can be any of these values: CLIENT, ATM, INTERNAL

Response example:
{
"reference":"12345A",
"status":"PENDING",
"amount":193.38,
"fee":3.18
}

reference: The transaction reference number
status: The status of the transaction. It can be any of these values: PENDING, SETTLED, FUTURE, INVALID
amount: the amount of the transaction
fee: The fee applied to the transaction
