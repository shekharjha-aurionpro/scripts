# Scripts

A collection of scripts for personal use.

## readDocker.js

Used to search Docker and dump Dockerfile from automated build repositories. I was using it to learn best practices 
associated with writing Dockerfile for storm. Run the script using 
```shell
node readDocker.js <search criteria>
```
## WebPing.java

Used to monitor connectivity between client and server (supports socket connection, Database connection). If you want to use Oracle Database connectivity monitor, please pass `<user id>` and ensure that Oracle JDBC driver is available in local directory (Download ojdbc7.jar from http://www.oracle.com/technetwork/database/features/jdbc/jdbc-drivers-12c-download-1958347.html).
Usage 
```bash
$ javac WebPing.java
$ java WebPing <server name> <port> <service name> <user id>
Database Password : <provide database password>
```
The system will print result of monitoring when you kill the program (Ctrl C should be enough).
```
Execution time (max/avg/median/min) : 15 / 4 / 3 / 2
Connection time (max/avg/median/min) : 14 / 4 / 3 / 2
SQL time (count/max/avg/median/min)    : 25 / 586 / 83 / 56 / 53
```
