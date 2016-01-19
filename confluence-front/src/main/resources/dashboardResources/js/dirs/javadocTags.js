angular.module("DoC")
    .directive("javadocTags",function($sanitize,$compile,$stateParams,javadocEntities) {
        return {
            link: function(scope,element) {

                scope.$watch("content",function(value) {
                    if (value) {
                        var tagProcessors = [
                            function() {
                                if (typeof scope.content == "string") {
                                    var regExp = /{@link(plain)? (([^}#]+)?(#([^}]+))?)}/gi;
                                    scope.content = scope.content.replace(regExp,function(match,$1,clazz,$3,element) {
                                        if (!clazz) {
                                            // TODO Scrolling to specific method
                                            return '<code>'+$1+'</code>';
                                        }
                                        var pack = "";
                                        if ($stateParams.name && $stateParams.name.indexOf(".")) {
                                            pack = $stateParams.name.substring(0,$stateParams.name.lastIndexOf("."));
                                        }

                                        if (javadocEntities.existsByName(pack+"."+clazz)) {
                                            //return '<a href="#" ui-sref="javadoc.entities({name:\''+clazz+'\'})">'+clazz+'</a>';
                                            return '<span class="type" doc-qname="\''+pack+"."+clazz+'\'"></span>';
                                        } else if (javadocEntities.existsByName(clazz)) {
                                            return '<span class="type" doc-qname="\''+clazz+'\'"></span>';
                                        } else {
                                            return $1;
                                        }

                                    });
                                }
                            },
                            function() {
                                if (typeof scope.content == "string") {
                                    var regExp = /{@code\s*([^}]+)}/gi;
                                    scope.content = scope.content.replace(regExp,"<code>$1</code>");
                                }
                            },
                            function() {
                                var debug = false;
                                if (!debug && typeof scope.content == "string") {
                                    var regExp = /{@[\w]+ ([^}]+)}/gi;
                                    scope.content = scope.content.replace(regExp,"$1");
                                }
                            }/*
                                {@inheritDoc}
                            */
                        ];

                        tagProcessors.forEach(function(fn) {
                            fn.call();
                        });
                        element.html(scope.content);

                        $compile(element.contents())(scope);
                    }

                });
            },
            scope: {
                content: "=javadocTags",
                myAttribute: "=myAttribute"
            }
        };
    });