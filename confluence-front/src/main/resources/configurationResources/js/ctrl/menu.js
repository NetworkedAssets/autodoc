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
    }

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
    }

    settingsData.registerCallback("menu",function() {
        processTree(settingsData.raw);

        fakeChosen();
        //menu.tree = settingsData.raw;
        //menu.initSources();
        /*setTimeout(function() {
            menu.setProject("AUT");
        },100);*/
    });

    var auto = 0;

    menu.initSources = function() {
        var sources = [];
        angular.forEach(menu.tree.sources,function(value,key) {
            console.log(key,value);
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





    }
    $scope.$watch("menu.chosen",function(newValue,oldValue) {
        console.log(menu.chosen);

        if (newValue.sourceProject !== oldValue.sourceProject && (typeof newValue.sourceProject == "string")) {
            var arr = newValue.sourceProject.split('\uF000');
            menu.chosen.source = arr[0];
            menu.chosen.project = arr[1];
            menu.chosen.repo = null;
            menu.chosen.branch = null;
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

    $scope.$watch("menu.chosen.sourceProject",function(sourceProject) {

    });

    $scope.$watch("menu.chosen.repo",function() {
        menu.chosen.branch = null;
    });


});