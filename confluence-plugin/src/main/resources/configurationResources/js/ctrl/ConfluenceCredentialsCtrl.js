angular.module("DoC_Config").controller("ConfluenceCredentialsCtrl",function($scope,$resource,$rootScope,$timeout,urlService) {
    var cc = this;
    cc.error = false;

    var Credentials = $resource(urlService.getRestUrl("/credentials"),{},{
        get: {
            method: "GET",
            transformResponse: function(data) {
                data = JSON.parse(data);
                return {
                    username: data.confluenceUsername,
                    password: null
                };
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
            if (response.status == "401") {
                cc.error = false;
                cc.credentialsCorrect = false;
            } else {
                cc.error = true;
            }
        });
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
        },function() {
            cc.loading = false;
            cc.error = true;
        });
    };

    cc.setAsDirty = function() {
        cc.savingState = "dirty";
    };

    cc.get();
});