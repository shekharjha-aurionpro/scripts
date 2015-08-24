
This is the base image that gets the OEL 7 image ready for other installations. It

1. Updates and applies all the patches and installs basic tools like tar
2. Downloads and install JDK in /opt/. It also exports JAVA_HOME=/opt/java environment variable 
3. Configure a new user "app" 
4. Exports volume /var/log/app & /opt/app
5. Set work dir to /opt/app and 
6. Set user id to app

> Note: Step 6 above is defined on-build to ensure that extending images can not get root access.

# Tag

latest - OEL 7.1, JDK 8u60

# Build 
Build the image using command
```
docker build --rm=true --force-rm=true --pull=true -t jhash-base:latest .
```
