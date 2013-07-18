DynDNS for Amazon Route53 [![Build Status](https://api.travis-ci.org/jdevelop/dyndns.png)] (http://travis-ci.org/jdevelop/dyndns)
=========

DynDNS daemon for Amazon Route 53

Building
---------

    mvn clean install

Running
---------

    java -jar dyndns-1.0-SNAPSHOT-jar-with-dependencies.jar

Options
---------
    -v    : show verbose output
    -v -v : show everything including the protocol dumps

Example of .dyndns.rc file
---------

    route53 {
        resolver {
            uri = "http://domain.com/get-ip"
        }
        aws {
            accessKey = 1234567890
            secretKey = 0987654321
        }
        zone {
            id = YHT12345
            ttl = 1800
            subdomain = your.domain.here
            updateInterval = 1800
        }
    }
