# Rename
Rename files with particular word with another word.
e.g. Renaming files with name containing word 'Create' to 'Bulk'
```
for file in *.xml ; do mv $file ${file//Create/Bulk} ; done
```

# Delete line
Delete a line matching given pattern
```
for file in *.xml ; do sed -i '/serviceaccount__c/d" ${file} ; done
```

# Read input
Read and validate input.
```
# A internal function used as default validation function in case no function is specified.
# It always returns response that input is valid.
defaultFunc() {
  return 0;
}

# Core function that reads the input
# $1 : Prompt to display
# $2 : Default value to set if user presses enter key.
# $3 : extensions to read function (e.g. -n 1 for key press, -s for not prompting input).
# $4 : Validation function to use.
# $5 - : Passed as input to validation function.
readVariable() {
  checkFunc=${4-defaultFunc}
  checkFuncResult=1
  while [ $checkFuncResult -eq 1 ];
  do
    prompt="Please provide input? ";
    if ( [ $# -ge 1 ] && [ -n "$1" ] ); then prompt="$1 ? "; fi;
    if ( [ $# -ge 2 ] && [ -n "$2" ] ); then prompt="${prompt}($2)"; fi;
    if ( [ $# -ge 3 ] && [ -n "$3" ] );
    then
      read -p "${prompt}" $3;
    else
      read -p "${prompt}";
    fi
    if ( [ -z "${REPLY}" ] && [ -n "$2" ] ); then result="$2"; else result="${REPLY}"; fi;
    $checkFunc "$result" ${@:5}
    checkFuncResult=$?
  done
  echo $result
}

# A simple function to write to error channel if the main shell is collecting output for echo.
# Used by validation function to print error messages (since the output is being collected by shell).
write () {
  ( >&2 echo $1 )
}

# Function to check whether to continue.
# Used by functions in case of error.
continueOnError() { prompt=$1; prompt=${prompt:="Continue?"}; skipError=$( readVariable "${prompt}" "Y/y" "-n 1" ); if [ ${skipError} != "Y/y" ]; then write; fi; if [ "${skipError}" == "Y/y" ] || [[ "${skipError}" =~ ^[Yy]$ ]]; then return 0; else return 1; fi }

# Check whether given hostname can be resolved.
validateHostName() {  if [[ -z $1 ]]; then return 1; fi; if [[ "`getent hosts $1`" == "" ]]; then write "Error: Hostname $1 could not be resolved"; continueOnError; return $?; else return 0; fi  }

isNumber(){ re='^[0-9]+$'; if [[ $1 =~ $re ]] ; then return 0; else  write "Error: $1 is not a number"; return 1; fi }

canWriteToDirectory(){ if [[ -w $1 ]] ; then return 0; else write "Error: Can not write to directory $1"; return 1; fi }

# Check whether system can connect to given host and port
isAlive(){ timeout 2 bash -c "</dev/tcp/$1/$2" 2>/dev/null; if [[ $? -eq 0 ]] ; then return 0; else write "Error: Failed to connect to server $1:$2"; continueOnError;  return $?; fi }

# Function to parse Cluster detail and validate connectivity to individual servers
# identified.
splitAndCheck() {
  allAlive=1; origIFS=$IFS;
  IFS=','; splitValues=(${1}) ; IFS=$origIFS;
  for item in "${splitValues[@]}";
  do
    IFS=':'; splitHostPort=(${item}); IFS=$origIFS; if [[ ${#splitHostPort[@]} -lt 2 ]] ; then write "Error: Server ${item} is not in <host>:<port> format"; return 1; else isAlive ${splitHostPort[0]} ${splitHostPort[1]}; isAliveResult=$?; if [[ isAliveResult -eq 1 ]]; then  return ${isAliveResult}; fi; fi
  done;
  return 0;
}

# A function that combines and validates multiple values.
validateAndConnect() { isNumber $1; if [[ $? -eq 1 ]] ; then return 1; fi;  validateHostName $2; if [[ $? -eq 1 ]]; then return 1; fi; isAlive $2 $1; if [[ $? -eq 1 ]]; then return 1; fi; }

canReadFile() { if ! [[ -r "$1" ]] ; then write "Error: Could not read file $1"; return 1; else return 0; fi }

# A function  to check whether the given file is a PKCS12 file. Check is performed using NSS pk12util tool.
validateP12File() { canReadFile $1; if [[ $? -eq 1 ]]; then return 1; fi; isValidPKCS12File=$( pk12util -l $1 -W 'sk\nsd!jksa#jdkjdskjsdkjsdds' 2>&1 >/dev/null| grep "The security password entered is incorrect." |wc -l ); if [[ "${isValidPKCS12File}" != "2" ]]; then write "Error: $1 is not a valid PKCS12 file"; return 1; else return 0; fi; }

# A function to validate PKCS12 certificate and password using pk12util tool.
validateP12() { canReadFile $2; if [[ $? -eq 1 ]] ; then return 1; fi; filePassword=$1; if [[ -z "${filePassword}" ]]; then certificate=$( pk12util -l $2 2>/dev/null ); else pk12util -l $2 -W $1 2>/dev/null >/dev/null ; if [[ $? -ne 0 ]]; then write; write "Error: PKCS12 File could not be read. Please check password"; return 1; fi; certificate=$( pk12util -l $2 -W $1 2>/dev/null ); fi; dnsName=$( echo "$certificate" | grep "DNS name: " | cut -d'"' -f2 ); if [[ "${dnsName}" == "$3" ]]; then return 0; else continueOnError "Domain name in certificate ${dnsName} does not match provided server name $3"; return $?; fi }

fully_qualified_server_name=$( readVariable "Server Name" "$( nslookup $( hostname ) |grep Name |cut -f 2 )" "" validateHostName )
port=$( readVariable "Port" "8443" "" isNumber )
site_name=$( readVariable "Name of site" "gidm" )
site_location=$( readVariable "Site Location" "/opt/apache/sites" "" canWriteToDirectory )
oim_admin_host=$( readVariable "OIM Admin server's fully qualified hostname" "" "" validateHostName )
oim_admin_port=$( readVariable "OIM Admin server port" "7002" "" validateAndConnect ${oim_admin_host} )

```
