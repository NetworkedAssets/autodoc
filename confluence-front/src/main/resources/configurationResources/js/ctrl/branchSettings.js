angular.module("DoC_Config").controller("branchSettingsCtrl",function($scope,$http,settingsData,$rootScope) {

    var branchSettings = this;

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


});