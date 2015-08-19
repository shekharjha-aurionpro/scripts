
var https = require('https');

var processedRecord = 0;
var processedAutomated =0;
var processedDockerFile = 0;

function readDockerSearchPage(url) {
    https.get(url, function (res) {
        var body = '';
        // console.log("Got response: " + res.statusCode);
        res.on('data', function (d) {
            body += d;
        });
        res.on('end', function () {
            var queryResponse = JSON.parse(body);
            var nextURL = queryResponse.next;
            var arrayLength = queryResponse.results.length;
            for (var i = 0; i < arrayLength; i++) {
                var repo_name = queryResponse.results[i].repo_name;
                //console.log("Processing record " + repo_name);
                if (true == queryResponse.results[i].is_automated) {
                    (function (processed_repo_name) {
                        https.get("https://hub.docker.com/v2/repositories/" + repo_name + "/dockerfile/", function (dockerResponse) {
                            var dockerFileBody = '';
                            //console.log("Got response: " + dockerResponse.statusCode);
                            dockerResponse.on('data', function (d) {
                                dockerFileBody += d;
                            });
                            dockerResponse.on('end', function () {
                                console.log("( " + processed_repo_name + ") - " + dockerFileBody);
                                processedDockerFile++;
                            });
                        }).on('error', function (dockerError) {
                                console.log("Could not locate docker file for " + repo_name + ". Ignoring");
                            }
                        );
                        processedAutomated++;
                    })(repo_name);
                }
                processedRecord++;
                if (processedRecord % 10 == 0 ) {
                    console.log("Processed: " + processedRecord + " Automated: " + processedAutomated + " Docker file processed " + processedDockerFile);
                }
            }
            if (nextURL != null) {
                readDockerSearchPage(nextURL);
            } else {
                console.log("Completed processing all the result...");
            }
        })
    }).on('error', function (e) {
        console.log("Got error: " + e.message);
    });
}

function searchDocker(queryString) {
    readDockerSearchPage("https://hub.docker.com/v2/search/repositories/?page=1&query=" + queryString);
}

if (typeof process != 'undefined' && process && typeof process.argv != 'undefined' && process.argv && process.argv.length > 2) {
    searchDocker(process.argv[2]);
} else {
    searchDocker('storm');
}
