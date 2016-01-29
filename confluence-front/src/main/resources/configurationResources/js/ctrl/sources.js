angular.module("DoC_Config").controller("sourcesCtrl", function ($resource, urlProvider) {
    var vm = this;

    var Source = $resource(
        urlProvider.getRestUrlWithParams("sources") + ":id",
        {
            id: '@id'
        },
        {
            query: {
                method: 'GET',
                isArray: true
            },
            update: {
                transformRequest: function (data, headers) {
                    console.log(data);
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
            addFromApplinks: {
                url: urlProvider.getRestUrlWithParams("applinks","sources"),
                method: 'POST'
            }
        }
    );

    vm.sources = Source.query();

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
        vm.sources[index].$delete(function () {
            //console.log(vm.sources[index]);
        });
    };

    vm.addFromAppLinks = function () {
        console.log("");
        Source.addFromAppLinks();
    };
});