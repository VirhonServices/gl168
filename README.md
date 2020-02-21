# Virhon General Ledger

![picture](http://i.piccy.info/i9/4000ad033c7371a6cc1a72d2fe9c194c/1582148037/8830/1363454/1111.png)

## Table of Contents

- [Concept](#concept)
- [General Information](#general-information)
- [API Reference](#api-reference)
    * [Open a new account](#open-a-new-account-post)
    * [Get the information](#get-the-information-get)
    * [Get account's balance at the posting moment](#get-accounts-balance-at-the-posting-moment-get)
    * [Get account's open and closed balances on a particular reporting period](#get-accounts-open-and-closed-balances-on-a-particular-reporting-period-get)
    * [Get account's transfers by posting period](#get-accounts-transfers-by-posting-period-get)
    * [Get account's transfers list by reporting period](#get-accounts-transfers-list-by-reporting-period-get)

## CONCEPT

## GENERAL INFORMATION

### Types of account
 |Parameter| Description|
 |---------|------------|
 |**ACTIVE**| The account balance **able to be an active only** (an attemption to turn the account into passive balance throws an exception)
 |**PASSIVE**| The account balance **able to be an passive only** (an attemption to turn the account into active balance throws an exception)
 |**ACTIVEPASSIVE**| The account balance **able to be active either passive value** (the both values are valid)

### Dates and moments of the balance and periods
Some functions require date or period to be defined. There are two types of 
date values regarding the transfers:
* posting
* reporting

#### POSTING moment
Posting date shows the moment when the transfer was posted. The value 
includes year, month, day, hour, minutes, seconds, milliseconds and 
server's timezone.

#### REPORTING date 
Reporting date says about the financial day the action was referred to. 
It is possible posting moment and reporting date to be different, but
the both values are strictly increasing.

### Errors
In case if the system is not able to process your request correctly, you will get 
an appropriate http status code with error details provided by a response body. The body has
the following structure:
````json
{
  "code": 150,
  "message": "Currency XBTH not supported"
}
````
where:

|Parameter|Description|Type|Mandatory|
|---------|-----------|----|---------|
|code|Numeric code of error situation|Int|Yes
|message|A text providing details of the situation having initiated by the error|String|Yes

The following table shows all the possible error situations:

|Code|Description
|----|------------
|100|Unable to add the post older than the page (by posting moment)
|110|Unable to add the post older than the page (by reporting date)
|120|The negative amount can't be reserved
|130|The negative amount can't be transferred
|140|Invalid type of account
|150|Currency not supported
|200|Red balance on the account
|210|The balance of the account is not enough to make a reservation
|300|The account doesn't exist
|310|The account didn't exist on the date
|320|The account can't be operated
|400|Can't operate read-only account

## API REFERENCE

### Accounts
````
/v1/gl/{currencyCode}/accounts
````
 |Parameter| Description|
 |---------|------------|
 |currencyCode| The code of currency according to ISO 4217 alpha-3

#### Open a new account [POST]

##### Request
```json
{
  "accType": "PASSIVE",
  "accNumber": "26003000078365",
  "iban": "UA5630529926003000078365"
}
```
##### Response 201
```json
{
  "uuid": "9f9ec79d-4e98-410f-b180-cecce31d9680",
  "accType": "PASSIVE",
  "accNumber": "26003000078365",
  "iban": "UA5630529926003000078365",
  "currency": "UAH",
  "openedAt": "2020-02-16T01:26:51.556+02:00[Europe/Kiev]"
}
```

### Getting account's information
 ````
 /v1/gl/{currencyCode}/accounts/{accountUuid}
 ````
 |Parameter| Description|
 |---------|------------|
 |currencyCode| The code of currency according to ISO 4217 alpha-3
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
  "repBalance": 1267.89,
  "balType": "CREDIT",
  "available": 1267.89,
  "openedAt": "2020-02-16T01:26:51.556+02:00[Europe/Kiev]",
  "closedAt": null
}
```

### Getting account's balance at the particular posting moment
````
/v1/gl/{currencyCode}/accounts/{accountUuid}/posting/balance
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |accountUuid| A uuid of the account


#### Get account's balance at the posting moment [GET]

##### Request
You need to pass separated DateTime value in server's timezone
````json
{
    "year": 2020,
    "month": 2,
    "day": 16,
    "hour": 1,
    "minutes": 26,
    "seconds": 51,
    "miliseconds": 556
}
````

##### Response 200
````json
{
   "uuid": "9f9ec79d-4e98-410f-b180-cecce31d9680",
   "at": "2020-02-16T01:26:51.556+02:00[Europe/Kiev]",
   "accType": "PASSIVE",
   "accNumber": "26003000078365",
   "iban": "UA5630529926003000078365",
   "currency": "UAH",
   "balance": 1267.89,
   "repBalance": 1267.89,
   "balType": "CREDIT"
}
````

### Getting account's reporting balance for the period
````
/v1/gl/{currencyCode}/accounts/{accountUuid}/reporting/balance
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |accountUuid| A uuid of the account

#### Get account's open and closed balances on a particular reporting period [GET]

##### Request
You need to pass separated parts of LocalDate values
````json
 {
   "beginOn": {
       "year": 2020,
       "month": 2,
       "day": 14
   },
   "finishOn": {
       "year": 2020,
       "month": 2,
       "day": 16
   }
 }
 ````

##### Response 200
````json
{
  "accType": "PASSIVE",
  "accNumber": "26003000078365",
  "iban": "UA5630529926003000078365",
  "open": {
       "balance": 1267.89,
       "repBalance": 1267.89,
       "balType": "CREDIT"
   },
   "closed": {
       "balance": 1267.89,
       "repBalance": 1267.89,
       "balType": "CREDIT"   
   }
}
````
### Getting account's transfers list (by posting period)
````
/v1/gl/{currencyCode}/accounts/{accountUuid}/posted/transfers
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |accountUuid| A uuid of the account

#### Get account's transfers by posting period [GET]

##### Request
````json
{
  "beginAt": {
      "year": 2020,
      "month": 2,
      "day": 16,
      "hour": 1,
      "minutes": 26,
      "seconds": 51,
      "miliseconds": 556
  },
  "finishAt": {
      "year": 2020,
      "month": 2,
      "day": 18,
      "hour": 2,
      "minutes": 22,
      "seconds": 24,
      "miliseconds": 876
  }
}
````

##### Response 200
````json
{
    "transfers": [
        {
          "transferRef": "qw7663837jnn0094948-003",
          "postedAt": "2020-02-16T01:26:51.556+02:00[Europe/Kiev]",
          "reportedOn": "2020-02-16",
          "currency": "UAH",
          "amount": 100.00,
          "repAmount": 100.00,
          "description": "Purchasing goods in MEGAMART",
          "debit": {
              "accUuid": "de49a7a8-77de-42cd-b5f6-bbf1aa745623",
              "accNumber": "1001200038767",
              "iban": "UA893052991001200038767",
              "accType": "ACTIVE"
          },
          "credit": {
              "accUuid": "9f9ec79d-4e98-410f-b180-cecce31d9680",
              "accNumber": "26003000078365",
              "iban": "UA5630529926003000078365",
              "accType": "PASSIVE"
          }
        }
    ]
}
````

### Getting account's transfers list (by reporting period)
Get all the transfers of the account that was reported on the specified period
````
/v1/gl/{currencyCode}/accounts/{accountUuid}/reported/transfers
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |accountUuid| A uuid of the account

#### Get account's transfers list by reporting period [GET]

##### Request
 |Field| Description|Type|Mandatory|
 |---------|------------|----|----------
 |beginOn| The first financial day of the period|Object|Yes
 |finishOn| The last financial day of the period|Object|Yes

````json
{
  "beginOn": {
      "year": 2020,
      "month": 2,
      "day": 14
  },
  "finishOn": {
      "year": 2020,
      "month": 2,
      "day": 16
  }
}
````

##### Response 200
````json
{
    "transfers": [
        {
          "uuid": "be65733f-5479-4850-8d9f-9509b33fc5fc",
          "transferRef": "qw7663837jnn0094948-003",
          "postedAt": "2020-02-16T01:26:51.556+02:00[Europe/Kiev]",
          "reportedOn": "2020-02-16",
          "currency": "UAH",
          "transactionType": "CREDIT",
          "amount": 100.00,
          "repAmount": 100.00,
          "description": "Purchasing goods in MEGAMART",
          "corresponding": {
              "accUuid": "de49a7a8-77de-42cd-b5f6-bbf1aa745623",
              "accNumber": "1001200038767",
              "iban": "UA893052991001200038767",
              "accType": "ACTIVE"
          }
        }
    ]
}
````