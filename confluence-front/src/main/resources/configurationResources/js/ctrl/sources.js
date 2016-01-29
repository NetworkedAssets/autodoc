angular.module("DoC_Config").controller("sourcesCtrl", function ($resource, urlProvider) {
    var vm = this;

    var Source = $resource(
        urlProvider.getRestUrlWithParams(["applinks", "sources"]) + ":id",
        {
            id: '@id'
        },
        {
            query: {
                method: 'POST',
                isArray: true,
                transformResponse: function (data, headersGetter, status) {
                    return [
                        {
                            "id": 1,
                            "credentialsCorrect": false,
                            "hookKey": "com.networkedassets.atlasian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener",
                            "projects": {},
                            "sourceType": "STASH",
                            "url": "http://atlas.networkedassets.net:7990",
                            "nameCorrect": true,
                            "password": null,
                            "correct": false,
                            "username": null,
                            "sourceTypeCorrect": false,
                            "sourceExists": true,
                            "name": "AtlasDev Stash"
                        },
                        {
                            "id": 2,
                            "credentialsCorrect": false,
                            "hookKey": "com.networkedassets.atlassian.plugins.bitbucket-postReceive-hook-plugin:postReceiveHookListener",
                            "projects": {},
                            "sourceType": "BITBUCKET",
                            "url": "http://atlas.networkedassets.net:7991",
                            "nameCorrect": true,
                            "password": null,
                            "correct": false,
                            "username": null,
                            "sourceTypeCorrect": false,
                            "sourceExists": true,
                            "name": "Bitbucket"
                        }
                    ];
                }
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
            add: function () {
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
        Source.add();
    };
});