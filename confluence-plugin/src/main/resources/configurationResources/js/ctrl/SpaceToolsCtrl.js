angular.module("DoC_Config").controller("SpaceToolsCtrl", function($http, $scope, $rootScope, $timeout, $filter, urlService) {
    var spaceTools = this;

    spaceTools.path = null;
    spaceTools.raw = {};
    spaceTools.original = {};

    var listenForChanges = false;

    var processListenToValues = function() {
        var processParentChildListenTo = function(parent, child) {
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
        angular.forEach(spaceTools.raw.sources, function(source) {
            source.listenTo = "none";
            angular.forEach(source.projects, function(project) {
                project.listenTo = "none";
                angular.forEach(project.repos, function(repo) {
                    repo.listenTo = "none";
                    angular.forEach(repo.branches, function(branch) {
                        if (branch.listenTo === null) {
                            branch.listenTo = "none";
                        }
                        if (branch.listenTo !== "none") {
                            repo.listenTo = processParentChildListenTo(repo, branch);
                        }
                        angular.forEach(branch.scheduledEvents, function(event) {
                            event.oneTimeDate = parseDate(event.oneTimeDate);
                        });
                    });
                    if (repo.listenTo !== "none") {
                        project.listenTo = processParentChildListenTo(project, repo);
                    }
                });
                if (project.listenTo !== "none") {
                    source.listenTo = processParentChildListenTo(source, project);
                }
            });
        });
    };

    spaceTools.load = function() {
        spaceTools.loading = true;
        $http
            .get(urlService.getRestUrlWithParams("sources", "extended"))
            .then(function(response) {
                var sources = {};
                response.data.forEach(function(source) {
                    source.verified = true;
                    if (!source.name) {
                        source.name = "(no name)";
                    }
                    sources[source.id] = source;
                });
                spaceTools.raw = {};
                spaceTools.raw.sources = sources;
                processListenToValues();

                $scope.$broadcast("spaceTools.ready");
                spaceTools.error = false;
                spaceTools.loading = false;
            }, function() {
                spaceTools.loading = false;
                spaceTools.error = true;
            });
    };

    spaceTools.reload = function() {
        return spaceTools.load();
    };

    var formatDate = function(date) {
        return $filter("date")(date, "yyyy-MM-dd");
    };

    var parseDate = function(string) {
        if (string) {
            var date = new Date(string);
            date.setHours(0);
            return date;
        } else {
            return null;
        }

    };

    spaceTools.save = function() {
        listenForChanges = false;
        var chosen = spaceTools.path;
        var branchSettings = spaceTools.raw.sources[chosen.source].projects[chosen.project].repos[chosen.repo].branches[chosen.branch];
        branchSettings.scheduledEvents = spaceTools.scheduledEvents;
        branchSettings.listenTo = spaceTools.listenTo;

        var data = {
            scheduledEvents: angular.copy(spaceTools.scheduledEvents),
            listenTo: spaceTools.listenTo,
            displayId: branchSettings.displayId,
            id: branchSettings.id
        };

        data.scheduledEvents.forEach(function(event) {
            delete event.form;
            event.oneTimeDate = formatDate(event.oneTimeDate);
        });

        console.log(data.scheduledEvents);

        if (data.listenTo !== "schedule") {
            delete data.scheduledEvents;
        }

        spaceTools.savingState = "saving";
        $http
            .put(urlService.getRestUrlWithParams(
                "sources",
                chosen.source,
                chosen.project,
                chosen.repo,
                chosen.branch
            ), data)
            .then(function() {
                processListenToValues();
                spaceTools.savingState = "saved";
                $timeout(function() {
                    listenForChanges = true;
                    $scope.$broadcast("spaceTools.saved");
                    spaceTools.form.$setPristine();
                });
            }, function() {
                spaceTools.savingState = "dirty";
            });
    };

    spaceTools.revert = function() {
        spaceTools.setBranch(spaceTools.path);
    };

    spaceTools.get = function() {
        return this.getData();
    };

    spaceTools.setBranch = function(chosen) {
        listenForChanges = false;
        if (typeof chosen == "object") {
            var branchSettings = spaceTools.raw.sources[chosen.source].projects[chosen.project].repos[chosen.repo].branches[chosen.branch];
            spaceTools.scheduledEvents = angular.copy(branchSettings.scheduledEvents);
            spaceTools.listenTo = branchSettings.listenTo;
            spaceTools.path = chosen;
            spaceTools.updateNowState = "ready";
            //spaceTools.dirty = false;
            spaceTools.savingState = "saved";
            spaceTools.form.$setPristine();
            $timeout(function() {
                listenForChanges = true;
            });

        }
    };

    spaceTools.getPath = function() {
        return spaceTools.path;
    };

    var getSourceUrlFromId = function(id) {
        return spaceTools.raw.sources[id].url;
    };

    spaceTools.getPathAsString = function() {
        var url = urlService.encodeComponent(getSourceUrlFromId(spaceTools.path.source));
        return url + "/" + spaceTools.path.project + "/" + spaceTools.path.repo + "/" + urlService.encodeComponent(spaceTools.path.branch);
    };

    spaceTools.resetBranch = function() {
        listenForChanges = false;
        spaceTools.path = null;
        spaceTools.scheduledEvents = null;
        spaceTools.listenTo = null;
        spaceTools.updateNowState = "ready";
        spaceTools.savingState = "saved";
        $timeout(function() {
            listenForChanges = true;
        });
    };

    spaceTools.getData = function() {
        return spaceTools.raw;
    };

    spaceTools.updateNow = function() {
        listenForChanges = false;
        spaceTools.updateNowState = "updating";
        listenForChanges = true;

        $http
            .post(urlService.getRestUrl("/event/" + spaceTools.getPathAsString()) + "/")
            .then(function(response) {
                if (response.status == 202) {
                    require(['aui/flag'], function(flag) {
                        flag({
                            type: 'info',
                            title: 'Updating in background',
                            body: 'The documentation is quite big, so the generating process will continue in the background. You will be notified through activity stream when it\'s done.'
                        });
                    });

                    spaceTools.updateNowState = "updatingInBackground";
                } else {
                    spaceTools.updateNowState = "updated";
                }
                listenForChanges = false;

                $timeout(function() {
                    listenForChanges = true;
                });
            }, function() {
                listenForChanges = false;
                spaceTools.updateNowState = "ready";
                $timeout(function() {
                    listenForChanges = true;
                });
            });
    };

    spaceTools.getListenTo = function(type, id, chosen) {
        try {
            var obj;
            if (type == "source") {
                obj = spaceTools.raw.sources[id];
            } else if (type == "project") {
                obj = spaceTools.raw.sources[chosen.source].projects[id];
            } else if (type == "repo") {
                obj = spaceTools.raw.sources[chosen.source].projects[chosen.project].repos[id];
            } else if (type == "branch") {
                obj = spaceTools.raw.sources[chosen.source].projects[chosen.project].repos[chosen.repo].branches[id];
            }
            if (typeof obj == "object") {
                return obj.listenTo;
            }
        } catch (e) {
            if (!e instanceof TypeError) {
                throw e;
            }
        }
    };

    /*spaceTools.$watch(function() {
        return spaceTools;
    }, function() {
        if (listenForChanges) {
            spaceTools.savingState = "dirty";
        }
    }, true);*/

    spaceTools.listenToOptions = {
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


    spaceTools.load();
});