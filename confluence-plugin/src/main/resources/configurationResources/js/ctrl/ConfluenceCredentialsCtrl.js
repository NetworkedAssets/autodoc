angular.module("DoC_Config").controller("ConfluenceCredentialsCtrl",function($scope,$resource,$rootScope,$timeout,urlService) {
    var cc = this;
    cc.error = false;

    var Credentials = $resource(urlService.getRestUrl("/credentials"),{},{
        get: {
            method: "GET",
            transformResponse: function(data) {
                data = JSON.parse(data);
                if (!data.error) {
                    return {
                        username: data.confluenceUsername,
                        password: null
                    };
                } else {
                    return data;
                }
            }
        },
        update: {
            method: "POST",
            transformRequest: function(data) {
                return JSON.stringify({
                    confluenceUsername: data.username,
                    confluencePassword: data.password
                });
            }
        }
    });

    cc.save = function() {
        cc.savingState = "saving";

        cc.credentials.$update().then(function() {
            cc.get();
        },function(response) {
            cc.savingState = "dirty";
            cc.handleResponse(response);
            if (response.status == "401") {
                cc.credentialsCorrect = false;
            }
        });
    };

    cc.handleResponse = function(response) {
        console.log(response);
        if (response.status == 500) {
            console.log(response.data);
            if (response.data && response.data.error && response.data.error.match(/Connect to.*failed/)) {
                cc.transformerConnectionError = true;
                cc.error = true;
                $rootScope.$broadcast("ConfluenceCredentials.transformerConnectionError");
            } else {
                cc.transformerConnectionError = false;
                cc.error = true;
            }
        } else {
            cc.error = false;
            cc.transformerConnectionError = false;
        }
    };

    cc.revert = function() {
        cc.get();
    };

    cc.get = function() {
        cc.loading = true;
        cc.credentials = Credentials.get();
        cc.credentials.$promise.then(function() {
            cc.error = false;
            cc.savingState = "saved";
            cc.loading = false;
            cc.credentialsCorrect = true;
            $rootScope.$broadcast("ConfluenceCredentials.ready",cc.credentials.username);
            $timeout();
        },function(response) {
            cc.loading = false;
            cc.handleResponse(response);
        });
    };

    cc.setAsDirty = function() {
        cc.savingState = "dirty";
    };

    cc.get();
});