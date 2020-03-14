# Virhon General Ledger

## Table of Contents

- [Overview](#overview)
- [API Reference](#api-reference)
    * [Open a new account](#open-a-new-account-post)
    * [Get the information](#get-the-information-get)
    * [Transfer funds](#transfer-funds-post)
    * [Get the transfer's information](#get-the-transfers-information-get)
    * [Reserve funds](#reserve-funds-post)
    * [Post the reservation](#post-the-reservation-put)
    * [Get the data for reporting period](#get-the-data-for-reporting-period-post)
    * [Get the data for posting period](#get-the-data-for-posting-period-post)

## OVERVIEW
The service provides General Ledger functionality includes accounts, transfers and reservations.  

### Authentication
The service is able to be communicated by authorized TPP's systems only. So, each of request should 
be signed according to the following schema:
````
access-token = sha1(md5(requestDateTime.requestData.clientDigest.salt))
````
dots mean a concatenation operation

where
* requestDateTime - the moment of request provided by **Date** header in format **DateTimeFormatter.ISO_ZONED_DATE_TIME**
* requestData - the content of request's body in string Json format
* salt - a mixina previded during the boarding process
* clientDigest - the digest stored for the client and calculated as:
````
clientDigest = sha1(md5(apiKey.clientuuid.salt))
````
where
* apiKey - the key provided during the boarding process
* clientUuid - ID of the client provided during the boarding process
* salt - a mixina previded during the boarding process

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
````json
{
  "clientCustomerId": "ff637646-7673/WWD",
  "accType": "PASSIVE",
  "accNumber": "26003000078365",
  "iban": "UA5630529926003000078365"
}
````
##### Response 201
````json
{
  "clientCustomerId": "ff637646-7673/WWD",
  "transferUuid": "9f9ec79d-4e98-410f-b180-cecce31d9680",
  "accType": "PASSIVE",
  "accNumber": "26003000078365",
  "iban": "UA5630529926003000078365",
  "currency": "UAH",
  "openedAt": "2020-02-16T01:26:51.556+02:00[Europe/Kiev]"
}
````

### Getting account's information
 ````
 /v1/gl/{currencyCode}/accounts/{accountUuid}
 ````
 |Parameter| Description|
 |---------|------------|
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |accountUuid| A transferUuid of the account
 
#### Get the information [GET]

##### Response 200
````json
{
  "uuid": "9f9ec79d-4e98-410f-b180-cecce31d9680",
  "clientCustomerId": "ff637646-7673/WWD",
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
````
### Getting account's balance at the particular posting moment
````
/v1/gl/{currencyCode}/accounts/{accountUuid}/posting/balance
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |accountUuid| A transferUuid of the account

#### Get account's balance at the posting moment [POST]

##### Request
You need to pass separated DateTime value in server's timezone
````json
{
    "year": 2020,
    "month": 2,
    "day": 16,
    "hour": 1,
    "minute": 26,
    "second": 51,
    "nanoOfSecond": 556
}
````

##### Response 200
````json
{
   "uuid": "9f9ec79d-4e98-410f-b180-cecce31d9680",
   "clientCustomerId": "ff637646-7673/WWD",
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
### Making a transfer
````
/v1/gl/{currencyCode}/accounts/{debitAccountUuid}/transfers
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |debitAccountUuid| Uuid of the account being debited
 
#### Transfer funds [POST]

##### Request
````json
{
  "transferRef": "qw7663837jnn0094948-003",
  "clientCustomerId": "ff637646-7673/WWD",  
  "creditAccountUuid": "d9984b8e-9a7a-401c-840a-2531f003c9dc",
  "amount": 2481.00,
  "repAmount": 100.00,
  "reportedOn": {
      "year": 2020,
      "month": 2,
      "day": 21
  },
  "description": "ONLINE TAXI bill 1228/UKR-11 payment"
}
````
##### Response 201
````json
{
  "transferUuid": "be65733f-5479-4850-8d9f-9509b33fc5fc",
  "transferRef": "qw7663837jnn0094948-003",
  "clientCustomerId": "ff637646-7673/WWD",  
  "postedAt": "2020-02-21T01:26:51.556+02:00[Europe/Kiev]",
  "reportedOn": "2020-02-21",
  "amount": 2481.00,
  "repAmount": 100.00,
  "description": "Purchasing goods in MEGAMART",
  "debit": {
      "accUuid": "de49a7a8-77de-42cd-b5f6-bbf1aa745623",
      "accNumber": "1001200038767",
      "iban": "UA893052991001200038767",
      "accType": "ACTIVE"
  },
  "credit": {
      "accUuid": "5e19fcbb-3fc5-497e-bcb9-09cf5e157fc6",
      "accNumber": "2602100009203",
      "iban": "UA673052992602100009203",
      "accType": "PASSIVE"
  }  
}
````
### Getting transfer's information
````
/v1/gl/{currencyCode}/transfers/{transferUuid}
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |transferUuid| A uuid of the transfer being gotten

#### Get the transfer's information [GET]

##### Response
````json
{
  "transferUuid": "be65733f-5479-4850-8d9f-9509b33fc5fc",
  "transferRef": "qw7663837jnn0094948-003",
  "clientCustomerId": "ff637646-7673/WWD",  
  "postedAt": "2020-02-21T01:26:51.556+02:00[Europe/Kiev]",
  "reportedOn": "2020-02-21",
  "amount": 2481.00,
  "repAmount": 100.00,
  "description": "Purchasing goods in MEGAMART",
  "debit": {
      "accUuid": "de49a7a8-77de-42cd-b5f6-bbf1aa745623",
      "accNumber": "1001200038767",
      "iban": "UA893052991001200038767",
      "accType": "ACTIVE"
  },
  "credit": {
      "accUuid": "5e19fcbb-3fc5-497e-bcb9-09cf5e157fc6",
      "accNumber": "2602100009203",
      "iban": "UA673052992602100009203",
      "accType": "PASSIVE"
  }  
}
````

### Reserving funds for future transfer
````
/v1/gl/{currencyCode}/accounts/{debitAccountUuid}/reservations
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |debitAccountUuid| A transferUuid of debit account
#### Reserve funds [POST]
##### Request
````json
{
  "transferRef": "qw7663837jnn0094948-003",
  "clientCustomerId": "ff637646-7673/WWD",  
  "creditAccountUuid": "d9984b8e-9a7a-401c-840a-2531f003c9dc",
  "amount": 100.00,
  "description": "ONLINE TAXI bill 1228/UKR-11 payment"
}
````
##### Response 201
````json
{
  "uuid": "48db13a1-b58d-4f42-89f1-c58f30fb6297",
  "transferRef": "qw7663837jnn0094948-003",
  "clientCustomerId": "ff637646-7673/WWD",  
  "debitAccountUuid": "f1fb1ca9-3e3e-4eb1-80cf-e2a42a82ebff",
  "creditAccountUuid": "d9984b8e-9a7a-401c-840a-2531f003c9dc",
  "amount": 100.00,
  "description": "ONLINE TAXI bill 1228/UKR-11 payment",
  "expireAt": "2020-02-21T01:26:51.556+02:00[Europe/Kiev]"
}
````
### Posting reserved transfer
````
/v1/gl/{currencyCode}/reservations/{reservationUuid}
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |reservationUuid| Uuid of the reservation need to be posted
#### Post the reservation [PUT]
##### Request
````json
{
  "repAmount": 2481.00,
  "reportedOn": {
      "year": 2020,
      "month": 2,
      "day": 20
  }
}
````
##### Response 201
````json
{
  "transferUuid": "be65733f-5479-4850-8d9f-9509b33fc5fc",
  "transferRef": "qw7663837jnn0094948-003",
  "clientCustomerId": "ff637646-7673/WWD",  
  "postedAt": "2020-02-21T01:26:51.556+02:00[Europe/Kiev]",
  "reportedOn": "2020-02-20",
  "amount": 100.00,
  "repAmount": 2481.00,
  "description": "ONLINE TAXI bill 1228/UKR-11 payment",
  "debit": {
      "accUuid": "f1fb1ca9-3e3e-4eb1-80cf-e2a42a82ebff",
      "accNumber": "1001200038767",
      "iban": "UA893052991001200038767",
      "accType": "ACTIVE"
  },
  "credit": {
      "accUuid": "d9984b8e-9a7a-401c-840a-2531f003c9dc",
      "accNumber": "2602100009203",
      "iban": "UA673052992602100009203",
      "accType": "PASSIVE"
  }  
} 
````
### Getting the reporting period data
````
/v1/gl/{currencyCode}/accounts/{accountUuid}/reporting
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |accountUuid| A transferUuid of the account

#### Get the data for reporting period [POST]

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
    "clientCustomerId": "ff637646-7673/WWD",  
    "accType": "PASSIVE",
    "accNumber": "260350009",
    "iban": "UA55305299260350009",
    "closed": {
        "balType": "DEBIT",
        "balance": 2859.36,
        "repBalance": 69053.544
    },
    "open": {
        "balType": "DEBIT",
        "balance": 865.2,
        "repBalance": 20894.58
    },
    "transfers": [
        {
            "transferUuid": "e711609d-bb88-482f-ba0f-1dcef2695cd3",
            "transferRef": "AUTO-426",
            "clientCustomerId": "ff637646-7673/WWD",  
            "amountType": "DEBIT",
            "amount": 4.26,
            "repAmount": 102.879,
            "description": "Autogenerated transfer",
            "postedAt": "2020-02-26T18:40:06.107+02:00[Europe/Kiev]",
            "reportedOn": "2020-02-27",
            "creditPageUuid": "0b175013-c991-425b-99ca-c1b035f00284",
            "debitPageUuid": "caf86739-e154-43a7-b2b1-a0b9cf09f4b5"
        }
    ]
}
````
### Getting the posting period data
````
/v1/gl/{currencyCode}/accounts/{accountUuid}/posting
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |accountUuid| A transferUuid of the account

#### Get the data for posting period [POST]

##### Request
You need to pass separated parts of LocalDate values
````json
 {
   "startedAt": {
       "year": 2020,
       "month": 2,
       "day": 14,
       "hour": 0,
       "minute": 0,
       "second": 0,
       "nano": 0
   },
   "finishedAt": {
       "year": 2020,
       "month": 2,
       "day": 16,
       "hour": 23,
       "minute": 59,
       "second": 59,
       "nano": 9999
   }
 }
 ````
##### Response 200
````json
{
    "clientCustomerId": "ff637646-7673/WWD",  
    "accType": "PASSIVE",
    "accNumber": "260350009",
    "iban": "UA55305299260350009",
    "closed": {
        "balType": "DEBIT",
        "balance": 2859.36,
        "repBalance": 69053.544
    },
    "open": {
        "balType": "DEBIT",
        "balance": 865.2,
        "repBalance": 20894.58
    },
    "transfers": [
        {
            "transferUuid": "e711609d-bb88-482f-ba0f-1dcef2695cd3",
            "transferRef": "AUTO-426",
            "clientCustomerId": "ff637646-7673/WWD",  
            "amountType": "DEBIT",
            "amount": 4.26,
            "repAmount": 102.879,
            "description": "Autogenerated transfer",
            "postedAt": "2020-02-26T18:40:06.107+02:00[Europe/Kiev]",
            "reportedOn": "2020-02-27",
            "creditPageUuid": "0b175013-c991-425b-99ca-c1b035f00284",
            "debitPageUuid": "caf86739-e154-43a7-b2b1-a0b9cf09f4b5"
        }
    ]
}
````
