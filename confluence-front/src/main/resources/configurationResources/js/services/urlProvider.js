angular.module("DoC_Config").factory("urlProvider",function() {
    var urlProvider = {
        getRestUrl: function(path) {
            if (!path) {
                path = "";
            }
            return this.getBaseUrl()+"/rest/autodoc/1.0/configuration"+path;
        },
        getResourcesUrl: function(path) {
            if (!path) {
                path = "";
            }
            return this.getBaseUrl()+"/download/resources/com.networkedassets.autodoc.confluence-front:configuration-resources/configurationResources"+path;
        },
        isLocal: function() {
            return AJS.params && AJS.params.baseUrl;
        },
        getBaseUrl: function() {
            if (this.isLocal()) {
                return AJS.params.baseUrl;
            } else {
                return "http://atlas.networkedassets.net/confluence";
            }
        }
    }
    return urlProvider;
});