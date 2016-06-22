This process consists of transforming existing Cent OS 6 (on AWS) instance to OEL.

# Transformation from CentOS to OEL

1. Login to CentOS Linux 6 x86_64 HVM EBS `ssh -i "...key" centos@<ip address>`
2. Run transformation script
`sudo su -`<br>
`curl -O https://linsux.oracle.com/switch/centos2ol.sh`<br>
`sh centos2ol.sh`<br>
`yum update`<br>
`reboot`<br>

# Basic configuration

The base configuration was performed and AMI was created.

### Disable iptables
```
/etc/init.d/iptables stop
chkconfig iptables off
```
### SELinux disable
```
sed 's/enforcing/disabled/g' -i /etc/sysconfig/selinux
sed 's/enforcing/disabled/g' -i /etc/selinux/config
```
### reboot
### Set timezone
```
mv /etc/localtime /etc/localtime.bak
ln -s /usr/share/zoneinfo/America/Chicago /etc/localtime
```
### Add a new swap file of 2GB
```
mkdir /opt/swap
cd /opt/swap
dd if=/dev/zero of=/opt/swap/swapfile bs=1024 count=2MB
mkswap /opt/swap/swapfile
cp /etc/fstab /etc/fstab.bak
echo "/opt/swap/swapfile          swap            swap    defaults        0 0" >>/etc/fstab
```
### reboot
### Change host name by adding the following to /etc/sysconfig/network
```
HOSTNAME=oel67.dev.acmes
```
### Pre-install all the standard RDBMS pre-requisites
```
> yum install oracle-rdbms-server-11gR2-preinstall
Installing:
 oracle-rdbms-server-11gR2-preinstall                          x86_64                          1.0-12.el6                                          ol6_latest                               18 k
Installing for dependencies:
 bind-libs                                                     x86_64                          32:9.8.2-0.37.rc1.el6_7.4                           ol6_latest                              886 k
 bind-utils                                                    x86_64                          32:9.8.2-0.37.rc1.el6_7.4                           ol6_latest                              185 k
 cloog-ppl                                                     x86_64                          0.15.7-1.2.el6                                      ol6_latest                               93 k
 compat-libcap1                                                x86_64                          1.10-1                                              ol6_latest                               17 k
 compat-libstdc++-33                                           x86_64                          3.2.3-69.el6                                        ol6_latest                              183 k
 cpp                                                           x86_64                          4.4.7-16.el6                                        ol6_latest                              3.7 M
 gcc                                                           x86_64                          4.4.7-16.el6                                        ol6_latest                               10 M
 gcc-c++                                                       x86_64                          4.4.7-16.el6                                        ol6_latest                              4.7 M
 glibc-devel                                                   x86_64                          2.12-1.166.el6_7.3                                  ol6_latest                              985 k
 glibc-headers                                                 x86_64                          2.12-1.166.el6_7.3                                  ol6_latest                              614 k
 kernel-headers                                                x86_64                          2.6.32-573.7.1.el6                                  ol6_latest                              3.9 M
 kernel-uek                                                    x86_64                          2.6.39-400.264.5.el6uek                             ol6_UEK_latest                           28 M
 kernel-uek-firmware                                           noarch                          2.6.39-400.264.5.el6uek                             ol6_UEK_latest                          3.7 M
 keyutils                                                      x86_64                          1.4-5.el6                                           ol6_latest                               39 k
 ksh                                                           x86_64                          20120801-28.el6_7.3                                 ol6_latest                              760 k
 libICE                                                        x86_64                          1.0.6-1.el6                                         ol6_latest                               52 k
 libSM                                                         x86_64                          1.2.1-2.el6                                         ol6_latest                               36 k
 libX11                                                        x86_64                          1.6.0-6.el6                                         ol6_latest                              586 k
 libX11-common                                                 noarch                          1.6.0-6.el6                                         ol6_latest                              192 k
 libXau                                                        x86_64                          1.0.6-4.el6                                         ol6_latest                               24 k
 libXext                                                       x86_64                          1.3.2-2.1.el6                                       ol6_latest                               34 k
 libXi                                                         x86_64                          1.7.2-2.2.el6                                       ol6_latest                               36 k
 libXinerama                                                   x86_64                          1.1.3-2.1.el6                                       ol6_latest                               12 k
 libXmu                                                        x86_64                          1.1.1-2.el6                                         ol6_latest                               65 k
 libXrender                                                    x86_64                          0.9.8-2.1.el6                                       ol6_latest                               23 k
 libXt                                                         x86_64                          1.1.4-6.1.el6                                       ol6_latest                              164 k
 libXtst                                                       x86_64                          1.2.2-2.1.el6                                       ol6_latest                               18 k
 libXv                                                         x86_64                          1.0.9-2.1.el6                                       ol6_latest                               16 k
 libXxf86dga                                                   x86_64                          1.1.4-2.1.el6                                       ol6_latest                               17 k
 libXxf86misc                                                  x86_64                          1.0.3-4.el6                                         ol6_latest                               17 k
 libXxf86vm                                                    x86_64                          1.1.3-2.1.el6                                       ol6_latest                               16 k
 libaio                                                        x86_64                          0.3.107-10.el6                                      ol6_latest                               21 k
 libaio-devel                                                  x86_64                          0.3.107-10.el6                                      ol6_latest                               13 k
 libdmx                                                        x86_64                          1.1.3-3.el6                                         ol6_latest                               14 k
 libevent                                                      x86_64                          1.4.13-4.el6                                        ol6_latest                               65 k
 libgomp                                                       x86_64                          4.4.7-16.el6                                        ol6_latest                              133 k
 libgssglue                                                    x86_64                          0.1-11.el6                                          ol6_latest                               22 k
 libstdc++-devel                                               x86_64                          4.4.7-16.el6                                        ol6_latest                              1.6 M
 libtirpc                                                      x86_64                          0.2.1-10.el6                                        ol6_latest                               78 k
 libxcb                                                        x86_64                          1.9.1-3.el6                                         ol6_latest                              110 k
 mailx                                                         x86_64                          12.4-8.el6_6                                        ol6_latest                              234 k
 mpfr                                                          x86_64                          2.4.1-6.el6                                         ol6_latest                              156 k
 nfs-utils                                                     x86_64                          1:1.2.3-64.el6                                      ol6_latest                              330 k
 nfs-utils-lib                                                 x86_64                          1.1.5-11.el6                                        ol6_latest                               68 k
 ppl                                                           x86_64                          0.10.2-11.el6                                       ol6_latest                              1.3 M
 ql23xx-firmware                                               noarch                          3.03.27-3.1.el6                                     ol6_latest                               92 k
 rpcbind                                                       x86_64                          0.2.0-11.el6                                        ol6_latest                               51 k
 smartmontools                                                 x86_64                          1:5.43-1.el6                                        ol6_latest                              439 k
 sysstat                                                       x86_64                          9.0.4-27.el6                                        ol6_latest                              232 k
 xorg-x11-utils                                                x86_64                          7.5-6.el6                                           ol6_latest                               94 k
 xorg-x11-xauth                                                x86_64                          1:1.0.2-7.1.el6                                     ol6_latest                               34 k

Additional packages
1. zlib.i686
2. compat-libstdc++-33.i686
3. glibc-devel.i686
4. libstdc++.i686
5. openmotif
6. openmotif22
7. redhat-lsb-core
8. libXext.i686
9. libXtst.i686
10. libaio.i686
11. xorg-x11-apps
12. xorg-x11-xinit
13. xorg-x11-server-Xorg
14. xterm
```
### Kernel parameters
kernel.sem 256 32000 100 142 (Required by document)
The base package for oracle-rdbms-server-11gR2-preinstall install added the 250 32000 100 128. Left to that value
14. Updated /etc/security/limits.conf
* soft  nofile  4096
* hard  nofile  65536
# * soft  nproc   2047
* hard  nproc   16384

### Added public keys for all the users who need access.

# Instance specific

After launching the instance, the following operations were performed.

### Update
```
sudo yum update
```
### Change hostname (see above).
Update the /etc/cloud/cloud.cfg file on your RHEL 7 or Centos 7 Linux instance.
     sudo vim /etc/cloud/cloud.cfg
Append the following string at the bottom of the file to ensure that the hostname is preserved between restarts/reboots.
     preserve_hostname: true
Save and exit the vim editor.

### Mount the new disk
```
> fdisk /dev/xvdb
Command (m for help): n
Command action
   e   extended
   p   primary partition (1-4)
p
Partition number (1-4): 1
First cylinder (1-6527, default 1): 1
Last cylinder, +cylinders or +size{K,M,G} (1-6527, default 6527):
Using default value 6527

Command (m for help): w
> mkfs.ext4 -L Oracle /dev/xvdb1
> mkdir /opt/oracle
> mount -t ext4 /dev/xvdb1 /opt/oracle/
> chown oracle:oinstall /opt/oracle/
```
3. Add the new disk in mount file /etc/fstab
```
/dev/xvdb1              /opt/oracle             ext4    defaults        0 2
```
4. reboot.
5. copy the .ssh/authorized_keys to ~oracle/.ssh and make oracle:oinstall owner to allow people to login using oracle id
6. Added the relevant lines in /etc/hosts

