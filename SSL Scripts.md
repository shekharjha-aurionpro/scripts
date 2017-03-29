Simple scripts for various SSL operations using OpenSSL and Java keystores

# CSR Generation
```
if [ $# -eq 0 ]
then
   echo "Please provide fully qualified server name"
   exit
fi
echo "Processing $1 "
splitValues=(${1//./ })
if [ ${#splitValues[@]} -lt 2 ]
then
   echo "Please ensure that server name is fully qualified"
   exit
fi
if [[ "`getent hosts $1`" == '' ]]
then
   echo "The given server name $1 could not be resolved. Please ensure that server name is correct"
   read -p "Proceed (y/n)? " -n 1 -r
   echo
   if [[ $REPLY =~ ^[Yy]$ ]]
   then
     echo "Moving ahead with process"
   else
     exit
   fi
fi
processHostName=${splitValues[0]}
mkdir -p $processHostName
cd $processHostName
openssl genrsa -out ./$processHostName.key 2048
openssl req -new -key ./$processHostName.key -out ./$processHostName.csr -subj "/C=IN/ST=MH/L=WHATEVER/O=ACMEINC/CN=$1"
```

# Create JKS
```
JAVA_HOME="${JAVA_HOME:-/opt/oracle/java/jdk1.7.0_80}"
if [ $# -eq 0 ]
then
   echo "Please provide fully qualified server name"
   exit
fi
echo "Processing $1 "
splitValues=(${1//./ })
if [ ${#splitValues[@]} -lt 1 ]
then
   echo "Please ensure that server name is provided"
   exit
fi
processHostName=${splitValues[0]}

if [ ! -d "$processHostName" ];
then
  echo "Looks like no SSL initialization has not yet been performed. Please generate CSR and get signed certificate before running this script"
  exit
fi
if [ ! -f "$processHostName/$processHostName.key" ]
then
  echo "Failed to locate private key. Please ensure that SSL initialization was performed correctly."
  exit
fi
if [ ! -f "$processHostName/$processHostName.cert" ]
then
  echo "Looks like CSR was created but signed certificate is not available. Please ensure that signed certificate is stored in $processHostName.cert file"
  exit
fi
cd $processHostName
read -s -p "Keystore password?" keystorePassword
echo
read -s -p "Private key password?" privateKeyPassword
echo
rm -f $processHostName.p12
openssl pkcs12 -export -in $processHostName.cert -inkey $processHostName.key -out $processHostName.p12 -password pass:$privateKeyPassword -name $processHostName
rm -f $processHostName.jks
$JAVA_HOME/bin/keytool -importkeystore -srckeystore $processHostName.p12 -srcstoretype PKCS12 -destkeystore $processHostName.jks -deststoretype JKS -srcstorepass $privateKeyPassword -destkeypass $privateKeyPassword -deststorepass $keystorePassword -alias $processHostName
rm -f $processHostName.p12
cd ..
for cert in `ls *.crt`;
do
  $JAVA_HOME/bin/keytool -import -file $cert -alias $cert -trustcacerts -keystore $processHostName/$processHostName.jks -storepass $keystorePassword -noprompt
done
```


