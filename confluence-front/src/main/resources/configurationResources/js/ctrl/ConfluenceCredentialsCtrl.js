angular.module("DoC_Config").controller("ConfluenceCredentialsCtrl",function($scope,$resource,$rootScope,settingsData,$timeout,urlProvider) {
    var cc = this;

    var Credentials = $resource(urlProvider.getRestUrl("/credentials"),{},{
        get: {
            method: "GET",
            transformResponse: function(data) {
                data = JSON.parse(data);
                var obj = {
                    username: data.confluenceUsername,
                    password: null
                };
                console.log(obj);
                return obj;
            }
        },
        update: {
            method: "POST",
            transformRequest: function(data) {
                return {
                    confluenceUsername: data.username,
                    confluencePassword: data.password
                }
            }
        }
    });

    cc.save = function() {
        cc.savingState = "saving";

        cc.credentials.$save().then(function() {
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