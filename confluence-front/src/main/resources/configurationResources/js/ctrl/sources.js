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
                id: source.id,
                name: source.name,
                url: source.url,
                sourceType: source.sourceType,
                username: source.username,
                password: null,
                verified: true,
                nameCorrect: true,
                sourceExists: true,
                credentialsCorrect: true,
                sourceTypeCorrect: true,
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
                "id": null,
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



    sources.save = function(index) {
        var original = sources.list[index];
        var data = extractSourceDataForRest(original);

        var id = original.id;

        var req;
        if (original.id === null) {
            req = $http.post(url,data);
        } else {
            req = $http.put(url+"/"+original.id,data);
        }

        req.then(function(resp) {
            sources.list[index] = resp.data;
            sources.list[index].id = resp.data.id;
            sources.list[index].dirty = false;
            sources.list[index].verified = true;
            settingsData.reload();
        },function(resp) {
            if (resp.status == "400") {
                sources.list[index] = resp.data;
                sources.list[index].id = null;
                sources.list[index].dirty = false;
                sources.list[index].verified = false;
            }
        });

    };

    sources.delete = function(index) {
        var source = sources.list[index];
        if (source.id === null) {
            sources.removeFromList(index);
        } else {
            source.deletionState = "confirming";
        }
    };

    sources.cancelDeletion = function(index) {
        var source = sources.list[index];
        source.deletionState = null;
    };

    sources.deleteExecute = function(index) {
        var source = sources.list[index];
        source.deletionState = "executing";
        $http.delete(url+"/"+source.id).then(function(response) {
            sources.removeFromList(index);
            settingsData.reload();
        });

    };


});
