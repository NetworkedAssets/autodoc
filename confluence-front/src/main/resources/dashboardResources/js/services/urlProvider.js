angular.module("DoC").factory("urlProvider",function(macroParams) {

    var getRestPathParamsString = function() {
        var pathString = "/"
            //+ urlProvider.encodeComponent(macroParams.get("source")) + "/"
            + urlProvider.encodeComponent(macroParams.get("project")) + "/"
            + urlProvider.encodeComponent(macroParams.get("repo")) + "/"
            + urlProvider.encodeComponent(macroParams.get("branch"));
        return pathString;
    };

    var urlProvider = {
        getBaseUrl: function() {

            if (AJS.params && AJS.params.baseUrl) {
                return AJS.params.baseUrl;
            } else {
                return "http://atlas.networkedassets.net/confluence";
            }
        },
        getRestUrl: function(path) {
            if (!path) {
                path = "";
            }
            return this.getBaseUrl()+"/rest/autodoc/1.0/documentation"+getRestPathParamsString()+path;
        },
        getResourcesUrl: function(path) {
            if (!path) {
                path = "";
            }
            return this.getBaseUrl()+"/download/resources/com.networkedassets.autodoc.confluence-front:configuration-resources/configurationResources"+path;
        },
        encodeComponent: function(string) {
            return encodeURIComponent(encodeURIComponent(string));
        }
    }
    return urlProvider;
});