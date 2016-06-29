
WLST script to add start argument to a server

```
domainHome='/opt/oim/domains/oim/';
server='oim_server1';

readDomain(domainHome);
cd('/Server/' + server);
create(server,'ServerStart');
cd ('ServerStart/' + server);
set ('Arguments', '-Dscheduler.disabled=true');
updateDomain();
closeDomain();
exit();
```
Script to read server start arguments.

```
readDomain('/opt/idm/oim/domains/oim/');
cd('/Server/oim_server1');
cd ('ServerStart/oim_server1');
get ('Arguments');
closeDomain();
exit();
```
