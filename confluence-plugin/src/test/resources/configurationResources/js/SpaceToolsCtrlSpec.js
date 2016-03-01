describe("SpaceToolsCtrl", function() {
    var $httpBackend, urlService;
    var spaceToolsScope, spaceTools;

    var setUpBackend = function() {
        $httpBackend.when('GET', urlService.getRestUrlWithParams("sources", "extended"))
            .respond([{
                "id": 9,
                "name": "AtlasDev Stash",
                "url": "http://atlas.networkedassets.net:7990",
                "sourceType": "STASH",
                "username": "admin",
                "password": null,
                "sourceExists": true,
                "credentialsCorrect": true,
                "nameCorrect": true,
                "sourceTypeCorrect": true,
                "correct": true,
                "projects": {
                    "AUT": {
                        "name": "Autodoc",
                        "key": "AUT",
                        "repos": {
                            "autodoc": {
                                "name": "autodoc",
                                "slug": "autodoc",
                                "branches": {
                                    "refs/heads/develop": {
                                        "displayId": "develop",
                                        "id": "refs/heads/develop",
                                        "latestCommit": "0f2b739834465a1390116ad96b3868a2374aba17",
                                        "listenTo": null,
                                        "scheduledEvents": []
                                    },
                                    "refs/heads/master": {
                                        "displayId": "master",
                                        "id": "refs/heads/master",
                                        "latestCommit": "6e92acdd6bb1fb081d7f61c800b335e97bb70ae1",
                                        "listenTo": "none",
                                        "scheduledEvents": []
                                    }
                                }
                            }
                        }
                    },
                    "GUAV": {
                        "name": "Guava",
                        "key": "GUAV",
                        "repos": {
                            "guava": {
                                "name": "guava",
                                "slug": "guava",
                                "branches": {
                                    "refs/heads/master": {
                                        "displayId": "master",
                                        "id": "refs/heads/master",
                                        "latestCommit": "d4e03e02a6026f113fe818dc0a9d7744e8eddd90",
                                        "listenTo": "git",
                                        "scheduledEvents": []
                                    }
                                }
                            }
                        }
                    },
                    "CON": {
                        "name": "Confluence",
                        "key": "CON",
                        "repos": {
                            "confluence": {
                                "name": "Confluence",
                                "slug": "confluence",
                                "branches": {
                                    "refs/heads/master": {
                                        "displayId": "master",
                                        "id": "refs/heads/master",
                                        "latestCommit": "22a03e5d32cba317116308b2f0b4c885626887f0",
                                        "listenTo": "schedule",
                                        "scheduledEvents": [{
                                            "periodic": false,
                                            "periodType": "DAY",
                                            "number": 1,
                                            "weekdays": {
                                                "mon": false,
                                                "tue": false,
                                                "wed": true,
                                                "thu": false,
                                                "fri": false,
                                                "sat": false,
                                                "sun": false
                                            },
                                            "oneTimeDate": "2016-03-02",
                                            "time": "15:30"
                                        }, {
                                            "periodic": true,
                                            "periodType": "DAY",
                                            "number": 2,
                                            "weekdays": {
                                                "mon": false,
                                                "tue": false,
                                                "wed": true,
                                                "thu": false,
                                                "fri": false,
                                                "sat": false,
                                                "sun": false
                                            },
                                            "time": "16:00"
                                        }, {
                                            "periodic": true,
                                            "periodType": "WEEK",
                                            "number": 2,
                                            "weekdays": {
                                                "mon": true,
                                                "tue": false,
                                                "wed": true,
                                                "thu": false,
                                                "fri": true,
                                                "sat": false,
                                                "sun": false
                                            },
                                            "time": "17:05"
                                        }]
                                    }
                                }
                            }
                        }
                    }
                },
                "appLinksId": "4bf086a7-4bef-39bc-9657-54143d7e0848",
                "hookKey": "com.networkedassets.atlasian.plugins.stash-postReceive-hook-plugin:postReceiveHookListener"
            }]);
    };

    beforeAll(function() {

    });

    beforeEach(angular.mock.module("DoC_Config"));

    beforeEach(angular.mock.inject(function($injector) {
        $httpBackend = $injector.get("$httpBackend");
        urlService = $injector.get("urlService");
        setUpBackend();
    }));

    beforeEach(angular.mock.inject(function($controller, $rootScope) {
        spaceToolsScope = $rootScope.$new();
        $controller('SpaceToolsCtrl as spaceTools', {$scope: spaceToolsScope});
        spaceTools = spaceToolsScope.spaceTools;
    }));

    it('source listened for both git and schedule should have listenTo = "both"', function() {
        $httpBackend.expectGET(urlService.getRestUrlWithParams("sources", "extended"));
        $httpBackend.flush();
        expect(spaceTools.raw.sources["9"].listenTo).toBe("both");
    });

    describe("SchedulesCtrl", function() {
        var schedulesScope, schedules;
        beforeEach(angular.mock.inject(function($controller, $rootScope) {
            schedulesScope = $rootScope.$new();
            schedulesScope.$parent = spaceToolsScope;
            schedules = $controller('SchedulesCtrl', {$scope: schedulesScope});
        }));
    });

});