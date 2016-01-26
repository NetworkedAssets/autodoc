angular.module("DoC").factory("macroParams",function() {
    var params;

    if (AJS && AJS.params && typeof AJS.params.dashboardParamsJson == "string") {
        params = angular.fromJson(AJS.params.dashboardParamsJson);
    } else {
        var guava = false;
        if (guava) {
            params = {
                source: "1",
                project: "GUAV",
                repo: "guava",
                branch: "refs/heads/master",
                javadoc: true,
                classDiagram: true,
                structureGraph: true
            };
        } else {
            params = {
                source: "1",
                project: "AUT",
                repo: "autodoc",
                branch: "refs/heads/master",
                javadoc: true,
                classDiagram: true,
                structureGraph: true
            };
        }
    }

    var macroParams = {
        get: function(key) {
            if (!key) {
                return this.getAll();
            } else {
                if (params[key]) {
                    return params[key];
                } else {
                    return null;
                }
            }
        },
        getAll: function() {
            return params;
        }
    };

    return macroParams;
});