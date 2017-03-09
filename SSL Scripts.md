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


