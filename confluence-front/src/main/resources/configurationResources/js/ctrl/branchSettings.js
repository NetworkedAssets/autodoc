angular.module("DoC_Config").controller("branchSettingsCtrl",function($scope,$http,settingsData,$rootScope) {

    var branchSettings = this;

    var url = "http://46.101.240.138:8090/rest/autodoc/1.0/configuration/TEST/pages";

    branchSettings.listenToOptions = {
        "mon": {
            label: "Off",
            value: "none"
        },
        "tue": {
            label: "Git event",
            value: "git"
        },
        "wed": {
            label: "Schedule",
            value: "schedule"
        }
    };

    branchSettings.settings = settingsData;

    branchSettings.pages = [];

    $http.get(url).then(function(response) {
        var pages = [];
        angular.forEach(response.data,function(page) {
            pages.push({
                value: page.id,
                label: page.title
            });
        });

        branchSettings.pages = pages;
    });

    /*settingsData.registerCallback("branchSettingsCtrl",function() {
        branchSettings.pages = [];
        console.log(settingsData.get().pages);
    })*/

});