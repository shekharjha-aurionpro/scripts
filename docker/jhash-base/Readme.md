
This is the base image that gets the OEL (Oracle Enterprise Linux) 7 image ready for other installations. It

1. Updates and applies all the patches and installs basic tools like tar
2. Downloads and install JDK in /opt/. It also exports JAVA_HOME=/opt/java environment variable 
3. Configure a new user "app" 
4. Exports volume /var/log/app & /opt/app
5. Set work dir to /opt/app and 
6. Set user id to app

> Note: Step 6 above is defined on-build to ensure that extending images can not get root access.

# Tag

> latest - OEL (Oracle Enterprise Linux) 7.0, Oracle JDK (1.8.0_60)

# Build 
Build the image using command
```
docker build --rm=true --force-rm=true --pull=true -t jhash-base:latest .
```
# Run
Run a new docker image using command
```
docker run -i -t --rm jhash-base bash
```
# Additional details

* Base Image Size (OEL 7.0) : 249M
* Final Size : 782M (178M from yum update + 353M from JDK)
