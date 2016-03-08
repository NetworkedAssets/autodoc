angular.module("DoC_Config").controller("MenuCtrl",function($scope,$element,$http,$timeout) {
    var menu = this;

    menu.chosen = {
        source: null,
        project: null,
        repo: null,
        branch: null
    };

    var spaceTools = $scope.$parent.spaceTools;

    var splitSourceProject = function(sourceProject) {
        var arr = sourceProject.split('\uF000');
        return {
            source: arr[0],
            project: arr[1]
        };
    };

    var joinSourceProject = function(source,project) {
        return source+'\uF000'+project;
    };

    var processTree = function(raw) {
        menu.tree = {
            sources: {}
        };
        angular.forEach(raw.sources,function(value,key) {
            var source = menu.tree.sources[key] = {
                value: key,
                label: value.name,
                isOptGroup: true,
                projects: {}
            };
            angular.forEach(value.projects,function(value,key) {
                var project = source.projects[key] = {
                    value: joinSourceProject(source.value,key),
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

    $scope.$on("spaceTools.ready",function() {
        processTree(spaceTools.raw);
    });

    $scope.$on("spaceTools.saved",function() {
        var select = $element.find("select");
        select.each(function() {
            $(this).auiSelect2("val",$(this).auiSelect2("val"));
        });
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

    menu.formatResult = function(data) {
        var element = $(data.element);

        var parseType = function() {
            var data = element.data();
            if (typeof data.projects == "object") {
                return "source";
            } else if (typeof data.repos == "object") {
                return "project";
            } else if (typeof data.branches == "object") {
                return "repo";
            } else {
                return "branch";
            }
        };

        var listenToStringToChar = function(string) {
            if (string == "both") {
                return "&#x25cf;";
            } else if (string == "git") {
                return "G";
            } else if (string == "schedule") {
                return "S";
            } else {
                return "";
            }
        };

        var id = data.id;
        var type = parseType();
        var chosenCopy = angular.copy(menu.chosen);
        if (type == "project") {
            var sourceProject = splitSourceProject(id);
            id = sourceProject.project;
            chosenCopy.source = sourceProject.source;
        }

        if (element.is("option")) {
            var listenTo = spaceTools.getListenTo(type,id,chosenCopy);
            if (listenTo !== "none") {
                return "<span class='doc_config-branch-menu-option listened'><span class='indicator'>"+listenToStringToChar(listenTo)+"</span> "+data.text+"</span>";
            } else {
                return "<span class='doc_config-branch-menu-option'><span class='indicator'></span> "+data.text+"</span>";
            }

        } else {
            return data.text;
        }

    };

    $scope.$watch("menu.chosen",function(newValue,oldValue) {
        if (newValue.sourceProject !== oldValue.sourceProject) {
            if (typeof newValue.sourceProject == "string") {
                var sourceProject = splitSourceProject(newValue.sourceProject);
                menu.chosen.source = sourceProject.source;
                menu.chosen.project = sourceProject.project;
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
            spaceTools.setBranch(menu.chosen);
        } else {
            spaceTools.resetBranch();
        }

    },true);

    $scope.$watch(function() {
        return spaceTools.path;
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