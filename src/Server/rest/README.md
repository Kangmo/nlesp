REST service server of NLESP
=====

# Description
NLESP for SNS consists of 3 components: NLESP Server, REST Server, and Android application(client).

* NLESP Server <--(Thrift)--> REST Server  <--(HTTP)--> Client (Android application)

REST Server hides NLESP Server's Thrift API and provides REST API for client applications.


# Environments
REST server requires a Servlet 3.0 compatible container.

* It tested on Tomcat 7 only. (Tomcat 6 or below does not support Servlet 3.0)

# Build and run
## Build
On this GitHub, it includes Eclipse project files. Check in this folder, then import it to your Eclipse workspace.

* Make sure your Eclipse has WTP plugin. ('Eclipse IDE for Java EE Developers' is recommended.)

## Run
Follow below tutorial to install and configure Tomcat 7 and Eclipse.

  http://www.coreservlets.com/Apache-Tomcat-Tutorial/tomcat-7-with-eclipse.html

It's a very simple Servlet project. There's nothing special you can find.

# Test
## Unit test
Unit tests are under implementation.

## Stress test
Stress test runs multiple clients, which performs basic scenario per each, in parallel.

[Warning]
It's a very immature test. I made it in a day by a very urgent request for demonstration, never refined at all.
For examples, it creates 10,000 new accounts permanently every time you run the setup. It assumes many things which are hard-coded.
You must carefully review the code first before run this.

To run this test,

0. Modify value of the 'VD_ROOT_URL_PRODUCTION' in 'StressTest.java', then recompile.

The value should be proper REST service to test. 

1. Run StressTestSetup
  # java StressTestSetup

This setup program creates accounts, makes relationship among them, then post some messages to simulate more realistic test.

2. Run StressTest
  # java StressTest [Num_Of_Clients]

Argument means the number of clients to run in parallel. Each client has own user account starting from 1.
