Localization Validator
======================

Script for searching defects in localization files with JSON suffix.
Program will find JSON files in specified directory with DFS.
You can select between which directory you want to compare and find all defects.

This version works specifically with CS.json and EN.json files.
Program mostly compares names of messages, only difference is in singular and plural description.
When the plural is recognized, it will check if CS and EN are in match cause it has different properties.
CS messages are specified by number behind underscore and EN messages with word plural behind underscore.
If it has plural amount, it should have these forms:

**CS**
 * example_0
 * example_1
 * example_2
 * example_5

**EN**
 * example
 * example_plural

Comparing
---------

| CS        | EN             |
|-----------|----------------|
| example_0 | -              |
| example_1 | example        |
| example_2 | example_plural |
| example_5 | example_plural |

Table: How program compares plural messages

Messages that ends with _0 are just checked if it's there when it comes to plural.

Other cases are just compared and if there is a mismatch, program will search for closest match.
If the match is not found it will continue comapring next pair.
Otherwise it will add words between mismatch and newly found match to defects and continue comparing from newly found point.
