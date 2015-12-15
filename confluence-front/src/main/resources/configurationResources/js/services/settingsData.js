angular.module('DoC_Config').factory('settingsData', function($http,$rootScope,$timeout){
    var settings = {
        path: null,
        raw: {}
    };

    var callbacks = {};


    $http
        //.get("data/settings2.json")
        //.get("data/settings3.json")
        .get("http://46.101.240.138:8090/download/resources/com.networkedassets.autodoc.confluence-front:configuration-resources/configurationResources/data/settings3.json")
        //.get("http://46.101.240.138:8090/rest/autodoc/1.0/configuration/TEST/projects")
        .then(function(response) {
            var sources = {};
            response.data.sources.forEach(function(source) {
                source.verified = true;
                sources[source.slug] = source;
            });
            settings.raw = response.data;
            settings.raw.sources = sources;
            console.log("Original settings data: ",settings.raw.sources);
            //settings.setBranch(0,"AUT","autodoc","refs/heads/master");

            angular.forEach(callbacks,function(fn) {
                fn();
            });

        });

    settings.save = function() {
        $.post();
    }

    settings.get = function() {
        return this.getData();
    }

    $rootScope.$watch(function() {
        return settings;
    },function() {
        //console.log(settings);
    },true);

    settings.setBranch = function(chosen) {
        if (typeof chosen == "object") {
            var branchSettings = settings.raw.sources[chosen.source].projects[chosen.project].repos[chosen.repo].branches[chosen.branch];
            settings.components = branchSettings.components;
            settings.scheduledEvents = branchSettings.scheduledEvents;
            settings.listensTo = branchSettings.listensTo;
            settings.pageId = branchSettings.pageId;
            settings.path = chosen;
        }

    }

    settings.resetBranch = function() {
        settings.path = null;
        settings.components = null;
        settings.scheduledEvents = null;
        settings.listensTo = null;
    }

    settings.getData = function() {
        return settings.raw;
    }

    settings.registerCallback = function(name,fn) {
        if (typeof fn != "function") {
            console.error("Callback must be a function.");
        }

        callbacks[name] = fn;

    }

    return settings;
});