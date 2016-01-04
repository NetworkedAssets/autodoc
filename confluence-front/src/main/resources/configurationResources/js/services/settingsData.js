angular.module('DoC_Config').factory('settingsData', function($http,$rootScope,$timeout,urlProvider){
    var settings = {
        path: null,
        raw: {}
    };

    $rootScope.loading = true;

    var callbacks = {};

    $http
        .get(urlProvider.getRestUrl("/projects"))
        //.get((urlProvider.isLocal())?urlProvider.getRestUrl("/projects"):urlProvider.getResourcesUrl("/data/settings3.json"))
        .then(function(response) {
            var sources = {};
            response.data.sources.forEach(function(source) {
                source.verified = true;
                if (!source.name) {
                    source.name = "(no name)";
                }
                sources[source.id] = source;
            });
            settings.raw = response.data;
            settings.raw.sources = sources;
            console.log("Original settings data: ",settings.raw.sources);
            //settings.setBranch(0,"AUT","autodoc","refs/heads/master");

            angular.forEach(callbacks,function(fn) {
                fn();
            });
            $rootScope.loading = false;
        });

    settings.save = function() {
        var chosen = settings.path;
        var branchSettings = settings.raw.sources[chosen.source].projects[chosen.project].repos[chosen.repo].branches[chosen.branch];
        branchSettings.scheduledEvents = settings.scheduledEvents;
        branchSettings.listensTo = settings.listensTo;
        console.log(urlProvider.getRestUrl("/projects"),settings.raw);
        $http
            .post(urlProvider.getRestUrl("/projects"),settings.raw)
            .then(function(response) {
                console.log(response);
            });
    }

    settings.get = function() {
        return this.getData();
    }

    $rootScope.$watch(function() {
        return settings;
    },function() {
        //console.log(settings);
    },true);

    settings.setBranch = function(chosen) {
        if (typeof chosen == "object") {
            var branchSettings = settings.raw.sources[chosen.source].projects[chosen.project].repos[chosen.repo].branches[chosen.branch];
            settings.scheduledEvents = branchSettings.scheduledEvents;
            settings.listensTo = branchSettings.listensTo;
            settings.path = chosen;
            settings.updateNowState = "ready";
        }
    };

    settings.getPath = function() {
        return settings.path;
    };

    settings.getPathAsString = function() {
        return settings.path.project+"/"+settings.path.repo+"/"+settings.path.branch;
    }

    settings.resetBranch = function() {
        settings.path = null;
        settings.scheduledEvents = null;
        settings.listensTo = null;
        settings.updateNowState = "ready";
    };

    settings.getData = function() {
        return settings.raw;
    };

    settings.updateNow = function() {
        settings.updateNowState = "initiating";
        var callback = function() {
            console.log("initiated?");
            settings.updateNowState = "initiated";
            $timeout();
        };

        console.log(settings.getPathAsString());
        /*$http
            .post(urlProvider.getRestUrl("/"+settingsData.getPathAsString()))
            .then(callback);*/

        setTimeout(callback,1000);
    };

    settings.registerCallback = function(name,fn) {
        if (typeof fn != "function") {
            console.error("Callback must be a function.");
        }

        callbacks[name] = fn;

    }

    $rootScope.settings = settings;

    return settings;
});