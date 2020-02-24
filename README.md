# Virhon General Ledger

![picture](http://i.piccy.info/i9/4000ad033c7371a6cc1a72d2fe9c194c/1582148037/8830/1363454/1111.png)

## Table of Contents

- [Overview](#overview)
- [API Reference](#api-reference)
    * [Open a new account](#open-a-new-account-post)
    * [Get the information](#get-the-information-get)
    * [Get account's balance at the posting moment](#get-accounts-balance-at-the-posting-moment-get)
    * [Get account's open and closed balances on a particular reporting period](#get-accounts-open-and-closed-balances-on-a-particular-reporting-period-get)
    * [Transfer funds](#transfer-funds-post)
    * [Reserve funds](#reserve-funds-post)
    * [Post the reservation](#post-the-reservation-post)
    * [Get the transfer's information](#get-the-transfers-information-get)
    * [Get account's transfers by posting period](#get-accounts-transfers-by-posting-period-get)
    * [Get account's transfers list by reporting period](#get-accounts-transfers-list-by-reporting-period-get)

## OVERVIEW

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

All the possible timezones provided below:

|Code|Zone
|----|-----
|EST|-05:00
|HST|-10:00
|MST|-07:00
|ACT|Australia/Darwin
|AET|Australia/Sydney
|AGT|America/Argentina/Buenos_Aires
|ART|Africa/Cairo
|AST|America/Anchorage
|BET|America/Sao_Paulo
|BST|Asia/Dhaka
|CAT|Africa/Harare
|CNT|America/St_Johns
|CST|America/Chicago
|CTT|Asia/Shanghai
|EAT|Africa/Addis_Ababa
|ECT|Europe/Paris
|IET|America/Indiana/Indianapolis
|IST|Asia/Kolkata
|JST|Asia/Tokyo
|MIT|Pacific/Apia
|NET|Asia/Yerevan
|NST|Pacific/Auckland
|PLT|Asia/Karachi
|PNT|America/Phoenix
|PRT|America/Puerto_Rico
|PST|America/Los_Angeles
|SST|Pacific/Guadalcanal
|VST|Asia/Ho_Chi_Minh

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
  "accType": "PASSIVE",
  "accNumber": "26003000078365",
  "iban": "UA5630529926003000078365"
}
````
##### Response 201
````json
{
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
  "transferUuid": "9f9ec79d-4e98-410f-b180-cecce31d9680",
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
    "nanoOfSecond": 556,
    "zoneId": "Europe/Kiev"
}
````

##### Response 200
````json
{
   "transferUuid": "9f9ec79d-4e98-410f-b180-cecce31d9680",
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
 |accountUuid| A transferUuid of the account

#### Get account's open and closed balances on a particular reporting period [POST]

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
### Making a transfer
````
/v1/gl/{currencyCode}/accounts/{debitAccountUuid}/transfers
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |debitAccountUuid| A transferUuid of debit account
#### Transfer funds [POST]

##### Request
````json
{
  "transferRef": "qw7663837jnn0094948-003",
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
 |transferUuid| UUID of the transfer the information need to be gotten of
#### Get the transfer's information [GET]
##### Response 200
````json
{
  "transferUuid": "be65733f-5479-4850-8d9f-9509b33fc5fc",
  "transferRef": "qw7663837jnn0094948-003",
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
  "reportedOn": "2020-02-20"
}
````
##### Response 201
````json
{
  "transferUuid": "be65733f-5479-4850-8d9f-9509b33fc5fc",
  "transferRef": "qw7663837jnn0094948-003",
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
### Getting account's transfers list (by posting period)
````
/v1/gl/{currencyCode}/accounts/{accountUuid}/posted/transfers
````
 |Variable| Description
 |---------|------------
 |currencyCode| The code of currency according to ISO 4217 alpha-3
 |accountUuid| A transferUuid of the account

#### Get account's transfers by posting period [POST]

##### Request
````json
{
  "beginAt": {
      "year": 2020,
      "month": 2,
      "day": 16,
      "hour": 1,
      "minute": 26,
      "second": 51,
      "nanoOfSecond": 556,
      "zoneId": "Europe/Kiev"
  },
  "finishAt": {
      "year": 2020,
      "month": 2,
      "day": 18,
      "hour": 2,
      "minute": 22,
      "second": 24,
      "nanoOfSecond": 876,
      "zoneId": "Europe/Kiev"
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
          "transactionType": "CREDIT",
          "amount": 100.00,
          "repAmount": 100.00,
          "description": "Purchasing goods in MEGAMART",
          "correspondent": {
              "accUuid": "de49a7a8-77de-42cd-b5f6-bbf1aa745623",
              "accNumber": "1001200038767",
              "iban": "UA893052991001200038767",
              "accType": "ACTIVE"
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
 |accountUuid| A transferUuid of the account

#### Get account's transfers list by reporting period [POST]

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
          "transferUuid": "be65733f-5479-4850-8d9f-9509b33fc5fc",
          "transferRef": "qw7663837jnn0094948-003",
          "postedAt": "2020-02-16T01:26:51.556+02:00[Europe/Kiev]",
          "reportedOn": "2020-02-16",
          "transactionType": "CREDIT",
          "amount": 100.00,
          "repAmount": 100.00,
          "description": "Purchasing goods in MEGAMART",
          "correspondent": {
              "accUuid": "de49a7a8-77de-42cd-b5f6-bbf1aa745623",
              "accNumber": "1001200038767",
              "iban": "UA893052991001200038767",
              "accType": "ACTIVE"
          }
        }
    ]
}
````