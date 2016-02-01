angular.module("DoC_Config").controller("ConfluenceCredentialsCtrl",function($scope,$resource,$rootScope,settingsService,$timeout,urlService) {
    var cc = this;

    var Credentials = $resource(urlService.getRestUrl("/credentials"),{},{
        get: {
            method: "GET",
            transformResponse: function(data) {
                data = JSON.parse(data);
                var obj = {
                    username: data.confluenceUsername,
                    password: null
                };
                return obj;
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
        },function() {
            cc.credentialsCorrect = false;
            cc.savingState = "dirty";
        });
    };

    cc.revert = function() {
        cc.get();
    };

    cc.get = function() {
        cc.loading = true;
        cc.credentials = Credentials.get();
        cc.credentials.$promise.then(function() {
            cc.savingState = "saved";
            cc.loading = false;
            cc.credentialsCorrect = true;
            console.log(cc);
            $timeout();
        });
    };

    cc.setAsDirty = function() {
        cc.savingState = "dirty";
    };

    cc.get();
});