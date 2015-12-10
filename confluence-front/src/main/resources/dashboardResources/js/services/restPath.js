angular.module("DoC").factory("restPath",function() {
    var restPath = {
        origin: window.location.origin,
        get: function(localPath) {
            /*
            * TODO Hope you don't mind my own todo lists ;)
            * TODO Confluence URL/localhost recognition
            * TODO source/project/repo/branch service creation and integration
            *
            *
            * */
            var path = restPath.origin+"/"+localPath;
            path = 'http://46.101.240.138:8090/rest/autodoc/1.0/documentation/AUT/autodoc/master/'+localPath;
            return path;
        }
    };
    return restPath;
});