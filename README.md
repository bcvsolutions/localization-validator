Localization Validator
======================

Script for searching defects in localization files with JSON suffix.
Program will find JSON files in specified directory with DFS.
You can choose between 'files you want to compare' and 'find all defects'.

This version works specifically with CS.json and EN.json files.
Program mostly compares names of messages, only difference is in singular and plural description.
When the plural is recognized, it will check if CS and EN are in match despite different properties.
CS messages are specified by number behind underscore and EN messages ends with word plural behind the underscore.
If it has plural amount, it should have these forms:

**CS**
 * example_0 (optional)
 * example_1
 * example_2
 * example_5

**EN**
 * example_0 (optional)
 * example
 * example_plural

Comparing
---------
*Table: how program compares plural messages*

| CS        | EN             |
|-----------|----------------|
| example_0 | example_0      |
| example_1 | example        |
| example_2 | example_plural |
| example_5 | example_plural |

Messages that ends with _0 are optional. If its found in CS or EN language, it's just checked for pair in other language.

Other cases are simply compared and if there is a mismatch, program will search for closest match.
If the match is not found it will continue comapring next pair.
Otherwise it will add words between mismatch and newly found match to defects and continue comparing from newly found point.
