angular.module("DoC").factory("macroParams",function() {
    var params;

    if (AJS && AJS.params && typeof AJS.params.dashboardParamsJson == "string") {
        params = angular.fromJson(AJS.params.dashboardParamsJson);
        angular.forEach(params,function(value,key) {
            if (value === "false") { // let's hope there will never be a branch named like this...
                params[key] = false;
            } else if (value === "true") { // or this...
                params[key] = true;
            }
        });
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
                branch: "refs/heads/develop",
                javadoc: true,
                classDiagram: true,
                structureGraph: true
            };
        }
    }

    return {
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
});