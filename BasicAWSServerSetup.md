
# Red Hat Enterprise Linux Server release 7.2 (Maipo) (HVM)

1. If needed stop firewall (not enabled or available)<br/>
`systemctl status firewalld`<br/>
`service firewalld stop`<br/>
`systemctl disable firewalld`<br/>
2. SELinux disable, if needed and then reboot (not done)
sed 's/enforcing/disabled/g' -i /etc/sysconfig/selinux
sed 's/enforcing/disabled/g' -i /etc/selinux/config
3. Timezone<br/>
`timedatectl set-timezone America/New_York`
4. Swap file setup and reboot, if needed<br/>
`mkdir /opt/swap`<br/>
`cd /opt/swap`<br/>
`dd if=/dev/zero of=/opt/swap/swapfile bs=1024 count=2MB`<br/>
`mkswap /opt/swap/swapfile`<br/>
`cp /etc/fstab /etc/fstab.bak`<br/>
`echo "/opt/swap/swapfile          swap            swap    defaults        0 0" >>/etc/fstab`
5. Create a new user<br/>
`useradd -c "Product specific user" -d /home/demo -g users -m -N demo`
5. Change hostname <br/>
`hostnamectl status`<br/>
`hostnamectl set-hostname <new name>.demo.ap`<br/>
6. Add new disk as physical disk
`fdisk /dev/xvdb`<br/>
Command (m for help): `n`<br/>
Command action<br/>
   e   extended<br/>
   p   primary partition (1-4)<br/>
`p`<br/>
Partition number (1-4): `1`<br/>
First cylinder (1-6527, default 1): `1`<br/>
Last cylinder, +cylinders or +size{K,M,G} (1-6527, default 6527):<br/>
Using default value 6527<br/>
<br/>
`Command (m for help): `w<br/>
`mkfs.ext4 -L Product /dev/xvdb1`<br/>
`mkdir /opt/product`<br/>
`mount -t ext4 /dev/xvdb1 /opt/product/`<br/>
`chown demo:users /opt/product/`<br/>
7. Add the new disk in mount file `/etc/fstab`<br/>
`/dev/xvdb1              /opt/product             ext4    defaults        0 2`
8. Update the install
`yum update`

