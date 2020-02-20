# Virhon General Ledger 24/7

![picture](http://i.piccy.info/i9/4000ad033c7371a6cc1a72d2fe9c194c/1582148037/8830/1363454/1111.png)

## Table of Contents

- [General Information](#general-information)
- [API Reference](#api-reference)
    * [Open a new account](#open-a-new-account-post)
    * [Get the information](#get-the-information-get)
    * [Get account's balance at the posting moment](#getting-accounts-balance-at-the-particular-posting-moment)
    * [Get account's starting balance on a particular reporting day](#get-accounts-starting-balance-on-a-particular-reporting-day-get)
    * [Get account's finishing balance at the particular reporting day](#get-accounts-finishing-balance-at-the-particular-reporting-day-get)
    * [Get account's transfers by posting period](#get-accounts-transfers-by-posting-period-get)
    * [Get account's transfers list by reporting period](#get-accounts-transfers-list-by-reporting-period-get)

## General Information

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

## API reference

### Accounts
````
/v1/gl/accounts
````
#### Open a new account [POST]

##### Request
```json
{
  "accType": "PASSIVE",
  "accNumber": "26003000078365",
  "iban": "UA5630529926003000078365",
  "currency": "UAH"
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
  "repBalance": 1267.89,
  "balType": "CREDIT",
  "available": 1267.89,
  "openedAt": "2020-02-16T01:26:51.556+02:00[Europe/Kiev]",
  "closedAt": null
}
```

### Getting account's balance at the particular posting moment
````
/v1/gl/accounts/{accountUuid}/posting/balance
````
 |Parameter| Description|Type|Mandatory|
 |---------|------------|----|----------
 |accountUuid| A uuid of the account|UUID|Yes

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

### Getting account's starting balance
````
/v1/gl/accounts/{accountUuid}/reporting/balance/begin
````
 |Parameter| Description|Type|Mandatory|
 |---------|------------|----|----------
 |accountUuid| A uuid of the account|UUID|Yes

#### Get account's starting balance on a particular reporting day [GET]

##### Request
You need to pass separated LocalDate value
````json
{
    "year": 2020,
    "month": 2,
    "day": 16
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

### Getting account's finishing balance
````
/v1/gl/accounts/{accountUuid}/reporting/balance/finish
````
 |Parameter| Description|Type|Mandatory|
 |---------|------------|----|----------
 |accountUuid| A uuid of the account|UUID|Yes

#### Get account's finishing balance at the particular reporting day [GET]

##### Request
You need to pass separated LocalDate value
````json
{
    "year": 2020,
    "month": 2,
    "day": 16
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

### Getting account's transfers list (by posting period)
````
/v1/gl/accounts/{accountUuid}/posted/transfers
````
 |Parameter| Description|Type|Mandatory|
 |---------|------------|----|----------
 |accountUuid| A uuid of the account|UUID|Yes

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
/v1/gl/accounts/{accountUuid}/reported/transfers
````
 |Parameter| Description|Type|Mandatory|
 |---------|------------|----|----------
 |accountUuid| A uuid of the account|UUID|Yes

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