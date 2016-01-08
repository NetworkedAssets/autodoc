angular.module("DoC_Config").controller("menuCtrl",function($scope,$http,$timeout,settingsData) {
    var menu = this;

    menu.chosen = {
        source: null,
        project: null,
        repo: null,
        branch: null
    };

    menu.weekdays;

    var fakeChosen = function() {
        menu.chosen = {
            sourceProject: "staszek\uF000APD",
            repo: "javadoc-plugin",
            branch: "refs/heads/master"
        };
        $timeout();
    };

    var processTree = function(raw) {
        menu.tree = {};
        menu.tree.sources = {};
        angular.forEach(raw.sources,function(value,key) {
            var source = menu.tree.sources[key] = {
                value: key,
                label: value.name,
                isOptGroup: true,
                projects: {}
            };
            angular.forEach(value.projects,function(value,key) {
                var project = source.projects[key] = {
                    value: source.value+'\uF000'+key,
                    label: value.name,
                    repos: {}
                };
                angular.forEach(value.repos,function(value,key) {
                    var repo = project.repos[key] = {
                        value: key,
                        label: value.name,
                        branches: {}
                    };
                    angular.forEach(value.branches,function(value,key) {
                        repo.branches[key] = {
                            value: key,
                            label: value.displayId
                        };
                    });
                });
            });
            source.options = source.projects;
        });
    };

    settingsData.registerCallback("menu",function() {
        processTree(settingsData.raw);
    });

    var auto = 0;



    menu.initSources = function() {
        var sources = [];
        angular.forEach(menu.tree.sources,function(value,key) {
            sources.push({
                label: value.name,
                value: key
            });
        });

        if (auto && sources.length === 1) {
            sources[0].selected = true;
            menu.setSource(sources[0].value);
        }

        menu.sources = sources;
    };

    menu.reset = function() {
        menu.chosen = {
            source: null,
            project: null,
            repo: null,
            branch: null,
            sourceProject: ""
        };
        $timeout();
    };

    $scope.$watch("menu.chosen",function(newValue,oldValue) {
        if (newValue.sourceProject !== oldValue.sourceProject) {
            if (typeof newValue.sourceProject == "string") {
                var arr = newValue.sourceProject.split('\uF000');
                menu.chosen.source = arr[0];
                menu.chosen.project = arr[1];
                menu.chosen.repo = null;
                menu.chosen.branch = null;
            } else {
                menu.chosen = {
                    source: null,
                    project: null,
                    repo: null,
                    branch: null
                };
            }
        }

        if (
            menu.chosen.source != null &&
            menu.chosen.project != null &&
            menu.chosen.repo != null &&
            menu.chosen.branch != null
        ) {
            settingsData.setBranch(menu.chosen);
        } else {
            settingsData.resetBranch();
        }

    },true);

    $scope.$watch(function() {
        return settingsData.path;
    },function(path) {
        if (path === null) {
            menu.chosen.branch = null;
            $timeout();
        }}
    );

    $scope.$watch("menu.chosen.repo",function() {
        menu.chosen.branch = null;
    });


});