angular.module("DoC_Config").controller("sourcesCtrl",function($http,urlProvider) {
    var vm = this;

    vm.sources = [];

    var load = function() {
        var success = function(response) {
            vm.sources = response.data.sources;
        };
        var error = function(response) {
            var sources = [];
        };
        setTimeout(function() {
            var response = {
                data: {
                    "sources": [
                        {
                            "id": 0,
                            "credentialsCorrect": false,
                            "hookKey": "com.networkedassets.atlasian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener",
                            "projects": {
                            },
                            "sourceType": "STASH",
                            "url": "http://atlas.networkedassets.net:7990",
                            "nameCorrect": true,
                            "password": null,
                            "correct": false,
                            "username": null,
                            "sourceTypeCorrect": false,
                            "sourceExists": true,
                            "name": "AtlasDev Stash"
                        },
                        {
                            "id": 0,
                            "credentialsCorrect": false,
                            "hookKey": "com.networkedassets.atlassian.plugins.bitbucket-postReceive-hook-plugin:postReceiveHookListener",
                            "projects": {
                            },
                            "sourceType": "BITBUCKET",
                            "url": "http://atlas.networkedassets.net:7991",
                            "nameCorrect": true,
                            "password": null,
                            "correct": false,
                            "username": null,
                            "sourceTypeCorrect": false,
                            "sourceExists": true,
                            "name": "Bitbucket"
                        }
                    ]
                },
                status: "OK"
            }
            success.call(this,response);
        },500);
        /*
        $http
            .get(urlProvider.getRestUrlWithParams(["applinks","sources"]))
            .then(success,error);*/
    };

    load();
});