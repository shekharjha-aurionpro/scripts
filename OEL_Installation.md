Oracle Enterprise Linux 7.1 with Docker

# Base Installation (not production)

1. Language : English (United States)
2. Date and Time: Chicago as Timezone, After enabling Network, enable NTP
3. Installation Destination: Default Partitioning using LVOL & XFS file system
4. Network & Hostname: iam.jhash.com, Select the MAC Address in configuration and select connect whenever available
5. Set root password and create a new user demo with admin access
6. Software selection minimal
7. Ensure that there are two networks
   a. Network Host Only & VNet0 is selected. - This is for connectivity from local machine
   b. NAT - this is for external connectivity

# Configuration

1. Login using demo
2. sudo apt-get update (multiple times) and reboot
3. sudo systemctl stop firewalld
4. sudo systemctl disable firewalld
10. sudo su -
11. cat >/etc/yum.repos.d/docker.repo <<-EOF
 [dockerrepo]
 name=Docker Repository
 baseurl=https://yum.dockerproject.org/repo/main/centos/7
 enabled=1
 gpgcheck=1
 gpgkey=https://yum.dockerproject.org/gpg
 EOF
12. sudo yum install docker-engine
13. sudo usermod -aG docker demo
14. sudo chkconfig docker on
15. docker run oraclelinux:7.0
16. curl -L https://github.com/docker/compose/releases/download/1.4.0/docker-compose-`uname -s`-`uname -m` > docker-compose
sudo mv docker-compose /usr/local/bin
chmod +x /usr/local/bin/docker-compose
17. TODO: add code to profile to automatically update docker-compose on start.

## Notes

1. Docker can be installed from Oracle Yum
..1. Edit /etc/yum.repos.d/public-yum-ol7.repo and set enabled=1 in the [ol7_addons] stanza.
..2. sudo yum install docker
..3. sudo systemctl start docker.service
..4. sudo systemctl enable docker.service
..5. sudo yum remove docker
