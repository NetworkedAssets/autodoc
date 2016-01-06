angular.module("DoC").factory("macroParams",function() {
    var params;

    if (AJS && AJS.params && typeof AJS.params.dashboardParamsJson == "string") {
        params = angular.fromJson(AJS.params.dashboardParamsJson);
    } else {
        params = {
            source: "1",
            project: "AUT",
            repo: "autodoc",
            branch: "master",
            javadoc: true,
            classDiagram: true
        };
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