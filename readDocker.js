
var https = require('https');

var processedRecord = 0;
var processedAutomated =0;
var processedDockerFile = 0;
var lastResultSetProcessed = false;

var concurrentFunctions = [];

var dockerFiles = {};

function push(){concurrentFunctions.push(new Object());}
function pop() {concurrentFunctions.pop();printResult();}

function readDockerSearchPage(url) {
    push();
    https.get(url, function (res) {
        push();
        var body = '';
        // console.log("Got response: " + res.statusCode);
        res.on('data', function (d) {
            body += d;
        });
        res.on('end', function () {
            push();
            var queryResponse = JSON.parse(body);
            var nextURL = queryResponse.next;
            processSearchResult(queryResponse.results);
            if (nextURL != null) {
                readDockerSearchPage(nextURL);
            } else {
                lastResultSetProcessed = true;
                console.log("Completed processing all the result...");
            }
            pop();
        })
        pop();
    }).on('error', function (e) {
        console.log("Got error: " + e.message);
    });
    pop();
}

function searchDocker(queryString) {
    readDockerSearchPage("https://hub.docker.com/v2/search/repositories/?page=1&query=" + queryString);
}

function processSearchResult(results) {
    push();
    var arrayLength = results.length;
    for (var i = 0; i < arrayLength; i++) {
        var repo_name = results[i].repo_name;
        //console.log("Processing record " + repo_name);
        if (true == results[i].is_automated) {
            loadDockerFile(repo_name);
        }
        processedRecord++;
        if (processedRecord % 10 == 0 ) {
            console.log("Processed: " + processedRecord + " Automated: " + processedAutomated + " Docker file processed " + processedDockerFile);
        }
    }
    pop();
}

function loadDockerFile(processed_repo_name) {
    push();
    https.get("https://hub.docker.com/v2/repositories/" + processed_repo_name + "/dockerfile/", function (dockerResponse) {
        push();
        var dockerFileBody = '';
        if (dockerResponse.statusCode == 200) {
            dockerResponse.on('data', function (d) {
                dockerFileBody += d;
            });
            dockerResponse.on('end', function () {
                push();
                var parsedResponse = JSON.parse(dockerFileBody);
                dockerFiles[processed_repo_name] = parsedResponse.contents;
                var linesInFile = dockerFiles[processed_repo_name].split('\n');
                for (var lineIndex in linesInFile) {
                    var line = linesInFile[lineIndex];
                    line.trim();
                    if (line.indexOf("#") == 0)
                        continue;
                    var locationOfFROM = line.indexOf("FROM ");
                    var locationOfSemiColon = line.indexOf(":");
                    if (locationOfFROM > -1) {
                        var parentRepoName = line.substring(locationOfFROM + 5);
                        if (locationOfSemiColon > -1) {
                            parentRepoName = line.substring(locationOfFROM + 5, locationOfSemiColon);
                        }
                        parentRepoName.trim();
                        if (parentRepoName in dockerFiles) {
                            //console.log("Already loaded ")
                        } else {
                            //console.log("Loading docker file for " + parentRepoName);
                            loadDockerFile(parentRepoName);
                        }
                    }
                }
                processedDockerFile++;
                pop();
            });
        }
        pop();
    }).on('error', function (dockerError) {
            console.log("Could not locate docker file for " + repo_name + ". Ignoring.");
        }
    );
    processedAutomated++;
    pop();
}

function printResult() {
    if (concurrentFunctions.length == 0 && lastResultSetProcessed) {
        console.log("Processed: " + processedRecord + " Automated: " + processedAutomated + " Docker file processed " + processedDockerFile);
        var file_dump = "";
        var repo_names = Object.keys(dockerFiles);
        repo_names.sort().forEach(function (key) {
            console.log(key);
            file_dump += "-------------------------- " + key + " ------------------------\n";
            file_dump +=dockerFiles[key];
            file_dump +="\n";
            // console.log("(" + key + ")" + dockerFiles[key]);
        });
        var fs = require("fs");
        fs.writeFile("./dump.txt", file_dump, function (err) {
            if (err) return console.log(err);
            console.log('Generated file dump.txt');
        });
    }
}

if (typeof process != 'undefined' && process && typeof process.argv != 'undefined' && process.argv && process.argv.length > 2) {
    searchDocker(process.argv[2]);
} else {
    searchDocker('storm');
}
