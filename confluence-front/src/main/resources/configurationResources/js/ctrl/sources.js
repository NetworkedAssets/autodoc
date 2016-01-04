angular.module("DoC_Config").controller("sourcesCtrl",function($scope,$http,settingsData,$timeout,urlProvider) {
    var sources = this;

    var url = urlProvider.getRestUrl("/source");


    var extractSourceDataForRest = function(original) {
        var data = {};

        data.name = original.name;
        data.url = original.url;
        data.sourceType = original.sourceType;
        data.password = original.password;
        data.username = original.username;
        return data;
    };

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
                verified: true,
                nameCorrect: true,
                sourceExists: true,
                credentialsCorrect: true,
                dirty: false
            });
        });
    });

    sources.removeFromList = function(index) {
        sources.list.splice(index,1);
        $timeout();
    };

    sources.changed = function(source) {
        source.verified = false;
        source.dirty = true;
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
                "verified": false,
                dirty: true
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
        var original = sources.list[id];
        var data = extractSourceDataForRest(original);

        var sourceId = original.sourceId;

        var req;
        if (original.sourceId === null) {
            req = $http.post(url,data);
        } else {
            req = $http.put(url+"/"+original.sourceId,data);
        }

        req.then(function(resp) {
            sources.list[id] = resp.data;
            sources.list[id].sourceId = resp.data.id;
            sources.list[id].dirty = false;
            sources.list[id].verified = true;
        },function(resp) {
            if (resp.status == "400") {
                sources.list[id] = resp.data;
                sources.list[id].sourceId = resp.data.id;
                sources.list[id].dirty = false;
                sources.list[id].verified = false;
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
        $http.delete(url+"/"+source.sourceId).then(function(response) {
            sources.removeFromList(id);
        });

    }


});
