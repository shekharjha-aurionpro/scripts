
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
