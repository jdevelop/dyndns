DynDNS for Amazon Route53 [![Build Status](https://api.travis-ci.org/jdevelop/dyndns)] (http://travis-ci.org/jdevelop/dyndns)
=========

DynDNS daemon for Amazon Route 53

Building
---------

    mvn clean install

Running
---------

    java -jar dyndns-1.0-SNAPSHOT-jar-with-dependencies.jar

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