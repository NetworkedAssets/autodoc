angular.module('DoC_Config').factory('settingsData', function($http,$rootScope,$timeout,urlProvider){
    var settings = {
        path: null,
        raw: {},
        original: {}
    };
    var callbacks = {};

    settings.load = function() {
        $rootScope.loading = true;
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

                angular.forEach(callbacks,function(fn) {
                    fn();
                });
                $rootScope.loading = false;
            });
    };

    settings.reload = function() {
        return settings.load();
    };

    settings.save = function() {
        var chosen = settings.path;
        var branchSettings = settings.raw.sources[chosen.source].projects[chosen.project].repos[chosen.repo].branches[chosen.branch];
        branchSettings.scheduledEvents = settings.scheduledEvents;
        branchSettings.listenTo = settings.listenTo;

        var data = {
            scheduledEvents: settings.scheduledEvents,
            listenTo: settings.listenTo,
            displayId: branchSettings.displayId,
            id: branchSettings.id
        };

        if (data.listenTo !== "schedule") {
            delete data.scheduledEvents;
        }

        console.log(urlProvider.getRestUrlWithParams([
                "branches",
                chosen.source,
                chosen.project,
                chosen.repo,
                chosen.branch
            ])
        );

        console.log(data);

        $http
            .post(urlProvider.getRestUrlWithParams([
                "branches",
                chosen.source,
                chosen.project,
                chosen.repo,
                chosen.branch
            ]), data)
            .then(function (response) {
                console.log(response);
            });
    };

    settings.revert = function() {
        settings.setBranch(settings.path);
    };

    settings.get = function() {
        return this.getData();
    };

    settings.setBranch = function(chosen) {
        if (typeof chosen == "object") {
            var branchSettings = settings.raw.sources[chosen.source].projects[chosen.project].repos[chosen.repo].branches[chosen.branch];
            settings.scheduledEvents = branchSettings.scheduledEvents;
            settings.listenTo = branchSettings.listenTo;
            settings.path = chosen;
            settings.updateNowState = "ready";
        }
    };

    settings.getPath = function() {
        return settings.path;
    };

    var getSourceUrlFromId = function(id) {
        return settings.raw.sources[id].url;
    };

    settings.getPathAsString = function() {
        var url = urlProvider.encodeComponent(getSourceUrlFromId(settings.path.source));
        console.log(url);
        return url+"/"+settings.path.project+"/"+settings.path.repo+"/"+urlProvider.encodeComponent(settings.path.branch);
    };

    settings.resetBranch = function() {
        settings.path = null;
        settings.scheduledEvents = null;
        settings.listenTo = null;
        settings.updateNowState = "ready";
    };

    settings.getData = function() {
        return settings.raw;
    };

    settings.updateNow = function() {
        settings.updateNowState = "initiating";
        var callback = function() {
            settings.updateNowState = "initiated";
            $timeout();
        };

        console.log(settings.getPathAsString());
        console.log(urlProvider.getRestUrl("/event/"+settings.getPathAsString())+"/");

        /* // @RequestBody
        var data = {
            source: getSourceUrlFromId(settings.getPath().source),
            project: settings.getPath().project,
            repo: settings.getPath().repo,
            branch: settings.getPath().branch
        };

        $http
            .post(urlProvider.getRestUrl("/event"),data)
            .then(callback);*/

        // @PathParam
        $http
            .post(urlProvider.getRestUrl("/event/"+settings.getPathAsString())+"/")
            .then(callback);





        //setTimeout(callback,1000);
    };

    settings.registerCallback = function(name,fn) {
        if (typeof fn != "function") {
            console.error("Callback must be a function.");
        }

        callbacks[name] = fn;

    };

    $rootScope.listenToOptions = {
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

    $rootScope.settings = settings;



    settings.load();
    return settings;
});