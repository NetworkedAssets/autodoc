angular.module("DoC_Config").controller("SourceCtrl", function ($rootScope, $resource, $timeout, urlService) {
    var vm = this;
    vm.loading = true;
    vm.addFromAppLinksSavingState = "ready";

    var Source = $resource(
        urlService.getRestUrlWithParams("sources") + ":id",
        {
            id: '@id'
        },
        {
            query: {
                method: 'GET',
                isArray: true
            },
            update: {
                transformRequest: function (data) {
                    var obj = {
                        id: data.id,
                        name: data.name,
                        url: data.url,
                        sourceType: data.sourceType
                    };
                    return JSON.stringify(obj);
                },
                method: 'PUT'
            },
            delete: {
                method: 'DELETE'
            },
            addFromAppLinks: {
                url: urlService.getRestUrlWithParams("applinks","sources"),
                method: 'POST'
            }
        }
    );

    vm.get = function() {
        vm.loading = true;
        vm.sources = Source.query();
        vm.sources.$promise.then(function() {
            vm.loading = false;
            vm.error = false;
        },function() {
            vm.loading = false;
            vm.error = true;
        });
    };

    vm.edit = function (index) {
        vm.sources[index].inEdit = true;
        vm.sources[index].originalUrl = vm.sources[index].url;
    };

    vm.save = function (index) {
        vm.sources[index].$update();
    };

    vm.revert = function (index) {
        vm.sources[index].inEdit = false;
        if (vm.sources[index].originalUrl) {
            vm.sources[index].url = vm.sources[index].originalUrl;
            delete vm.sources[index].originalUrl;
        }
    };

    vm.delete = function (index) {
        vm.sources[index].$delete();
    };

    vm.addFromAppLinks = function () {
        vm.addFromAppLinksSavingState = "saving";
        Source.addFromAppLinks().$promise.then(function() {
            vm.addFromAppLinksSavingState = "ready";
            vm.get();
        }, function() {
            vm.addFromAppLinksSavingState = "error";
            $timeout();
        });

    };

    $rootScope.$on("ConfluenceCredentials.ready",function(event,username) {
        if (username) {
            vm.noCredentials = false;
            vm.get();
        } else {
            vm.loading = false;
            vm.noCredentials = true;
        }
    });
});