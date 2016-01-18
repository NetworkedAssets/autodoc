angular.module("DoC").controller("javadocEntityCtrl",function($scope,$http,$sanitize,$stateParams,$rootScope,$timeout,$element,urlProvider,javadocEntities) {
    var vm = this;
    vm.loading = true;

    vm.$sanitize = $sanitize;

    var initSpinner = function() {
        AJS.$($element.find(".loadingSpinner")).spin("big");
    };

    vm.toggleDetails = function(method) {
        method.details.visible = !method.details.visible;
    };

    vm.packages = [];

    var parseBreadcrumb = function() {
        vm.packages = [];
        var qualified = "";
        angular.forEach(vm.entity.packageArray,function(pack) {
            if (!qualified) {
                qualified = pack;
            } else {
                qualified += "."+pack;
            }
            vm.packages.push({
                name: pack,
                qualified: qualified
            });
        });
        vm.packages.push({
            name: vm.entity.name,
            qualified: qualified+"."+name
        });
    };

    var init = function() {
        initSpinner();
        if (javadocEntities.isPackage($stateParams.name)) {
            vm.entity = new JavadocEntity(javadocEntities.getCopyByName($stateParams.name));
            parseBreadcrumb();
            vm.loading = false;
        } else {
            vm.loading = true;
            $http.get(urlProvider.getRestUrl("/JAVADOC/"+$stateParams.name)).then(function(response) {
                if (0) {
                    response.data = {
                        "name": "ConcurrentHashMap",
                        "qualified": "java.util.concurrent.ConcurrentHashMap",
                        "scope": "public",
                        "abstract": false,
                        "error": false,
                        "exception": false,
                        "externalizable": false,
                        "included": true,
                        "serializable": false,
                        "type": "class",
                        "comment": "Minimal emulation of {@link java.util.concurrent.ConcurrentHashMap}.\n Note that javascript intepreter is <a\n href=\"http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&t=DevGuideJavaCompatibility\">\n single-threaded</a>, it is essentially a {@link java.util.HashMap},\n implementing the new methods introduced by {@link ConcurrentMap}.",
                        "tag": [{
                            "name": "@author",
                            "text": "Hayward Chan"
                        }
                        ],
                        "generic": [{
                            "name": "K"
                        }, {
                            "name": "V"
                        }
                        ],
                        "class": {
                            "qualified": "java.util.AbstractMap",
                            "generic": [{
                                "qualified": "K"
                            }, {
                                "qualified": "V"
                            }
                            ]
                        },
                        "interface": [{
                            "qualified": "java.util.concurrent.ConcurrentMap",
                            "generic": [{
                                "qualified": "K"
                            }, {
                                "qualified": "V"
                            }
                            ]
                        }
                        ],
                        "constructor": [{
                            "name": "ConcurrentHashMap",
                            "signature": "()",
                            "qualified": "java.util.concurrent.ConcurrentHashMap",
                            "scope": "public",
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "modifier": ["public"]
                        }, {
                            "name": "ConcurrentHashMap",
                            "signature": "(int)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap",
                            "scope": "public",
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "initialCapacity",
                                "type": {
                                    "qualified": "int"
                                }
                            }
                            ],
                            "modifier": ["public"]
                        }, {
                            "name": "ConcurrentHashMap",
                            "signature": "(int, float)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap",
                            "scope": "public",
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "initialCapacity",
                                "type": {
                                    "qualified": "int"
                                }
                            }, {
                                "name": "loadFactor",
                                "type": {
                                    "qualified": "float"
                                }
                            }
                            ],
                            "modifier": ["public"]
                        }, {
                            "name": "ConcurrentHashMap",
                            "signature": "(java.util.Map<? extends K, ? extends V>)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap",
                            "scope": "public",
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "t",
                                "type": {
                                    "qualified": "java.util.Map",
                                    "generic": [{
                                        "qualified": "?",
                                        "wildcard": {
                                            "extendsBound": [{
                                                "qualified": "K"
                                            }
                                            ]
                                        }
                                    }, {
                                        "qualified": "?",
                                        "wildcard": {
                                            "extendsBound": [{
                                                "qualified": "V"
                                            }
                                            ]
                                        }
                                    }
                                    ]
                                }
                            }
                            ],
                            "modifier": ["public"]
                        }
                        ],
                        "method": [{
                            "name": "putIfAbsent",
                            "signature": "(K, V)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.putIfAbsent",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "generic": [
                                {name:"A"},
                                {name:"B"}
                            ],
                            "parameter": [{
                                "name": "key",
                                "type": {
                                    "qualified": "K"
                                }
                            }, {
                                "name": "value",
                                "type": {
                                    "qualified": "V"
                                }
                            }
                            ],
                            "return": {
                                "qualified": "V"
                            },
                            "modifier": ["public"]
                        }, {
                            "name": "remove",
                            "signature": "(java.lang.Object, java.lang.Object)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.remove",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "key",
                                "type": {
                                    "qualified": "java.lang.Object"
                                }
                            }, {
                                "name": "value",
                                "type": {
                                    "qualified": "java.lang.Object"
                                }
                            }
                            ],
                            "return": {
                                "qualified": "boolean"
                            },
                            "modifier": ["public"]
                        }, {
                            "name": "replace",
                            "signature": "(K, V, V)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.replace",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "key",
                                "type": {
                                    "qualified": "K"
                                }
                            }, {
                                "name": "oldValue",
                                "type": {
                                    "qualified": "V"
                                }
                            }, {
                                "name": "newValue",
                                "type": {
                                    "qualified": "V"
                                }
                            }
                            ],
                            "return": {
                                "qualified": "boolean"
                            },
                            "modifier": ["public"]
                        }, {
                            "name": "replace",
                            "signature": "(K, V)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.replace",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "key",
                                "type": {
                                    "qualified": "K"
                                }
                            }, {
                                "name": "value",
                                "type": {
                                    "qualified": "V"
                                }
                            }
                            ],
                            "return": {
                                "qualified": "V"
                            },
                            "modifier": ["public"]
                        }, {
                            "name": "containsKey",
                            "signature": "(java.lang.Object)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.containsKey",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "key",
                                "type": {
                                    "qualified": "java.lang.Object"
                                }
                            }
                            ],
                            "return": {
                                "qualified": "boolean"
                            },
                            "annotation": [{
                                "name": "Override",
                                "qualified": "java.lang.Override"
                            }
                            ],
                            "modifier": ["public"]
                        }, {
                            "name": "get",
                            "signature": "(java.lang.Object)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.get",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "key",
                                "type": {
                                    "qualified": "java.lang.Object"
                                }
                            }
                            ],
                            "return": {
                                "qualified": "V"
                            },
                            "annotation": [{
                                "name": "Override",
                                "qualified": "java.lang.Override"
                            }
                            ],
                            "modifier": ["public"]
                        }, {
                            "name": "put",
                            "signature": "(K, V)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.put",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "key",
                                "type": {
                                    "qualified": "K"
                                }
                            }, {
                                "name": "value",
                                "type": {
                                    "qualified": "V"
                                }
                            }
                            ],
                            "return": {
                                "qualified": "V"
                            },
                            "annotation": [{
                                "name": "Override",
                                "qualified": "java.lang.Override"
                            }
                            ],
                            "modifier": ["public"]
                        }, {
                            "name": "containsValue",
                            "signature": "(java.lang.Object)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.containsValue",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "value",
                                "type": {
                                    "qualified": "java.lang.Object"
                                }
                            }
                            ],
                            "return": {
                                "qualified": "boolean"
                            },
                            "annotation": [{
                                "name": "Override",
                                "qualified": "java.lang.Override"
                            }
                            ],
                            "modifier": ["public"]
                        }, {
                            "name": "remove",
                            "signature": "(java.lang.Object)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.remove",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "key",
                                "type": {
                                    "qualified": "java.lang.Object"
                                }
                            }
                            ],
                            "return": {
                                "qualified": "V"
                            },
                            "annotation": [{
                                "name": "Override",
                                "qualified": "java.lang.Override"
                            }
                            ],
                            "modifier": ["public"]
                        }, {
                            "name": "entrySet",
                            "signature": "()",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.entrySet",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "return": {
                                "qualified": "java.util.Set",
                                "generic": [{
                                    "qualified": "java.util.Map.Entry",
                                    "generic": [{
                                        "qualified": "K"
                                    }, {
                                        "qualified": "V"
                                    }
                                    ]
                                }
                                ]
                            },
                            "annotation": [{
                                "name": "Override",
                                "qualified": "java.lang.Override"
                            }
                            ],
                            "modifier": ["public"]
                        }, {
                            "name": "contains",
                            "signature": "(java.lang.Object)",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.contains",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "parameter": [{
                                "name": "value",
                                "type": {
                                    "qualified": "java.lang.Object"
                                }
                            }
                            ],
                            "return": {
                                "qualified": "boolean"
                            },
                            "modifier": ["public"]
                        }, {
                            "name": "elements",
                            "signature": "()",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.elements",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "return": {
                                "qualified": "java.util.Enumeration",
                                "generic": [{
                                    "qualified": "V"
                                }
                                ]
                            },
                            "modifier": ["public"]
                        }, {
                            "name": "keys",
                            "signature": "()",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.keys",
                            "scope": "public",
                            "abstract": false,
                            "final": false,
                            "included": true,
                            "native": false,
                            "synchronized": false,
                            "static": false,
                            "varArgs": false,
                            "return": {
                                "qualified": "java.util.Enumeration",
                                "generic": [{
                                    "qualified": "K"
                                }
                                ]
                            },
                            "modifier": ["public"]
                        }
                        ],
                        "field": [{
                            "name": "backingMap",
                            "qualified": "java.util.concurrent.ConcurrentHashMap.backingMap",
                            "scope": "private",
                            "volatile": false,
                            "transient": false,
                            "static": false,
                            "final": true,
                            "type": {
                                "qualified": "java.util.Map",
                                "generic": [{
                                    "qualified": "K"
                                }, {
                                    "qualified": "V"
                                }
                                ]
                            },
                            "modifier": ["private", "final"]
                        }
                        ],
                        "modifier": ["public"]
                    };
                }
                vm.entity = new JavadocEntity(response.data);
                parseBreadcrumb();
                vm.loading = false;
            });
        }
    };

    if (javadocEntities.isReady()) {
        init();
    } else {
        $rootScope.$on("javadocEntities.ready",function() {
            init();
        });
    }



});