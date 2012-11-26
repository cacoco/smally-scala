# smal.ly

##### Simple URL shortener in Scala using Finagle

### Running

To run:

    mvn scala:run
    
or optionally to run with debugging enabled (on port 5005 by default):

    mvn scala:run -Pdebug

To run unit tests:

    mvn test


### Using

To create a new shortened url:

    curl -i {host}:{port}/?url=www.to.shorten
    
e.g.,

    [~] ➔ curl -i "http://localhost:8080?url=http://www.twitter.com"
    HTTP/1.1 200 OK
    Content-Type: application/json; charset=utf-8
    Content-Length: 41

    {"smally-url" : "localhost:8080\/9h5k1"}
    
To use a shortened url:

    curl -i {host}/{shortened-url}
    
e.g.,

    [~] ➔ curl -i localhost:8080/9h5k1
    HTTP/1.1 302 Found
    Location: http://www.twitter.com
    Content-Length: 0
    
Or paste the shortened url into your browser.
