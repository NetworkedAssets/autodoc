angular.module('DoC_Config').factory('settingsService', function($http,$rootScope,$timeout,urlService){
    var settings = {
        path: null,
        raw: {},
        original: {}
    };
    var listenForChanges = false;

    var processListenToValues = function() {
        var processParentChildListenTo = function(parent,child) {
            var rtn;
            if (child.listenTo !== "none") {
                if (parent.listenTo !== "none") {
                    if (parent.listenTo !== child.listenTo) {
                        rtn = "both";
                    } else {
                        rtn = child.listenTo;
                    }
                } else {
                    rtn = child.listenTo;
                }
            } else {
                rtn = parent.listenTo;
            }
            return rtn;
        };
        angular.forEach(settings.raw.sources,function(source) {
            source.listenTo = "none";
            angular.forEach(source.projects,function(project) {
                project.listenTo = "none";
                angular.forEach(project.repos,function(repo) {
                    repo.listenTo = "none";
                    angular.forEach(repo.branches,function(branch) {
                        if (branch.listenTo !== "none") {
                            repo.listenTo = processParentChildListenTo(repo,branch);
                        }
                    });
                    if (repo.listenTo !== "none") {
                        project.listenTo = processParentChildListenTo(project,repo);
                    }
                });
                if (project.listenTo !== "none") {
                    source.listenTo = processParentChildListenTo(source,project);
                }
            });
        });
    };

    settings.load = function() {
        $rootScope.loading = true;
        $http
            .get(urlService.getRestUrlWithParams("sources","extended"))
            .then(function(response) {
                var sources = {};
                response.data.forEach(function(source) {
                    source.verified = true;
                    if (!source.name) {
                        source.name = "(no name)";
                    }
                    sources[source.id] = source;
                });
                settings.raw = {};
                settings.raw.sources = sources;
                processListenToValues();
                console.log("Original settings data: ",settings.raw.sources);

                $rootScope.$broadcast("settingsService.ready");
                $rootScope.error = false;
                $rootScope.loading = false;
            },function () {
                $rootScope.loading = false;
                $rootScope.error = true;
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

        settings.savingState = "saving";
        $http
            .put(urlService.getRestUrlWithParams(
                "sources",
                chosen.source,
                chosen.project,
                chosen.repo,
                chosen.branch
            ), data)
            .then(function () {
                processListenToValues();
                settings.savingState = "saved";
                $timeout(function() {
                    listenForChanges = true;
                    $rootScope.$broadcast("settingsService.saved");
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
        var url = urlService.encodeComponent(getSourceUrlFromId(settings.path.source));
        console.log(url);
        return url+"/"+settings.path.project+"/"+settings.path.repo+"/"+urlService.encodeComponent(settings.path.branch);
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

        $http
            .post(urlService.getRestUrl("/event/"+settings.getPathAsString())+"/")
            .then(function(response) {
                if (response.status == 202) {
                    require(['aui/flag'], function(flag) {
                        flag({
                            type: 'info',
                            title: 'Updating in background',
                            body: 'The documentation is quite big, so the generating process will continue in the background. You will be notified through activity stream when it\'s done.'
                        });
                    });

                    settings.updateNowState = "updatingInBackground";
                } else {
                    settings.updateNowState = "updated";
                }
                listenForChanges = false;

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
    };

    settings.getListenTo = function(type,id,chosen) {
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
                return obj.listenTo;
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