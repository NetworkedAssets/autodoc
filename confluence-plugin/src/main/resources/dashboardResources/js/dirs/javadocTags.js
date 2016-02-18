angular.module("DoC")
    .directive("javadocTags", function($sanitize, $compile, $stateParams, javadocEntities) {
        return {
            link: function(scope, element) {

                scope.$watch("content", function(value) {
                    if (value) {
                        var tagProcessors = [
                                function() {
                                    if (typeof scope.content == "string") {
                                        var regExp = /{\s*@link(plain)?\s+([^}\s#]+)?(#([^}\(]+(\([^)}]+\))?))?\s*([^}]+)?\s*}/gi;
                                        scope.content = scope.content.replace(regExp, function(match, $1, clazz, $3, element, elementSignature, label) {
                                                if (!clazz) {
                                                    // TODO Scrolling to specific method
                                                    return '<code>' + element + '</code>';
                                                }

                                                var pack = "";
                                                if ($stateParams.name && $stateParams.name.indexOf(".")) {
                                                    pack = $stateParams.name.substring(0, $stateParams.name.lastIndexOf("."));
                                                }

                                                var qualified;
                                                if (javadocEntities.existsByName(pack + "." + clazz)) {
                                                    qualified = pack + "." + clazz;
                                                } else {
                                                    qualified = clazz;
                                                }

                                                var html;

                                                if (!label) {
                                                    html = '<span class="type" doc-qname="\'' + qualified + '\'"></span>';
                                                } else {
                                                    html = '<a href="#" ui-sref="javadoc.entity({name:\'' + qualified + '\'})">' + label + '</a>';
                                                }
                                                if (element) {
                                                    html += '<code>#' + element + '</code>';
                                                }

                                                return html;

                                            }
                                        );
                                    }
                                },
                                function() {
                                    if (typeof scope.content == "string") {
                                        var regExp = /{@code\s*([^}]+)}/gi;
                                        scope.content = scope.content.replace(regExp, "<code>$1</code>");
                                    }
                                },
                                function() {
                                    if (typeof scope.content == "string") {
                                        var regExp = /{@inheritDoc}/gi;
                                        scope.content = scope.content.replace(regExp, "<javadoc-inherit-doc-tag></javadoc-inherit-doc-tag>");
                                    }
                                },
                                function() {
                                    /* includes {@literal text}*/
                                    var debug = false;
                                    if (!debug && typeof scope.content == "string") {
                                        var regExp = /{@[\w]+\s*([^}]+)}/gi;
                                        scope.content = scope.content.replace(regExp, "$1");
                                    }
                                }

                            ]
                            ;

                        tagProcessors.forEach(function(fn) {
                            fn.call();
                        });
                        element.html(scope.content);

                        $compile(element.contents())(scope);
                    }
                })
                ;
            },
            scope: {
                content: "=javadocTags"
            }
        }
            ;
    })
;