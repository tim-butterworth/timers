In two different tabs run the following commands (from the root directory)

```
$ SERVER_PORT=9000 SERVER_URL=http://localhost:8080 SERVER_INSTANCENAME=one ./gradlew bootRu
```

```
$ SERVER_URL=http://localhost:9000 SERVER_INSTANCENAME=two ./gradlew bootRu
```

This will start up two instances of the timer app that can reach one another over http

If everything starts correctly these urls should give timing responses:

[instance one](http://localhost:8080/timer)
[instance two](http://localhost:9000/timer)