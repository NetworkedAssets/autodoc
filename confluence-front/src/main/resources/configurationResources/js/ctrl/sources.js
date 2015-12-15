angular.module("DoC_Config").controller("sourcesCtrl",function($scope,$http,settingsData,$timeout) {
    var sources = this;

    sources.list = [];

    sources.availableKinds = [
        {
            value: "BITBUCKET",
            label: "Bitbucket"
        },
        {
            value: "STASH",
            label: "Stash"
        }
    ];

    settingsData.registerCallback("sourcesCtrl",function() {
        sources.list = [];
        if (settingsData.get().sources) {

        }
        angular.forEach(settingsData.get().sources,function(source) {
            sources.add({
                name: source.name,
                url: source.url,
                sourceType: source.sourceType,
                username: source.username,
                password: null,
                verified: true
            });
        });
    });

    sources.remove = function(index) {
        sources.list.splice(index,1);
    };

    sources.check = function(index) {
        //var source = sources.list[index];
        var url = "data/checkCorrect.json";
        var request = $http.get(url);
        request.then(function(response) {
            sources.list[index] = response.data;
        });
    };

    sources.changed = function(source) {
        source.verified = false;
    };

    sources.add = function(source) {

        if (!source) {
            source = {
                "name": "",
                "url": "",
                "sourceType": "STASH",
                "username": "",
                "password": "",
                "hookKey": "",
                "slug": "staszek",
                "verified": false
            };
        }

        source.initializing = true;

        sources.list.push(source);

        $scope.$watch(function() {
            return source;
        },function() {
            if (source.initializing) {
                $timeout(function() { source.initializing = false; });
            } else {
                sources.changed(source);
            }

        },true);
    };


});
