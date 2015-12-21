angular.module("DoC_Config").controller("sourcesCtrl",function($scope,$http,settingsData,$timeout,urlProvider) {
    var sources = this;

    var url = urlProvider.getRestUrl("/source");

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
        angular.forEach(settingsData.get().sources,function(source,key) {
            sources.add({
                sourceId: source.id,
                name: source.name,
                url: source.url,
                sourceType: source.sourceType,
                username: source.username,
                password: null,
                verified: true
            });
        });
    });

    sources.removeFromList = function(index) {
        sources.list.splice(index,1);
        $timeout();
    };

    sources.changed = function(source) {
        source.verified = false;
    };

    sources.add = function(source) {

        if (!source) {
            source = {
                "sourceId": null,
                "name": "",
                "url": "",
                "sourceType": "STASH",
                "username": "",
                "password": "",
                "hookKey": "",
                "verified": false
            };
        }

        source.initializing = true;

        sources.list.push(source);

        $scope.$watch(function() {
            return source;
        },function(newValue,oldValue) {
            if (newValue!==oldValue) {
                sources.changed(source);
            }
        },true);
    };



    sources.save = function(id) {
        var source = sources.list[id];
        var req;
        if (source.sourceId === null) {
            req = $http.post(url);
        } else {
            req = $http.put(url+"/"+sourceId);
        }

        req.then(function(resp) {
            if (resp.status == "201") {
                source = resp.data;
            } else {
                console.log("Some error: "+resp.data);
            }
        });

    }

    sources.delete = function(id) {
        var source = sources.list[id];
        if (source.sourceId === null) {
            sources.removeFromList(id);
        } else {
            source.deletionState = "confirming";
        }
    }

    sources.cancelDeletion = function(id) {
        var source = sources.list[id];
        source.deletionState = null;
    }

    sources.deleteExecute = function(id) {
        var source = sources.list[id];
        source.deletionState = "executing";
        //$http.delete(url).then();
        setTimeout(function() {
            console.log("hm...");
            sources.removeFromList(id);
        },500);
    }


});
