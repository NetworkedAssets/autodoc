angular.module("DoC").factory("urlProvider",function() {
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
            return this.getBaseUrl()+"/rest/autodoc/1.0/documentation/AUT/autodoc/master"+path;
        },
        getResourcesUrl: function(path) {
            if (!path) {
                path = "";
            }
            return this.getBaseUrl()+"/download/resources/com.networkedassets.autodoc.confluence-front:configuration-resources/configurationResources"+path;
        }
    }
    return urlProvider;
});