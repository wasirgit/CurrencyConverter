# Currency Convert
##Project Description

# Project Stack
-  Architecture Pattern - MVVM- Clean Architecture
- Kotlin
- Kotlin Flow
- View Binding
- Room Database
- Retrofit
- OkkHttp Client
- Dagger-Hilt Dependency Injection
- Navigation Component
- Junit
- turbine
- espresso
- google.truth
- Android preference

## Features
There are two screens in app.
- Currency conversion screen
  -- From this screen user can convert one currency to another
- Setting sreen
  -- From this screen user can update app settings and different logics- This screen was not in the requirement though I have added this screen for make ease to update the logic of different feature.

## Project initial configuration
I have used sqlite database where I have created two table initially which will be load during first time app installation.
Following table will be created during app installation for first time
- account
  --CREATE TABLE "account" ( "currencyCode" TEXT NOT NULL, "balance" REAL NOT NULL, PRIMARY KEY("currencyCode") )
  **account table column description**
    - currencyCode - will contain  user currency
    - balance -- will contain initial balance which is **1000.00**
- config
  CREATE TABLE "config" ( "id" INTEGER NOT NULL, "total_free_conversion" INTEGER NOT NULL, "total_convert" INTEGER NOT NULL, "max_free_amount" REAL NOT NULL, "every_nth_conversion_free" INTEGER NOT NULL, "commission" REAL NOT NULL, "syncTime" INTEGER NOT NULL, PRIMARY KEY("id") )

  **config table column description**
    - total_free_conversion *How much convert can user have for free of charge. Default value is 5.*
    - total_convert - *count of total conversion user has till now*
    - max_free_amount - *How much amount user can convert at a time for free of charge. No value by default. You can update it from settings screen*
    - every_nth_conversion_free - *special condition where user can convert for free of charge. For instance, if this field value 10, then it means user will get free convertion for every 10 conversion. No value set by default. You can from setting screen*
    - commission  - *How much commission will be charge. Default value is 0.7. You can update it from settings screen.*
    - syncTime - *How frequently API will be called. Default value is 5 seconds. You can update it from settings screen*


# Project Code structure
I have used MVVM- Clean architecture pattern. I have included project diagram in **project-preview** folder in root directory of the project. 



 
 
 
 
 
 
 
 
 
 
 

 