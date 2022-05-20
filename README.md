# CountryResource
An extension to manage countries.

## Description
This extension adds a Resource object providing utility services for ISO countries.

## Services
- *getCountries*: returns the ISO countries in the selected language
  - input
    - language: the language, none to use the current user language - STRING (No default value)
    - sortByName: true to sort by name, false to sort by ISO code - BOOLEAN (No default value)
  - output: INFOTABLE (ds_Country, see below)
- *getCountriesInOwnLanguage*: returns the ISO countries with each country in its own language
  - input
    - sortByName: true to sort by name, false to sort by ISO code - BOOLEAN (No default value)
  - output: INFOTABLE (ds_Country, see below)

## DataShapes
- ds_Country
  - iso - STRING
  - iso3 - STRING
  - name - STRING
  - language - STRING

## Donate
If you would like to support the development of this and/or other extensions, consider making a [donation](https://www.paypal.com/donate/?business=HCDX9BAEYDF4C&no_recurring=0&currency_code=EUR).
