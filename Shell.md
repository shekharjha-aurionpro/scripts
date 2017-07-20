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
