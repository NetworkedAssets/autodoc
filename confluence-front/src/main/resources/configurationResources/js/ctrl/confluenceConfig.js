angular.module("DoC_Config").controller("confluenceConfigCtrl",function($scope,$http,$rootScope,settingsData,$timeout,urlProvider) {
    var confluenceConfig = this;

    $scope.$on("settingsData.ready",function() {
        confluenceConfig.setFromSettingsData();
    });

    confluenceConfig.save = function() {
        var data = {
            confluenceUsername: confluenceConfig.username,
            confluencePassword: confluenceConfig.password
        };
        var success = function() {
            confluenceConfig.savingState = "saved";
            settingsData.raw.confluenceUsername = data.username;
            confluenceConfig.setFromSettingsData();
            $timeout();
        };
        var error = function() {
            console.log("error");
            confluenceConfig.savingState = "dirty";
            confluenceConfig.credentialsCorrect = false;
            $timeout();
        };
        console.log(data);
        confluenceConfig.savingState = "saving";

        // TODO Swap to this, when confluenceCredentials endpoint is ready
        $http.post(urlProvider.getRestUrl("/credentials"), {
            confluenceUsername: confluenceConfig.username,
            confluencePassword: confluenceConfig.password
        })
            .then(success,error);

        //setTimeout(function() {
        //    if (false) {
        //        success();
        //    } else {
        //        error();
        //    }
        //},1000);
    };

    confluenceConfig.setFromSettingsData = function() {
        confluenceConfig.savingState = "saved";
        confluenceConfig.username = settingsData.raw.confluenceUsername;
        confluenceConfig.password = null;
        confluenceConfig.credentialsCorrect = true;
    };

    confluenceConfig.get = function() {
        $http
            .get(urlProvider.getRestUrlWithParams("credentials"))
            .then(function(response) {
                confluenceConfig.savingState = "saved";
                confluenceConfig.username = response.data.confluenceUsername;
                confluenceConfig.password = null;
                confluenceConfig.credentialsCorrect = true;
            });
    };

    confluenceConfig.revert = function() {
        this.setFromSettingsData();
    };

    confluenceConfig.setAsDirty = function() {
        confluenceConfig.savingState = "dirty";
    };

    confluenceConfig.get();
});