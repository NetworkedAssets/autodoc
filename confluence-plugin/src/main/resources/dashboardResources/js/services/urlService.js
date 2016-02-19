angular.module("DoC").factory("urlService",function(macroParams) {

    var getRestPathParamsString = function() {
        return "/"
            //+ urlService.encodeComponent(macroParams.get("source")) + "/"
            + urlService.encodeComponent(macroParams.get("project")) + "/"
            + urlService.encodeComponent(macroParams.get("repo")) + "/"
            + urlService.encodeComponent(macroParams.get("branch"));
    };

    var urlService = {
        getBaseUrl: function() {
            if (AJS.params && AJS.params.baseUrl) {
                return AJS.params.baseUrl;
            } else {
                return "http://atlas.networkedassets.net/confluence";
            }
        },
        getRestUrl: function() {
            var params = arguments;
            var paramString = "/";
            angular.forEach(params,function(param) {
                paramString += urlService.encodeComponent(param)+"/";
            });
            return this.getBaseUrl()+"/rest/doc/1.0/documentation"+getRestPathParamsString()+paramString;
        },
        getResourcesUrl: function(path) {
            if (!path) {
                path = "";
            }
            return this.getBaseUrl()+"/download/resources/com.networkedassets.autodoc.confluence-plugin:dashboard-resources/dashboardResources"+path;
        },
        encodeComponent: function(string) {
            return encodeURIComponent(encodeURIComponent(string));
        }
    };
    return urlService;
});