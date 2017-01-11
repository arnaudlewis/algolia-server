# Algolia monitoring application

* The [source code](https://github.com/arnaudlewis/algolia-server) is on Github.
* The [Changelog](https://github.com/arnaudlewis/algolia-server/releases) is on Github's releases tab.

------------------------------------


[Installation](#installation)<br />
[Run](#run)<br />

===================================================

## Installation

Please install the following tools:
* [Mongo DB](https://www.mongodb.com/download-center?jmp=nav#community)
* [NodeJS](https://nodejs.org/en/)

For Mac users, both are available on brew:

```
$ brew install mongodb
```
```
$ brew install node
```

## Run

Go to the root directory and follow these steps to run your project:

**Start MongoDB**
```
$ mongod
```

**Fill the database or seed data from another app**

If you wanna fill all the data directly just use the mongorestore command to fill your database
```
$ mongorestore --collection probeevents --db algolia ./ext/mongo/algolia/probeevents.bson
```
If you prefer to seed data from another server, just use the following app:

[SDN Probe report simulator](https://github.com/arnaudlewis/sdn-probes-report-simulator)<br />
Start this application and then continue with the following instructions


**Start SBT**
Start this command in a new terminal
```
$ ./bin/activator
[algolia-server] $ run
```

**Install node dependencies and build front**
Start this command in another new terminal, it'll be in watch mode
```
$ npm install
$ npm start
```

Everything should be up and running!
Now, try it in your browser: http://localhost:9000/
