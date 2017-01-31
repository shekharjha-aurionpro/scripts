
# Set a global header

In the chrome console run the following method with correct request header and value. After that all the calls through XHR will contain the corresponding header.

```
(function() {
    var send = XMLHttpRequest.prototype.send;    XMLHttpRequest.prototype.send = function(data) {
        this.setRequestHeader('TESTING', 'VALUE');
        return send.apply(this, arguments);
    };
}());
```

# Run a function at regular interval.

In the chrome console run the following method with correct URL. Once 
```
var urlToInvokeAtRegularLevel = "http://www.google.com";
var timeIntervalBetweenEachCall = 5000;
var stopReload = false;
var downloadFunction = function() {
  if (!stopReload) {
    $.get(urlToInvokeAtRegularLevel)
    .done(
      function(data, textStatus, jqXHR) {
        console.log(Date() + " Downloaded details successfully as " + data)
        setTimeout(downloadFunction, timeIntervalBetweenEachCall);
      })
    .fail(
      function(jqXHR, textStatus, errorThrown){ 
        console.log(Date() + " Download Failed");
        setTimeout(downloadFunction, timeIntervalBetweenEachCall);
      });
  } else {
    console.log("Stopping url load");
  }
}
downloadFunction();
```
If you want to stop the execution, run the following in chrome console.
```
stopReload = true;
```
This is typically used to monitor a particular URL to test system failover or loadbalancing configurations that needs login cookies.
