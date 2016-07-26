

# Remove security headers

Add the following line (may need some tweeking based on response being generated)  `<Location>` section of 
Apache/OHS configuration to allow "secured" sites to work on HTTP or for debugging.

```
Header edit* Set-Cookie "(JSESSIONID=.*)(; secure; HttpOnly)" "$1"
```
