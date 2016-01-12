angular.module('DoC_Config').factory('settingsData', function($http,$rootScope,$timeout,urlProvider){
    var settings = {
        path: null,
        raw: {},
        original: {}
    };
    var listenForChanges = false;

    var processListenToValues = function() {
        angular.forEach(settings.raw.sources,function(source) {
            source.isListened = false;
            angular.forEach(source.projects,function(project) {
                project.isListened = false;
                angular.forEach(project.repos,function(repo) {
                    repo.isListened = false;
                    angular.forEach(repo.branches,function(branch) {
                        branch.isListened = branch.listenTo !== "none";
                        if (branch.isListened) {
                            repo.isListened = true;
                        }
                    });
                    if (repo.isListened) {
                        project.isListened = true;
                    }
                });
                if (project.isListened) {
                    source.isListened = true;
                }
            });
        });
    };

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
                processListenToValues();
                console.log("Original settings data: ",settings.raw.sources);

                $rootScope.$broadcast("settingsData.ready");

                $rootScope.loading = false;
            });
    };

    settings.reload = function() {
        return settings.load();
    };

    settings.save = function() {
        listenForChanges = false;
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

        /*console.log(urlProvider.getRestUrlWithParams([
                "branches",
                chosen.source,
                chosen.project,
                chosen.repo,
                chosen.branch
            ])
        );*/

        /*console.log(data);*/
        settings.savingState = "saving";
        $http
            .post(urlProvider.getRestUrlWithParams([
                "branches",
                chosen.source,
                chosen.project,
                chosen.repo,
                chosen.branch
            ]), data)
            .then(function (response) {
                processListenToValues();
                settings.savingState = "saved";
                $timeout(function() {
                    listenForChanges = true;
                    $rootScope.$broadcast("settingsData.saved");
                });
            },function() {
                settings.savingState = "dirty";
            });
    };

    settings.revert = function() {
        settings.setBranch(settings.path);
    };

    settings.get = function() {
        return this.getData();
    };

    settings.setBranch = function(chosen) {
        listenForChanges = false;
        if (typeof chosen == "object") {
            var branchSettings = settings.raw.sources[chosen.source].projects[chosen.project].repos[chosen.repo].branches[chosen.branch];
            settings.scheduledEvents = branchSettings.scheduledEvents;
            settings.listenTo = branchSettings.listenTo;
            settings.path = chosen;
            settings.updateNowState = "ready";
            //settings.dirty = false;
            settings.savingState = "saved";
            $timeout(function() {
                listenForChanges = true;
            });

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
        listenForChanges = false;
        settings.path = null;
        settings.scheduledEvents = null;
        settings.listenTo = null;
        settings.updateNowState = "ready";
        settings.savingState = "saved";
        $timeout(function() {
            listenForChanges = true;
        });
    };

    settings.getData = function() {
        return settings.raw;
    };

    settings.updateNow = function() {
        listenForChanges = false;
        settings.updateNowState = "updating";
        listenForChanges = true;

        /*console.log(settings.getPathAsString());
        console.log(urlProvider.getRestUrl("/event/"+settings.getPathAsString())+"/");*/

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
            .then(function() {
                listenForChanges = false;
                settings.updateNowState = "updated";
                $timeout(function() {
                    listenForChanges = true;
                });
            },function() {
                listenForChanges = false;
                settings.updateNowState = "ready";
                $timeout(function() {
                    listenForChanges = true;
                });
            });





        //setTimeout(callback,1000);
    };

    settings.getIsListened = function(type,id,chosen) {
        try {
            var obj;
            if (type == "source") {
                obj = settings.raw.sources[id];
            } else if (type == "project") {
                obj =  settings.raw.sources[chosen.source].projects[id];
            } else if (type == "repo") {
                obj =  settings.raw.sources[chosen.source].projects[chosen.project].repos[id];
            } else if (type == "branch") {
                obj =  settings.raw.sources[chosen.source].projects[chosen.project].repos[chosen.repo].branches[id];
            }
            if (typeof obj == "object") {
                return obj.isListened;
            }
        } catch(e) {
            if (!e instanceof TypeError) {
                throw e;
            }
        }
    };

    $rootScope.$watch(function() {
        return settings;
    },function() {
        if (listenForChanges) {
            settings.savingState = "dirty";
        }
    },true);

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