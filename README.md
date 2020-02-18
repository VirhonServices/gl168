# Virhon General Ledger 24/7

## API

### Getting account's information
 ````
 /v1/gl/accounts/{accountUuid}
 ````
 |Parameter| Description|
 |---------|------------|
 |accountUuid| A uuid of the account
 
#### Get the information [GET]

##### Response 200
```json
{
  "uuid": "9f9ec79d-4e98-410f-b180-cecce31d9680",
  "accType": "PASSIVE",
  "accNumber": "26003000078365",
  "iban": "UA5630529926003000078365",
  "currency": "UAH",
  "balance": 1267.89,
  "reportBalance": 1267.89,
  "balType": "CREDIT",
  "available": 1267.89
}
```
