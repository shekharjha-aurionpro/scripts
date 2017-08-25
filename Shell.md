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
# $3 : extensions to read function (e.g. -n 1 for key press, -s for not prompting input.
# $4 : Validation function to use.
# $5 - : Passed as input to validation function.
readVariable() {
  checkFunc=${4-defaultFunc}
  checkFuncResult=1
  while [ $checkFuncResult -eq 1 ];
  do
    if [ $# -eq 0 ]
    then
      read -p "Please provide input? "; result=${REPLY};
    elif ( [ $# -eq 1 ] || ( [ $# -ge 2 ] && [ -z "$2" ] ) )
    then
      read -p "$1 ? "; result=${REPLY};
    elif [ $# -eq 2 ] || ( [ $# -ge 3 ] && [ -z "$3" ] )
    then
      read -p "$1 ($2)? "
      if [ -z "${REPLY}" ]; then result=$2; else result=${REPLY}; fi
    else
      read -p "$1 ($2)? " $3
      if [ -z "${REPLY}" ]; then result=$2; else result=${REPLY}; fi
    fi
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
continueOnError() { skipError=$( readVariable "Continue?" "Y/y" "-n 1" ); if [ ${skipError} != "Y/y" ]; then write; fi; if [ "${skipError}" == "Y/y" ] || [[ "${skipError}" =~ ^[Yy]$ ]]; then return 0; else return 1; fi }

# Check whether given hostname can be resolved.
validateHostName() {  if [[ -z $1 ]]; then return 1; fi; if [[ "`getent hosts $1`" == "" ]]; then write "Error: Hostname $1 could not be resolved"; continueOnError; return $?; else return 0; fi  }

isNumber(){ re='^[0-9]+$'; if [[ $1 =~ $re ]] ; then return 0; else  write "Error: $1 is not a number"; return 1; fi }

canWriteToDirectory(){ if [[ -w $1 ]] ; then return 0; else write "Error: Can not write to directory $1"; return 1; fi }

# Check whether system can connect to given host and port
isAlive(){ timeout 2 bash -c "</dev/tcp/$1/$2" 2>/dev/null; if [[ $? -eq 0 ]] ; then return 0; else write "Error: Failed to connect to server $1:$2"; continueOnError;  return $?; fi }

# Function to parse Cluster detail and validate connectivity to individual servers
# identified.
splitAndCheck() {
  IFS=','; splitValues=(${1})
  for item in "${splitValues[@]}";
  do
    IFS=':'; splitHostPort=(${item}); if [[ ${#splitHostPort[@]} -lt 2 ]] ; then return 1; else isAlive ${splitHostPort[0]} ${splitHostPort[1]}; isAliveResult=$?  return ${isAliveResult}; fi
  done;
  return 1;
}

# A function that combines and validates multiple values.
validateAndConnect() { isNumber $1; if [[ $? -eq 1 ]] ; then return 1; fi;  validateHostName $2; if [[ $? -eq 1 ]]; then return 1; fi; isAlive $2 $1; if [[ $? -eq 1 ]]; then return 1; fi; }

defaultHostName=`nslookup ${serverHostName} |grep Name |cut -f 2`
fully_qualified_server_name=$( readVariable "Server Name" "${defaultHostName}" "" validateHostName )
echo ${fully_qualified_server_name}
oim_admin_host=$( readVariable "OIM Admin server's fully qualified hostname" "" "" validateHostName )
oim_admin_port=$( readVariable "OIM Admin server port" "7001" "" validateAndConnect ${oim_admin_host} )

```
